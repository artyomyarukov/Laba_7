package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

/**
 * Команда 'print_ascending'. Выводит элементы коллекции в порядке убывания.
 */
public class PrintAscending extends Command {
    CityService cityService;

    public PrintAscending(CityService cityService) {
        super(CommandDefinition.print_ascending, "вывести элементы коллекции в порядке возрастания");
        this.cityService = cityService;
    }
    @Override
    public ExecutionResponse execute(String arg) {
        return new ExecutionResponse(cityService.sortedByAreaCollection());
    }
}