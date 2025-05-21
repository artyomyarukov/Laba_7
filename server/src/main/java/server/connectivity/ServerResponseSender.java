package server.connectivity;

import lombok.extern.slf4j.Slf4j;
import common.utility.SerializationUtils;

import java.io.IOException;
import java.net.*;

/**
 * Отправляет ответы клиенту через UDP с обработкой ошибок
 */
@Slf4j
public class ServerResponseSender {
    private final DatagramSocket socket;

    public ServerResponseSender(DatagramSocket socket) {
        this.socket = socket;
    }

    public void sendResponse(Object response, InetAddress clientIp, int clientPort) {
        try {
            byte[] responseData = SerializationUtils.serialize(response);
            DatagramPacket packet = new DatagramPacket(
                    responseData,
                    responseData.length,
                    clientIp,
                    clientPort
            );

            socket.send(packet);
            log.debug("Ответ отправлен клиенту {}:{}", clientIp, clientPort);

        } catch (IOException e) {
            log.error("Ошибка отправки ответа клиенту {}:{} - {}",
                    clientIp, clientPort, e.getMessage());
        }
    }
}