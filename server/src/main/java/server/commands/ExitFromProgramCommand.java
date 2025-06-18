

import common.commands.AbstractCommand;
import common.utility.CommandRequest;
import common.utility.CommandResponse;

import java.io.IOException;

import static server.commands.CommandName.exit;


public class ExitFromProgramCommand extends AbstractCommand {
    public ExitFromProgramCommand() {
        super(exit.getBehavior(), "завершить программу (без сохранения в файл)");
    }

    private CommandResponse execute() {
        return new CommandResponse(EMPTY_RESULT);
    }

    @Override
    public CommandResponse execute(CommandRequest req) throws IOException {
        return execute();
    }
}