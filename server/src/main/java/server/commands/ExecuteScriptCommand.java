package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

import java.io.IOException;

/**
 * Команда 'execute_script'. Выполнить скрипт из файла.
 */
public class ExecuteScript extends Command {
    public ExecuteScript(CityService cityService) {
        super(CommandDefinition.execute_script, "Аргумент - filename, считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
    }

    public ExecutionResponse execute(String arg) throws IOException {
        return new ExecutionResponse("execute_script добавлен в историю");
    }
}