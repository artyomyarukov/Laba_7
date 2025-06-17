package client;

import common.commands.CommandDefinition;
import common.utility.CommandRequest;
import common.utility.ExecutionResponse;
import common.utility.SerializationUtils;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
отправка/получение данных
 */
@AllArgsConstructor
public class ClientConnector {
    private static final Logger log = Logger.getLogger(ClientConnector.class.getName());
    private static final int MAX_RETRIES = 3;
    private static final int RESPONSE_TIMEOUT_MS = 200;
    private static final int RETRY_DELAY_MS = 500;
    private static final int BUFFER_SIZE = 65536;

    static {
        // Настройка формата вывода логов
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$s: %5$s %n");
        System.setProperty("file.encoding", "UTF-8");

    }

    private final InetSocketAddress serverAddress;

    public ClientConnector(InetAddress ip, int port) {
        this.serverAddress = new InetSocketAddress(ip, port);
        log.log(Level.INFO, "Создан коннектор к серверу {0}:{1}",
                new Object[]{ip.getHostAddress(), port});
    }

    public ExecutionResponse sendCommand(CommandRequest request) throws ServerUnavailableException {
        log.log(Level.FINE, "Отправка команды: {0}", request);
        try {
            Object response = sendDataWithRetry(request);
            if (response instanceof ExecutionResponse) {
                return (ExecutionResponse) response;
            }
            throw new ServerUnavailableException("Неверный формат ответа сервера");
        } catch (ClassCastException e) {
            throw new ServerUnavailableException("Ошибка приведения типа ответа", e);
        }
    }

    private Object sendDataWithRetry(Object data) throws ServerUnavailableException {
        int attempt = 0;
        IOException lastException = null;

        System.out.println("=== Попытка подключения ===");

        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                System.out.printf("Попытка %d из %d...%n", attempt, MAX_RETRIES);
                Object result = sendData(data);
                System.out.println("Успешное подключение!");
                return result;
            } catch (SocketTimeoutException e) {
                lastException = e;
                System.err.printf("Таймаут! (попытка %d) Сервер не ответил за %d мс%n",
                        attempt, RESPONSE_TIMEOUT_MS);
            } catch (IOException e) {
                lastException = e;
                System.err.printf("Ошибка сети: %s (попытка %d)%n",
                        e.getMessage(), attempt);
            }

            if (attempt < MAX_RETRIES) {
                try {
                    System.out.printf("Пауза %d мс перед повторной попыткой...%n", RETRY_DELAY_MS);
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    System.err.println("Операция прервана пользователем!");
                    throw new ServerUnavailableException("Операция прервана");
                }
            }
        }

        String errorMsg = String.format("Сервер %s недоступен после %d попыток",
                serverAddress, MAX_RETRIES);
        System.err.println(errorMsg);
        throw new ServerUnavailableException(errorMsg, lastException);
    }

    private Object sendData(Object data) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        try {
            channel.configureBlocking(false);
            channel.socket().setSoTimeout(RESPONSE_TIMEOUT_MS);

            log.log(Level.FINE, "Отправка данных на сервер...");
            byte[] bytes = SerializationUtils.INSTANCE.serialize(data);
            channel.send(ByteBuffer.wrap(bytes), serverAddress);

            log.log(Level.FINE, "Ожидание ответа (таймаут {0} мс)...", RESPONSE_TIMEOUT_MS);
            System.out.println("Ожидаю ответ от сервера...");

            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < RESPONSE_TIMEOUT_MS) {
                InetSocketAddress responseAddress = (InetSocketAddress) channel.receive(buffer);
                if (responseAddress != null) {
                    System.out.println("Ответ получен!");
                    buffer.flip();
                    return SerializationUtils.INSTANCE.deserialize(buffer.array());
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Операция прервана");
                }
            }
            throw new SocketTimeoutException("Сервер не ответил в течение " + RESPONSE_TIMEOUT_MS + " мс");
        } finally {
            channel.close();
        }
    }

    public Collection<CommandDefinition> sendHello() throws ServerUnavailableException {
        System.out.println("=== Попытка подключения ===");

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            System.out.printf("Попытка %d из %d%n", attempt, MAX_RETRIES);

            try {
                Object response = sendData(ClientApplication.CLIENT_ID);
                if (response instanceof Collection) {
                    System.out.println("Подключение успешно установлено!");
                    return (Collection<CommandDefinition>) response;
                }
                log.log(Level.SEVERE, "Неверный формат ответа сервера");
            } catch (SocketTimeoutException e) {
                System.err.println("Таймаут: " + e.getMessage());
                log.log(Level.WARNING, "Таймаут подключения (попытка {0}): {1}",
                        new Object[]{attempt, e.getMessage()});
            } catch (IOException e) {
                System.err.println("Ошибка сети: " + e.getMessage());
                log.log(Level.WARNING, "Ошибка сети (попытка {0}): {1}",
                        new Object[]{attempt, e.getMessage()});
            }

            if (attempt < MAX_RETRIES) {
                try {
                    System.out.printf("Пауза %d мс перед следующей попыткой...%n", RETRY_DELAY_MS);
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ServerUnavailableException("Подключение прервано");
                }
            }
        }

        String errorMsg = "Сервер недоступен после " + MAX_RETRIES + " попыток";
        System.err.println(errorMsg);
        throw new ServerUnavailableException(errorMsg);
    }

    public static class ServerUnavailableException extends Exception {
        public ServerUnavailableException(String message) {
            super(message);
        }
        public ServerUnavailableException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}