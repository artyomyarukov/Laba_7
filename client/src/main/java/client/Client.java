package client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public final class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args) throws Exception {
        logger.info("Запуск клиента...");
        logger.debug("Параметры запуска: {}", Arrays.toString(args));

        try {
            if (args.length != 1) {
                logger.error("Передано неверное количество аргументов. Проверьте, что вы указали хост и порт, разделив их двоеточием");
                System.exit(-1);
            }

            String hostWithPort = args[0];
            String[] split = hostWithPort.split(":");
            if (split.length != 2) {
                logger.error("Неверный формат аргумента. Проверьте, что вы указали хост и порт, разделив их двоеточием");
                System.exit(1);
            }

            String strHost = split[0];
            String strPort = split[1];
            int port = 0;
            InetAddress ip = null;

            try {
                port = Integer.parseInt(strPort);
                ip = InetAddress.getByName(strHost);
            } catch (NumberFormatException e) {
                logger.error("Неверный формат порта: {}", strPort, e);
                System.exit(1);
            } catch (UnknownHostException e) {
                logger.error("Не удалось определить хост: {}", strHost, e);
                System.exit(-1);
            }

            ClientApplication app = new ClientApplication(ip, port);
            app.start();

        } catch (Exception e) {
            logger.error("Критическая ошибка в работе клиента: ", e);
            System.exit(-2);
        }
    }
}