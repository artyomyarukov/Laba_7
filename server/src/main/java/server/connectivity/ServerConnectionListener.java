package server.connectivity;

import lombok.extern.slf4j.Slf4j;
import common.utility.SerializationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Принимает UDP-сообщения от клиентов (неблокирующий режим)
 */
@Slf4j
public class ServerConnectionListener {
    private final DatagramSocket socket;
    private final int bufferSize;

    public ServerConnectionListener(int port, int bufferSize) throws IOException {
        this.socket = new DatagramSocket(port);
        this.bufferSize = bufferSize;
        log.info("Сервер запущен на порту {}", port);


    }

    public IncomingMessage receiveMessage() throws IOException {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        log.debug("Ожидание сообщения...");
        socket.receive(packet); // Блокирующий прием (можно заменить на NIO)

        Object data = SerializationUtils.deserialize(packet.getData());
        log.info("Получено сообщение от {}:{}", packet.getAddress(), packet.getPort());

        return new IncomingMessage(data, packet.getAddress(), packet.getPort());
    }

    public void close() {
        socket.close();
        log.info("Серверный сокет закрыт");
    }
}



