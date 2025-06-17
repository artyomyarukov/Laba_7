package client.client_command;

import common.commands.Command;
import common.utility.ExecutionResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import static common.commands.CommandDefinition.exit;


public class ExitFromProgramCommand extends Command {
    private static final Logger logger = Logger.getLogger(ExecuteScriptCommand.class.getName());
    public ExitFromProgramCommand() {
        super(exit, "завершить программу клиента (без сохранения в файл)");
    }

    @Override
    public ExecutionResponse execute() {
        logger.info("Завершаю программу клиента");
        System.exit(0);
        return new ExecutionResponse(EMPTY_RESULT);
    }


}