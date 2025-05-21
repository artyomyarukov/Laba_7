package server.utility;

public interface RequestHandler {
    byte[] handle(byte[] requestBytes);
}