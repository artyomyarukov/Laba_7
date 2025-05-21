package common.utility;


import common.commands.CommandDefinition;
import lombok.AllArgsConstructor;
import lombok.Data;



import java.io.Serializable;

@Data
@AllArgsConstructor
public class CommandWithArgument implements Serializable {
    private final CommandDefinition commandDefinition;
    private final String argument;
}
