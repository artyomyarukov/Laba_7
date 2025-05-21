package client.client_command;

import common.commands.Command;
import common.utility.ExecutionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static common.commands.CommandDefinition.exit;


public class ExitFromProgramCommand extends Command {
    private static final Logger logger = LoggerFactory.getLogger(ExitFromProgramCommand.class);
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