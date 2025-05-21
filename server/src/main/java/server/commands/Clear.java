package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;
import server.utility.IdCounter;

/**
 * Команда 'clear'. Очищает коллекцию.
 */
public class Clear extends Command {
    CityService cityService;

    /**
     * Конструктор команды
     */

    public Clear(CityService cityService) {
        super(CommandDefinition.clear, "очистить коллекцию");
        this.cityService = cityService;
    }
    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse execute() {
        cityService.clear_collection();
        IdCounter.setId(0);
        return new ExecutionResponse("Коллекция очищена!");
    }
}