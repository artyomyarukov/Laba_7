package server.connectivity;


import common.utility.SerializationUtils;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Отправляет ответы клиенту через UDP в неблокирующем режиме (NIO)
 */

public class ServerResponseSender {
    private static final Logger logger = Logger.getLogger(ServerResponseSender.class.getName());
    private final DatagramChannel channel;

    public ServerResponseSender(DatagramChannel channel) {
        this.channel = channel;
    }
    /**
     * Отправляем ответ клиенту
     */

    public void sendResponse(Object response, InetSocketAddress clientAddress) throws IOException {
        byte[] responseData = SerializationUtils.serialize(response);
        ByteBuffer buffer = ByteBuffer.wrap(responseData);
        channel.send(buffer, clientAddress);
        logger.fine("Ответ отправлен клиенту " + clientAddress);
    }
}