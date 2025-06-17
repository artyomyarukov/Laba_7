package client;




import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/*

точка входа клиента
 */

public final class Client {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private Client() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }
    static {
        // Настройка формата вывода логов
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$s: %5$s %n");
        System.setProperty("file.encoding", "UTF-8");

    }

    public static void main(String[] args) throws Exception {
        logger.info("Запуск клиента...");
        logger.log(Level.FINE, "Параметры запуска: {0}", Arrays.toString(args));

        try {
            if (args.length != 1) {
                logger.severe("Передано неверное количество аргументов. Проверьте, что вы указали хост и порт, разделив их двоеточием");
                System.exit(-1);
            }

            String hostWithPort = args[0];
            String[] split = hostWithPort.split(":");
            if (split.length != 2) {
                logger.severe("Неверный формат аргумента. Проверьте, что вы указали хост и порт, разделив их двоеточием");
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
                logger.log(Level.SEVERE, "Неверный формат порта: " + strPort, e);
                System.exit(1);
            } catch (UnknownHostException e) {
                logger.log(Level.SEVERE, "Не удалось определить хост: " + strHost, e);
                System.exit(-1);
            }

            ClientApplication app = new ClientApplication(ip, port);
            app.start();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Критическая ошибка в работе клиента: ", e);
            System.exit(-2);
        }
    }
}