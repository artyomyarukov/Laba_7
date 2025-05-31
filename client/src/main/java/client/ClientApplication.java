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
            logger.info("Подключение к серверу {}:{}", ip.getHostAddress(), port);

            ClientConnector clientConnector = null; // Выносим объявление наружу

            try {
                clientConnector = new ClientConnector(ip, port);
                commandDefinitions = clientConnector.sendHello();
            } catch (ClientConnector.ServerUnavailableException e) {
                logger.error("Ошибка подключения: {}", e.getMessage());
                System.exit(1);
                return;
            }

            // Теперь clientConnector доступен в этом scope
            try (AbstractInput input = new ConsoleInput()) {
                ClientInputProcessor inputProcessor = new ClientInputProcessor(
                        commandDefinitions,
                        clientConnector // Теперь переменная доступна
                );
                inputProcessor.processInput(input, true);
            } catch (Exception e) {
                logger.error("Ошибка в работе клиента: ", e);
                System.exit(2);
            }
        }
    }


