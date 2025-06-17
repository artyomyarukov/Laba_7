package server;

import common.collection.City;
import common.commands.CommandDefinition;
import common.utility.CommandRequest;
import common.utility.CommandWithArgument;
import server.connectivity.IncomingMessage;
import server.connectivity.ServerConnectionListener;
import server.connectivity.ServerResponseSender;
import server.requets_processing.RequestHandler;
import server.utility.*;
import server.collection.CityService;
import server.commands.CommandRegistry;
import server.commands.IHistoryProvider;
import server.utility.HistoryList;
import server.utility.IdCounter;
import common.utility.SerializationUtils;

import java.nio.channels.ClosedSelectorException;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import static java.lang.Math.max;

public class ServerApplication implements IHistoryProvider {
    private static final Logger logger = Logger.getLogger(ServerApplication.class.getName());
    private static final int BUFFER_SIZE = 65536;
    private static final int SELECT_TIMEOUT = 1000;
    private volatile boolean isRunning = true;
    private ServerConnectionListener connectionListener;
    private ServerResponseSender responseSender;

    static {
        // Настройка формата вывода логов
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$s: %5$s %n");
        System.setProperty("file.encoding", "UTF-8");

    }

    private final String filename;
    private final int port;
    private CityService cityService;
    private CommandRegistry commandRegistry;
    private RequestHandler requestHandler;
    private DatagramChannel channel;
    private Selector selector;

    public ServerApplication(String file, int port) {
        this.filename = file;
        this.port = port;
        init();
    }


