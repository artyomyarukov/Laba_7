package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;

/**
 * Команда 'exit'. Завершает выполнение.
 *
 * @author artyom_yarukov
 */
public class Exit extends Command {

    public Exit() {
        super(CommandDefinition.exit, "завершить программу (без сохранения в файл)");
    }

    @Override
    public ExecutionResponse execute() {
        return new ExecutionResponse(Command.EMPTY_RESULT);
    }
}