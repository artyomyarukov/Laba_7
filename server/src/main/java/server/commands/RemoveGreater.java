package server.commands;


import common.collection.City;
import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

;

/**
 * Команда 'remove_greater {element}'. Удаляет из коллекции все элементы, превышающие заданный.
 */
public class RemoveGreater extends Command {
    CityService cityService;

    public RemoveGreater(CityService cityService) {
        super(CommandDefinition.remove_greater, "Элемент. Удалить из коллекции все элементы, превышающие заданный");
        this.cityService = cityService;
    }

    @Override
    public ExecutionResponse execute(City element) {
        cityService.removeGreater(element);
        return new ExecutionResponse(Command.EMPTY_RESULT);
    }
}