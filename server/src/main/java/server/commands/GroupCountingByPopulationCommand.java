package server.commands;

import common.commands.AbstractCommand;
import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

/**
 * Команда 'group_counting_by_population'. Группирует элементы коллекции по значению поля population и выводит количество элементов в каждой группе.
 */
public class GroupCounting extends AbstractCommand {
    CityService cityService;

}
