package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;

import java.util.stream.Collectors;

/**
 * Команда 'help'. Выводит справку по доступным командам
 */
public class Help extends Command {
    private final CommandRegistry commandRegistry;

    public Help(CommandRegistry inv) {
        super(CommandDefinition.help, "вывести справку по доступным командам");
        this.commandRegistry = inv;
    }

    @Override
    public ExecutionResponse execute() {
        String result = commandRegistry.getClientCommandDefinitions().stream()
                .map(item -> commandRegistry.getCommandDescription(item))
                .collect(Collectors.joining("\n"));
        return new ExecutionResponse(result);
    }
}