package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;

/**
 * Команда 'history'. Вывыодит историю команд.
 */
public class History extends Command {
    private final IHistoryProvider historyProvider;

    public History(IHistoryProvider historyProvider) {
        super(CommandDefinition.history, "вывести последние 15 команд (без их аргументов)");
        this.historyProvider = historyProvider;
    }

    @Override
    public ExecutionResponse execute(String clientID) {
        String lastNCommands = historyProvider.getHistoryByClientID(clientID).getLastNCommands(15);
        return new ExecutionResponse("История клиента с ID: " + clientID + "\n" + lastNCommands);
    }
}