package server.commands;

import common.collection.City;
import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;

import server.collection.CityService;
import server.utility.IdCounter;


/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */
public class Add extends Command {
    CityService cityService;
    CommandDefinition comandDefinition;

    /**
     * Конструктор команды
     */

    public Add(CityService cityService, CommandDefinition comandDefinition) {
        super(CommandDefinition.add, "добавить новый элемент в коллекцию");
        this.cityService = cityService;
        this.comandDefinition = comandDefinition;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse execute(String key, City element) {

        element.setId(IdCounter.getNextId());
        String warning = cityService.put(key, element);
        return new ExecutionResponse(warning == null ? "Город добавлен" : warning);

    }
}