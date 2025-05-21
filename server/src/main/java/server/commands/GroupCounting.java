package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

/**
 * Команда 'group_counting_by_population'. Группирует элементы коллекции по значению поля population и выводит количество элементов в каждой группе.
 */
public class GroupCounting extends Command {
    CityService cityService;
    public GroupCounting(CityService cityService) {
        super(CommandDefinition.group_counting_by_population, "Возвращает остортированные по населению города");
        this.cityService = cityService;
    }
    @Override
    public ExecutionResponse execute() {
        return new ExecutionResponse(cityService.getCitiesSortedByPopulation());
    }

}
