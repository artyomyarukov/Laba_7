package common.commands;


import common.collection.City;
import common.utility.ExecutionResponse;


import java.io.IOException;

/**
 * Интерфейс для всех выполняемых команд.
 */
public interface Executable {
    /**
     * Выполнить что-либо.
     * @return результат выполнения
     */
    ExecutionResponse execute(String arg)throws IOException;
    ExecutionResponse execute(String argument, City element) throws IOException;
    ExecutionResponse execute() throws IOException;
    ExecutionResponse execute(City element) throws IOException;
}