    private void startConsoleThread() {
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Сервер запущен. Введите 'exit' для остановки:");

            while (isRunning) {
                try {
                    String input = scanner.nextLine().trim();
                    if ("exit".equalsIgnoreCase(input)) {
                        logger.log(Level.INFO, "Получена команда на завершение работы");
                        isRunning = false;
                        selector.wakeup();  // Прерываем select() для немедленного выхода
                        break;
                    }
                    if("save".equalsIgnoreCase(input)) {
                        try{
                            CommandWithArgument command = new CommandWithArgument(CommandDefinition.save, filename);
                            requestHandler.getCommandController().handle(new CommandRequest(command, null, null));
                            logger.info("Коллекция сохранена");
                        }
                        catch(Exception e){
                            logger.log(Level.SEVERE, "Ошибка при сохранении коллекции", e)
                            ;
                        }

                    }


                } catch (Exception e) {
                    logger.log(Level.WARNING, "Ошибка чтения консоли", e);
                }
            }
            scanner.close();
        });
        consoleThread.setDaemon(false);
        consoleThread.start();
    }

    private void gracefulShutdown() {
        try {
            logger.log(Level.INFO, "Завершение работы сервера...");

            // Сохраняем данные
            CommandWithArgument command = new CommandWithArgument(CommandDefinition.save, filename);
            requestHandler.getCommandController().handle(new CommandRequest(command, null, null));

            // Закрываем ресурсы
            if (selector != null) {
                selector.close();
            }
            if (channel != null) {
                channel.close();
            }

            logger.log(Level.INFO, "Сервер корректно остановлен");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при завершении работы", e);
        }
    }

    public void start() {
        startConsoleThread(); // Запускаем консоль до основного цикла
        logger.log(Level.INFO, "Сервер запущен на порту {0}", port);

        try {
            while (isRunning) {
                try {
                    int readyChannels = selector.select(SELECT_TIMEOUT);
                    if (!isRunning) break;

                    if (readyChannels > 0) {
                        processSelectedKeys();
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Ошибка в основном цикле", e);
                }
            }
        } finally {
            gracefulShutdown();
        }
    }

    private void processSelectedKeys() {
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iter = selectedKeys.iterator();

        while (iter.hasNext() && isRunning) {
            SelectionKey key = iter.next();
            iter.remove();

            if (key.isReadable()) {
                handleIncomingDatagram();
            }
        }
    }

    private void init() {
        try {

            logger.log(Level.INFO, "инициализация сервера...");
            initStorage();
            this.commandRegistry = new CommandRegistry(cityService, this);
            this.requestHandler = new RequestHandler(commandRegistry);
            initNetwork();
            handleSaveOnTerminate();
            logger.log(Level.INFO, "инициализация завершена успешно");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Критическая ошибка инициализации", e);
            System.exit(1);
        }
    }

    private void initNetwork() throws IOException {
        logger.log(Level.FINE, "Настройка сетевого взаимодействия...");
        this.connectionListener = new ServerConnectionListener(port, BUFFER_SIZE);
        this.responseSender = new ServerResponseSender(connectionListener.getChannel());

        // Получаем канал из connectionListener
        this.channel = connectionListener.getChannel();
        this.selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
    }


    private void handleIncomingDatagram() {
        try {
            IncomingMessage message = connectionListener.receiveMessage();
            if (message != null) {
                Object request = SerializationUtils.deserialize(message.getData());
                InetSocketAddress clientAddress = new InetSocketAddress(
                        message.getClientIp(), message.getClientPort());

                logger.info("Получен запрос от " + clientAddress + ": " + request.toString());
                Object response = requestHandler.onReceive(request);

                responseSender.sendResponse(response, clientAddress);
                logger.log(Level.FINER, "Ответ отправлен клиенту");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка обработки датаграммы", e);
        }
    }

    private void initStorage() {
        logger.log(Level.INFO, "Загрузка коллекции из файла: {0}", filename);
        XMLReader xmlReader = new XMLReader();
        HashMap<String, City> map = new HashMap<>();
        HashSet<Long> setOfId = new HashSet<>();

        try {
            map = xmlReader.loadFromXML(filename);

            if (map.isEmpty()) {
                logger.log(Level.INFO, "Коллекция пуста (файл {0} новый или не содержит данных)", filename);
            } else {
                logger.log(Level.INFO, "Успешно загружено {0} элементов", map.size());
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "ФАТАЛЬНАЯ ОШИБКА: Не удалось создать/загрузить файл коллекции", e);
            logger.log(Level.SEVERE, "Подробности: {0}", e.getMessage());
            logger.log(Level.SEVERE, "Сервер будет остановлен");
            System.exit(1);
        }

        // Обработка загруженной коллекции
        for (City city : map.values()) {
            IdCounter.setId(max(city.getId(), IdCounter.getId()));
            setOfId.add(city.getId());
        }

        if (!map.isEmpty() && setOfId.size() < map.size()) {
            logger.log(Level.WARNING, "Обнаружены дубликаты ID - коллекция очищена");
            map.clear();
            IdCounter.setId(0);
        }

        cityService = new CityService(map, filename);
        logger.log(Level.CONFIG, "Сервис коллекции инициализирован");
    }


    private void handleSaveOnTerminate() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.log(Level.INFO, "Завершение работы сервера...");
                CommandWithArgument command = new CommandWithArgument(CommandDefinition.save, filename);
                requestHandler.getCommandController().handle(new CommandRequest(command, null, null));
                logger.log(Level.INFO, "Коллекция сохранена в файл: {0}", filename);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка при сохранении коллекции", e);
            } finally {
                closeResources();
            }
        }));
    }

    private void closeResources() {
        try {
            // Закрытие сетевых ресурсов
            if (connectionListener != null) {
                connectionListener.close(); // Закрываем DatagramChannel
            }

            // Закрытие Selector
            if (selector != null && selector.isOpen()) {
                selector.close();
                logger.log(Level.FINE, "Selector закрыт");
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка при освобождении ресурсов", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Неожиданная ошибка при закрытии ресурсов", e);
        }

    }

    @Override
    public HistoryList getHistoryByClientID(String clientID) {
        logger.log(Level.FINEST, "Запрос истории команд для клиента {0}", clientID);
        return requestHandler.getHistoryByClientID(clientID);
    }
}