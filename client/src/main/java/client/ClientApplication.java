package client;

import client.input.AbstractInput;
import client.input.ConsoleInput;

import client.ClientInputProcessor;

import common.commands.CommandDefinition;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.UUID;


/*
основная логика клиента
 */


public class ClientApplication {
    private static final String SERVER_IP = "192.168.10.80";
    private static final Logger logger = Logger.getLogger(ClientApplication.class.getName());

    private final InetAddress ip;
    private final int port;
    public static final UUID CLIENT_ID = UUID.randomUUID();
    private Collection<CommandDefinition> commandDefinitions;

    public ClientApplication(InetAddress ip, int port) throws UnknownHostException {
        this.ip =InetAddress.getByName(SERVER_IP);;
        this.port = port;
    }
    static {
        // Настройка формата вывода логов
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$s: %5$s %n");
        System.setProperty("file.encoding", "UTF-8");

    }


        public void start() {
            logger.log(Level.INFO, "Подключение к серверу {0}:{1}", new Object[]{ip, port});

            ClientConnector clientConnector = null;

            try {
                clientConnector = new ClientConnector(ip, port);
                commandDefinitions = clientConnector.sendHello();
            } catch (ClientConnector.ServerUnavailableException e) {
                logger.log(Level.SEVERE, "Ошибка подключения: " + e.getMessage());
                System.exit(1);
                return;
            }


            try (AbstractInput input = new ConsoleInput()) {
                ClientInputProcessor inputProcessor = new ClientInputProcessor(
                        commandDefinitions,
                        clientConnector
                );
                inputProcessor.processInput(input, true);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка в работе клиента " + e.getMessage());
                System.exit(2);
            }
        }
    }


