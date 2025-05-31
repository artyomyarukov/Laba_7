package client;

import common.commands.CommandDefinition;
import common.utility.CommandRequest;
import common.utility.ExecutionResponse;
import common.utility.SerializationUtils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static common.utility.SerializationUtils.BUFFER_SIZE;
import static common.utility.SerializationUtils.INSTANCE;
import static client.ClientApplication.CLIENT_ID;


@AllArgsConstructor
public class ClientConnector {
    private static final Logger logger = LoggerFactory.getLogger(ClientConnector.class);
    public static final int INTER_WAIT_SLEEP_TIMEOUT = 50;
    private final InetAddress ip;
    private final int port;
    private static final int RETRY_COUNT = 10;
    private static final int WAIT_TIMEOUT = 1000 * 1000;
    InetSocketAddress serverAddress;

    public ClientConnector(InetAddress ip, int port) {
        serverAddress = new InetSocketAddress(ip, port);
        this.ip = ip;
        this.port = port;
    }
    private Object sendData(Object obj) {
        int attempts = 0;
        long startTime = System.currentTimeMillis();

        while (attempts < RETRY_COUNT) {
            try (DatagramChannel clientChannel = DatagramChannel.open()) {
                // 1. Отправка данных
                byte[] data = INSTANCE.serialize(obj);
                clientChannel.send(ByteBuffer.wrap(data), serverAddress);

                // 2. Ожидание ответа
                ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                InetSocketAddress sourceAddress = waitForResponse(clientChannel, buffer);

                if (sourceAddress != null) {
                    return INSTANCE.deserialize(buffer.array());
                }

            } catch (IOException e) {
                attempts++;
                logger.info("Попытка {}/{} не удалась: {}", attempts, RETRY_COUNT, e.getMessage());

                // 3. Проверка общего времени
                if (System.currentTimeMillis() - startTime > WAIT_TIMEOUT) {
                    break;
                }

                // 4. Пауза перед повторной попыткой
                try {
                    Thread.sleep(INTER_WAIT_SLEEP_TIMEOUT * attempts); // Экспоненциальная задержка
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        throw new IllegalStateException("Сервер недоступен после " + attempts + " попыток");
    }




    private static InetSocketAddress waitForResponse(DatagramChannel clientChannel, ByteBuffer receiveBuffer) throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        InetSocketAddress sourceAddress = null;

        while ((System.currentTimeMillis() - startTime) < WAIT_TIMEOUT) {
            receiveBuffer.clear();
            sourceAddress = (InetSocketAddress) clientChannel.receive(receiveBuffer);

            if (sourceAddress != null) {
                break;
            }

            Thread.sleep(INTER_WAIT_SLEEP_TIMEOUT);
        }
        return sourceAddress;
    }

    public Collection<CommandDefinition> sendHello() throws IllegalAccessException {
        Object commandDefinitions = sendData(CLIENT_ID);
        if (commandDefinitions instanceof Collection) {
            return (Collection<CommandDefinition>) commandDefinitions;
        }
        throw new IllegalArgumentException("Неверный ответ от сервера на команду приветствия: " + commandDefinitions);
    }

    public ExecutionResponse sendCommand(CommandRequest commandRequest) throws IllegalAccessException {
        Object commandResponse = sendData(commandRequest);
        if (commandResponse instanceof ExecutionResponse) {
            return (ExecutionResponse) commandResponse;
        }
        throw new IllegalArgumentException("Неверный ответ от сервера на команду: " + commandResponse);
    }


}