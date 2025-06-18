package server.commands;

import common.collection.City;
import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

/**
 * Команда 'update'. Обновляет элемент коллекции.
 */
public class Update extends Command {
    CityService cityService;

    public Update(CityService cityService) {
        super(CommandDefinition.update_id,"Аргумент - id. Элемент. Обновить значение элемента коллекции, id которого равен заданному");
        this.cityService = cityService;
    }

    @Override
    public ExecutionResponse execute(String id_str, City element) {
        Long id;
        try {
            id = Long.parseLong(id_str);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("id имеет формат Long, попробуйте ввести еще раз");
        }
        cityService.updateById(id, element);
        return new ExecutionResponse(Command.EMPTY_RESULT);
    }

}