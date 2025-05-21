package common.utility;


import common.collection.City;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CommandRequest implements Serializable {
    private CommandWithArgument commandWithArgument;
    private City element;
    private UUID clientID;
}