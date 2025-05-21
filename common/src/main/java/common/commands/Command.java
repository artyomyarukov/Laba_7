package common.commands;


import common.collection.City;
import common.utility.ExecutionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

/**
 * Абстрактная команда с именем и описанием
 */
@Data
@AllArgsConstructor


public abstract class Command implements Executable {
    public static final String EMPTY_RESULT = "";
    private final CommandDefinition commandDefinition;
    private final String description;

    public ExecutionResponse execute(String arg) throws IOException {
        throw new UnsupportedOperationException();
    }
    public ExecutionResponse execute(String argument, City element) throws IOException{
        throw new UnsupportedOperationException();
    }
    public ExecutionResponse execute() throws IOException{
        throw new UnsupportedOperationException();
    }
    public ExecutionResponse execute(City element) throws IOException{
        throw new UnsupportedOperationException();
    }

    public boolean hasElement() {
        return commandDefinition.hasElement();
    }
    public boolean hasArg() {
        return commandDefinition.hasArg();
    }
    public boolean isClientCommand() {
        return commandDefinition.isClient();
    }
    public CommandDefinition getCommandDefinition(){
        return commandDefinition;
    }
}