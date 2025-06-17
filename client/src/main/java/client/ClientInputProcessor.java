package client;

import client.ClientConnector;
import common.commands.CommandDefinition;
import common.utility.CityItemAssembler;
import common.utility.CommandRequest;
import common.utility.CommandWithArgument;
import common.utility.ExecutionResponse;
import lombok.Data;
import client.client_command.ExecuteScriptCommand;
import client.client_command.ExitFromProgramCommand;
import client.input.AbstractInput;

import java.util.Collection;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import static common.commands.ArgType.LONG;
import static client.ClientApplication.CLIENT_ID;

/*
обработка команд
 */
@Data
public class ClientInputProcessor {
    private static final Logger logger = Logger.getLogger(ClientInputProcessor.class.getName());
    public static boolean debug = true;
    private final ClientConnector clientConnector;
    private Collection<CommandDefinition> commandDefinitions;
    private Stack<String> scriptExecutionContext;
    private ExecuteScriptCommand executeScriptCommand;
    private ExitFromProgramCommand exitCommand;

    static {
        // Настройка формата вывода логов
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%4$s: %5$s %n");
        System.setProperty("file.encoding", "UTF-8");

    }

    public ClientInputProcessor(Collection<CommandDefinition> commandDefinitions, ClientConnector clientConnector) {
        this.scriptExecutionContext = new Stack<>();
        this.commandDefinitions = commandDefinitions;
        this.clientConnector = clientConnector;
        this.executeScriptCommand = new ExecuteScriptCommand(this);
        this.exitCommand = new ExitFromProgramCommand();
    }

    public void processInput(AbstractInput input, boolean interactive) throws Exception {
        String line;
        CommandWithArgument commandWithArgument = null;
        CityItemAssembler cityItemAssembler = null;
        while ((line = input.readLine()) != null) {
            if (cityItemAssembler == null) {
                try {
                    commandWithArgument = parseLineAsCommand(line);
                } catch (Exception e) {
                    handleException(interactive, e);
                    continue;
                }
                if (commandWithArgument.getCommandDefinition().hasElement()) {
                    cityItemAssembler = new CityItemAssembler(interactive);
                    continue;
                }
                sendAndProcessRequest(commandWithArgument, null);
                continue;
            }
            try {
                cityItemAssembler.addNextLine(line);
            } catch (Exception e) {
                handleException(interactive, e);
                continue;
            }
            if (cityItemAssembler.isFinished()) {
                sendAndProcessRequest(commandWithArgument, cityItemAssembler);
                cityItemAssembler = null;
            }
        }
        if (cityItemAssembler != null) {
            logger.log(Level.SEVERE, "Внимание! У вас есть невыполненная последняя команда - недостаточно полей введено, {0}",
                    commandWithArgument);
        }
    }

    private void sendAndProcessRequest(CommandWithArgument commandWithArgument, CityItemAssembler cityItemAssembler) throws Exception {
        CommandDefinition commandDefinition = commandWithArgument.getCommandDefinition();
        if (commandDefinition.hasElement()) {
            if (cityItemAssembler == null) {
                throw new IllegalArgumentException("Вы не передали элемент на команду, которой он необходим: " + commandWithArgument);
            }
        } else {
            if (cityItemAssembler != null) {
                throw new IllegalArgumentException("Вы передали элемент на команду, которой он не нужен: " + commandWithArgument);
            }
        }
        CommandRequest commandRequest = new CommandRequest(commandWithArgument,
                cityItemAssembler == null ? null : cityItemAssembler.getCity(), CLIENT_ID);
        if (commandDefinition == CommandDefinition.execute_script) {
            runExecuteScript(commandRequest);
        } else if (commandDefinition == CommandDefinition.exit) {
            exitCommand.execute();
        }
        ExecutionResponse commandResponse = clientConnector.sendCommand(commandRequest);
        processResponse(commandResponse);
    }

    private void runExecuteScript(CommandRequest commandRequest) throws Exception {
        try {
            executeScriptCommand.execute(commandRequest.getCommandWithArgument().getArgument());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка при выполнении скрипта", e);
        }
    }

    private void handleException(boolean interactive, Exception e) throws Exception {
        if (!interactive) {
            throw e;
        }
        displayCommonError(e);
    }

    private void processResponse(ExecutionResponse commandResponse) {
        if (commandResponse.getError() == null) {
            System.out.println(commandResponse.getOutput());
        } else {
            displayCommonError(commandResponse.getError());
        }
    }

    private CommandWithArgument parseLineAsCommand(String line) {
        String[] splittedLine = line.trim().split("\\s+");
        CommandDefinition commandDefinition;
        try {
            commandDefinition = CommandDefinition.valueOf(splittedLine[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Такой команды НЕТ: " + splittedLine[0], e);
        }
        if (!commandDefinition.hasArg() && splittedLine.length >= 2) {
            throw new IllegalArgumentException("Слишком много аргументов, ожидалось 0: " + line);
        } else if (commandDefinition.hasArg()) {
            if (splittedLine.length == 1 || splittedLine.length > 2) {
                throw new IllegalArgumentException("Неправильное количество аргументов, ожидался 1: " + line);
            }
            if (commandDefinition.getArgType() == LONG) {
                try {
                    Long.parseLong(splittedLine[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Ожидался аргумент типа Long, пришло: " + line);
                }
            }
        }
        return new CommandWithArgument(commandDefinition, splittedLine.length == 2 ? splittedLine[1] : null);
    }

    public void setScriptExecutionContext(String path) {
        scriptExecutionContext.push(path);
    }

    public void exitContext() {
        scriptExecutionContext.pop();
    }

    public boolean checkContext(String currentFile) {
        return scriptExecutionContext.contains(currentFile);
    }

    private void displayCommonError(Exception e) {
        logger.log(Level.SEVERE, e.getMessage());
    }
}