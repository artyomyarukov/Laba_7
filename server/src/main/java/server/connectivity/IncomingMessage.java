package server.connectivity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetAddress;
@AllArgsConstructor
@Data
public class IncomingMessage {
    private final byte[] data;
    private final InetAddress clientIp;
    private final int clientPort;
}