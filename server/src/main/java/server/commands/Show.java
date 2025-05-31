package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

import static server.collection.CityService.sortMapAndStringify;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 */
public class Show extends Command {
    CityService cityService;

    public Show(CityService cityService) {
        super(CommandDefinition.show, "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.cityService = cityService;
    }

    @Override
    public ExecutionResponse execute() {
        String sortCollectionAndStringifyResult = sortMapAndStringify(cityService.getWholeMap());
        return new ExecutionResponse(cityService.getCollectionSize() == 0 ? "ПУСТАЯ КОЛЛЕКЦИЯ" : sortCollectionAndStringifyResult);
    }
}