package server.commands;

import common.collection.City;
import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 */
public class Info extends Command {
    CityService cityService;


    public Info(CityService cityService) {
        super(CommandDefinition.info, "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.cityService = cityService;
    }

    @Override
    public ExecutionResponse execute() {
        return new ExecutionResponse("Это CityCollection, текущий размер: " + cityService.getCollectionSize() + ", состоит из элементов типа: " + City.class + "\n");
    }
}