package server.commands;


import common.collection.City;
import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.utility.IdCounter;
import server.collection.CityService;

/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции.
 */
public class AddIfMax extends Command {
    CityService cityService;
    CommandDefinition comandDefinition;

    /**
     * Конструктор команды
     */

    public AddIfMax(CityService cityService, CommandDefinition comandDefinition) {
        super(CommandDefinition.add_if_max, "добавить новый элемент в коллекцию, если он больше максимального");
        this.cityService = cityService;
        this.comandDefinition = comandDefinition;
    }
    @Override
    public ExecutionResponse execute(String key, City element) {
        if(cityService.isGreaterThanMax(element)){
            element.setId(IdCounter.getNextId());
            String warning = cityService.put(key, element);
            return new ExecutionResponse(warning == null ? "" : warning);
        }
        return new ExecutionResponse("Элемент не превосходит максимального");

    }


}