package server.requets_processing;


import common.commands.Command;
import common.commands.CommandDefinition;
import common.utility.CommandRequest;
import common.utility.CommandWithArgument;
import common.utility.ExecutionResponse;
import lombok.AllArgsConstructor;
import server.commands.CommandRegistry;
import server.commands.History;


@AllArgsConstructor
public class CommandController {
    private final CommandRegistry commandRegistry;
    /**   private final static Map<HandlerKey, Function> handlerMap = new HashMap();
     static {
     handlerMap.put(new HandlerKey(false, false), AbstractCommand::execute);
     }
     */
    public ExecutionResponse handle(CommandRequest request) {
        CommandWithArgument commandWithArgument = request.getCommandWithArgument();
        CommandDefinition commandDefinition = commandWithArgument.getCommandDefinition();
        ExecutionResponse executionResult = null;
        try {
            Command command = commandRegistry.getCommand(commandDefinition);
            HandlerKey handlerKey = command instanceof History ? new HandlerKey(true, false) : new HandlerKey(command);
            switch (handlerKey.hashCode()) {
                case 0:
                    executionResult = command.execute();
                    break;
                case 1:
                    String argument = command instanceof History ? request.getClientID().toString() : commandWithArgument.getArgument();
                    executionResult = command.execute(argument);
                    break;
                case 2:
                    executionResult = command.execute(request.getElement());
                    break;
                case 3:
                    executionResult = command.execute(commandWithArgument.getArgument(), request.getElement());
                    break;
            }
        } catch (Exception e) {
            executionResult = new ExecutionResponse(e);
        }
        return executionResult;
    }
    @AllArgsConstructor
    private static class HandlerKey{
        boolean hasArgument;
        boolean hasElement;
        public HandlerKey(Command abstractCommand){
            hasArgument = abstractCommand.hasArg();
            hasElement = abstractCommand.hasElement();
        }

        @Override
        public int hashCode() {
            int result = 0;
            result += hasArgument ? 1: 0;
            result += hasElement ? 2: 0;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HandlerKey that = (HandlerKey) o;
            return hasElement == that.hasElement && hasArgument == that.hasArgument;
        }
    }
}