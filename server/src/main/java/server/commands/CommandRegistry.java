package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import lombok.Getter;
import server.collection.CityService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;



public class CommandRegistry {


    private final CityService cityService;
    public Map<CommandDefinition, Command> commands = new HashMap<>();
    public Collection<CommandDefinition> commandDefinitions;
    @Getter
    public Collection<CommandDefinition> clientCommandDefinitions;

    public CommandRegistry(CityService cityService, IHistoryProvider historyProvider) {
        this.cityService = cityService;
        commands.put(CommandDefinition.add, new Add(cityService, CommandDefinition.add));
        commands.put(CommandDefinition.exit, new Exit());
        commands.put(CommandDefinition.show, new Show(cityService));
        commands.put(CommandDefinition.save, new Save(cityService));
        commands.put(CommandDefinition.remove_key, new RemoveById(cityService));
        commands.put(CommandDefinition.update_id, new Update(cityService));
        commands.put(CommandDefinition.print_ascending, new PrintAscending(cityService));
        commands.put(CommandDefinition.remove_greater, new RemoveGreater(cityService));
        commands.put(CommandDefinition.add_if_max, new AddIfMax(cityService, CommandDefinition.add_if_max));
        commands.put(CommandDefinition.help, new Help(this));
        commands.put(CommandDefinition.group_counting_by_population, new GroupCounting(cityService));
        commands.put(CommandDefinition.info, new Info(cityService));
        commands.put(CommandDefinition.sum_of_meters_above_sea_level, new SumOfMeters(cityService));
        commands.put(CommandDefinition.clear, new Clear(cityService));
        commands.put(CommandDefinition.execute_script, new ExecuteScript(cityService));
        commands.put(CommandDefinition.history, new History(historyProvider));
        commandDefinitions = commands.keySet();

        clientCommandDefinitions = commands.keySet().stream()
                .filter(key -> key != CommandDefinition.save)
                .collect(Collectors.toList());
    }

    public Command getCommand(CommandDefinition commandDefinition) throws IllegalArgumentException {
        return commands.get(commandDefinition);
    }

    public String getCommandDescription(CommandDefinition commandDefinition) {
        Command command = getCommand(commandDefinition);
        return command.getCommandDefinition().name() + ": " + command.getDescription();
    }

    public Collection<CommandDefinition> getCommandNames() {
        return commands.keySet();
    }




}
