package server.connectivity;

import lombok.extern.slf4j.Slf4j;
import common.utility.SerializationUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.logging.Logger;

/**
 * Принимает UDP-сообщения от клиентов (неблокирующий режим)
 */

public class ServerConnectionListener {
    private static final Logger logger = Logger.getLogger(ServerConnectionListener.class.getName());
    private final DatagramChannel channel;
    private final int bufferSize;

    public ServerConnectionListener(int port, int bufferSize) throws IOException {
        this.channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress(port));
        this.bufferSize = bufferSize;
        logger.info("Серверный канал инициализирован на порту " + port);
    }

    public IncomingMessage receiveMessage() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);

        if (clientAddress != null) {
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            return new IncomingMessage(data, clientAddress.getAddress(), clientAddress.getPort());
        }
        return null;
    }
    public DatagramChannel getChannel() {
        return channel;
    }

    public void close() throws IOException {
        channel.close();
        logger.info("Серверный канал закрыт");
    }

}



