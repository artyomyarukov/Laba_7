package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

/**
 * Команда 'sum_of_meters_above_sea_level'. Выводит сумму значений поля metersAboveSeaLevel для всех элементов коллекции.
 */
public class SumOfMeters extends Command {
    CityService cityService;
    public SumOfMeters(CityService cityService) {
        super(CommandDefinition.sum_of_meters_above_sea_level, "Выводит сумму значений высот над уровнем моря городов ");
        this.cityService = cityService;
    }

    @Override
    public ExecutionResponse execute() {
        return new ExecutionResponse(cityService.calculateSumOfMetersAboveSeaLevel());
    }

}