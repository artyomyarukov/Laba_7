package server;



import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Level;


public final class Server {
    private static final Logger logger =Logger.getLogger(Server.class.getName());
    static {
        // Настройка формата вывода логов
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$s: %5$s %n");
        System.setProperty("file.encoding", "UTF-8");

    }

    private Server() {
        throw new UnsupportedOperationException("This is an utility class and can not be instantiated");
    }

    public static void main(String[] args){
        if (args.length != 2) {
            logger.log(Level.INFO, "Получена команда на завершение работы");
            System.exit(1);
        }
        String strPort = args[1];
        int port = 0;
        try {
            port = Integer.parseInt(strPort);
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING,"Ожидается ввод : файл:сервер");
            System.exit(1);
        }
        ServerApplication app = new ServerApplication(args[0], port);
        app.start();

    }
}
