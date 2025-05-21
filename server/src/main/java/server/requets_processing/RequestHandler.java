package server.requets_processing;

import common.collection.City;
import common.commands.ArgType;
import common.commands.CommandDefinition;
import common.utility.CommandRequest;
import common.utility.CommandWithArgument;
import common.utility.ExecutionResponse;
import lombok.Getter;
import server.commands.CommandRegistry;
import server.commands.IHistoryProvider;
import server.utility.HistoryList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class RequestHandler implements IHistoryProvider {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    @Getter
    private final CommandController commandController;
    private final CommandRegistry commandRegistry;
    private Map<String, HistoryList> historyByClients = new ConcurrentHashMap();

    public RequestHandler(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
        this.commandController = new CommandController(commandRegistry);
    }

    public Object onReceive(Object inputData) {
        if (inputData instanceof CommandRequest) {
            CommandRequest commandRequest = (CommandRequest) inputData;
            ExecutionResponse validateResponse = validateCommandRequest(commandRequest);
            if (validateResponse != null){
                return validateResponse;
            }
            CommandDefinition commandDefinition = commandRequest.getCommandWithArgument().getCommandDefinition();
            UUID clientID = commandRequest.getClientID();
            HistoryList historyList = historyByClients.get(clientID.toString());
            if (historyList == null) {
                logger.warn("клиент с таким id не зарегистрирован, регистрирую " + clientID);
                historyList = new HistoryList();
                historyByClients.put(clientID.toString(), historyList);
            }
            historyList.addCommand(commandDefinition);
            return commandController.handle(commandRequest);
        } else if (inputData instanceof UUID) {
            historyByClients.put(inputData.toString(), new HistoryList());
            return commandRegistry.getClientCommandDefinitions();
        }
        return errorResponse("Вы передали какую-то чепуху: ", inputData);
    }

    private static ExecutionResponse errorResponse(String message, Object inputData) {
        return new ExecutionResponse(new IllegalArgumentException(message + inputData));
    }

    @Override
    public HistoryList getHistoryByClientID(String clientID) {
        return historyByClients.get(clientID);
    }

    private ExecutionResponse validateCommandRequest(CommandRequest commandRequest) {
        City element = commandRequest.getElement();
        CommandWithArgument commandWithArgument = commandRequest.getCommandWithArgument();
        if (commandWithArgument == null){
            return errorResponse("Неверный формат запроса: ", commandRequest);
        }
        CommandDefinition commandDefinition = commandWithArgument.getCommandDefinition();
        if (commandDefinition == null){
            return errorResponse("Неверный формат запроса: ", commandRequest);
        }
        if (commandDefinition == CommandDefinition.save || commandDefinition == CommandDefinition.exit){
            return errorResponse("Вы МОШЕННИК: эта команда на сервере не разрешена: ", commandRequest);
        }
        String argument = commandWithArgument.getArgument();
        if (commandDefinition.hasArg()) {
            if (argument == null || argument.isEmpty()) {
                return errorResponse("Ожидался аргумент, ничего не пришло: ", commandRequest);
            }
            if (commandDefinition.getArgType() == ArgType.LONG){
                try {
                    Long.parseLong(argument);
                } catch (NumberFormatException e){
                    return errorResponse("Ожидался аргумент типа Long, пришло: ", commandRequest);
                }
            }
        }
        if (commandDefinition.hasElement()) {
            if (element == null) {
                return errorResponse("Ожидался элемент, ничего не пришло: ", commandRequest);
            }
            if (!element.validate()) {
                return errorResponse("Вы передали невалидный элемент: ", commandRequest);
            }
        }
        return null;
    }
}