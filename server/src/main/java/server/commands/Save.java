package server.commands;

import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.ExecutionResponse;
import server.collection.CityService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Команда 'save'. Сохраняет коллекцию в файл.
 */
public class Save extends Command {
    CityService cityService;

    public Save(CityService cityService) {
        super(CommandDefinition.save, "сохранить коллекцию в файл");
        this.cityService = cityService;
    }

    @Override
    public ExecutionResponse execute() throws IOException {
        String xml = cityService.getCollectionAsXml();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cityService.getFileName()))) {
            writer.write(xml);
        }
        return new ExecutionResponse(Command.EMPTY_RESULT);
    }
}