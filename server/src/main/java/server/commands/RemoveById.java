package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции.
 */
public class RemoveById extends Command {
    CityService cityService;
    public RemoveById(CityService cityService) {
        super(CommandDefinition.remove_key, "Аргумент - ключ. Удалить элемент из коллекции по его ключу");
        this.cityService = cityService;
    }
    @Override
    public ExecutionResponse execute(String key) {
        cityService.remove(key);
        return new ExecutionResponse(Command.EMPTY_RESULT);
    }
}