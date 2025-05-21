package server;

import common.collection.City;
import common.commands.CommandDefinition;
import common.utility.CommandRequest;
import common.utility.CommandWithArgument;
import server.requets_processing.RequestHandler;
import server.utility.*;
import server.collection.CityService;
import server.commands.CommandRegistry;
import server.commands.IHistoryProvider;
import server.utility.HistoryList;
import server.utility.IdCounter;
import common.utility.SerializationUtils;

import java.util.logging.Logger;
import java.util.logging.Level;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import static java.lang.Math.max;

public class ServerApplication implements IHistoryProvider {
    private static final Logger logger = Logger.getLogger(ServerApplication.class.getName());
    private static final int BUFFER_SIZE = 65536;
    private static final int SELECT_TIMEOUT = 1000;

    static {
        // Настройка формата вывода логов
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
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

    public void start() {
        logger.log(Level.INFO, "Сервер запущен на порту {0} (неблокирующий режим UDP)", port);

        try {
            while (!Thread.interrupted()) {
                int readyChannels = selector.select(SELECT_TIMEOUT);
                if (readyChannels == 0) continue;

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iter = selectedKeys.iterator();

                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isReadable()) {
                        handleIncomingDatagram();
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка в основном цикле сервера", e);
        } finally {
            closeResources();
        }
    }

    private void init() {
        try {
            logger.log(Level.INFO, "Инициализация сервера...");
            initStorage();
            this.commandRegistry = new CommandRegistry(cityService, this);
            this.requestHandler = new RequestHandler(commandRegistry);
            initNetwork();
            handleSaveOnTerminate();
            logger.log(Level.INFO, "Инициализация завершена успешно");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Критическая ошибка инициализации", e);
            System.exit(1);
        }
    }

    private void initNetwork() throws IOException {
        logger.log(Level.FINE, "Настройка сетевого взаимодействия...");
        this.channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));

        this.selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
        logger.log(Level.INFO, "Сетевой канал открыт на порту {0}", port);
    }

    private void handleIncomingDatagram() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

            if (clientAddress != null) {
                logger.log(Level.FINE, "Получен пакет от {0}:{1}",
                        new Object[]{clientAddress.getHostString(), clientAddress.getPort()});

                buffer.flip();
                Object request = SerializationUtils.deserialize(buffer.array());
                logger.log(Level.FINER, "Десериализация запроса завершена");

                Object response = requestHandler.onReceive(request);
                logger.log(Level.FINE, "Запрос обработан, формирование ответа");

                byte[] responseData = SerializationUtils.serialize(response);
                channel.send(ByteBuffer.wrap(responseData), clientAddress);
                logger.log(Level.FINER, "Ответ отправлен клиенту");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Ошибка обработки датаграммы", e);
        }
    }

    private void initStorage() {
        logger.log(Level.INFO, "Загрузка коллекции из файла: {0}", filename);
        XMLReader xmlReader =  new XMLReader() ;
        Hashtable<String, City> map = new Hashtable<>();
        HashSet<Integer> setOfId = new HashSet<>();

        try {
            map = xmlReader.loadFromXML(filename);
            logger.log(Level.INFO, "Успешно загружено {0} элементов", map.size());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка загрузки коллекции из файла", e);
            System.exit(1);
        }

        for (City city : map.values()) {
            IdCounter.setId(max(city.getId(), IdCounter.getId()));
            setOfId.add( city.getId());
        }

        if (setOfId.size() < map.size()) {
            logger.log(Level.WARNING, "Обнаружены дубликаты ID - коллекция очищена");
            map.clear();
            IdCounter.setId(0);
        }

        cityService = new CityService(map, filename);
        logger.log(Level.CONFIG, "Сервис коллекции инициализирован");
    }

    private void handleSaveOnTerminate() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.log(Level.INFO, "Завершение работы сервера...");
            CommandWithArgument command = new CommandWithArgument(CommandDefinition.save, "");
            requestHandler.getCommandController().handle(new CommandRequest(command, null, null));
            logger.log(Level.INFO, "Коллекция сохранена в файл");
            closeResources();
        }));
    }

    private void closeResources() {
        try {
            if (selector != null) {
                selector.close();
                logger.log(Level.FINE, "Selector закрыт");
            }
            if (channel != null) {
                channel.close();
                logger.log(Level.FINE, "Сетевой канал закрыт");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Ошибка при освобождении ресурсов", e);
        }
    }

    @Override
    public HistoryList getHistoryByClientID(String clientID) {
        logger.log(Level.FINEST, "Запрос истории команд для клиента {0}", clientID);
        return requestHandler.getHistoryByClientID(clientID);
    }
}