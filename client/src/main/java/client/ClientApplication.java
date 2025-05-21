package client;

import client.input.AbstractInput;
import client.input.ConsoleInput;

import client.ClientInputProcessor;

import common.commands.CommandDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Collection;
import java.util.UUID;

public class ClientApplication {
    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);
    private final InetAddress ip;
    private final int port;
    public static final UUID CLIENT_ID = UUID.randomUUID();
    private Collection<CommandDefinition> commandDefinitions;

    public ClientApplication(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        client.ClientConnector clientConnector = new client.ClientConnector(ip, port);
        commandDefinitions = clientConnector.sendHello();

        try (AbstractInput input = new ConsoleInput()) {
            ClientInputProcessor inputProcessor = new ClientInputProcessor(commandDefinitions, clientConnector);
            inputProcessor.processInput(input, true);
        } catch (Exception e) {
            logger.error("Ошибка: ", e);
        }
    }
}