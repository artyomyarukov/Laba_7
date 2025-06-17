package client.client_command;

import client.ClientInputProcessor;
import client.input.AbstractInput;
import client.input.FileInput;
import common.commands.Command;
import common.utility.ExecutionResponse;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static common.commands.CommandDefinition.execute_script;

public class ExecuteScriptCommand extends Command {
    private final ClientInputProcessor inputProcessor;
    private static final Logger logger = Logger.getLogger(ExecuteScriptCommand.class.getName());

    public ExecuteScriptCommand(ClientInputProcessor inpPr) {
        super(execute_script, "Аргумент - filename. Считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.");
        this.inputProcessor = inpPr;
    }

    public ExecutionResponse execute(String arg) throws IOException {
        File file = new File(arg);
        logger.log(Level.INFO, "-------------------- Начало выполнения файла: {0} ---------------------------------------------------------------------",
                file.getCanonicalPath());

        if (inputProcessor.checkContext(file.getCanonicalPath())) {
            throw new IllegalArgumentException("Обнаружен ЦИКЛ, файл: " + file + " не будет открыт");
        }

        inputProcessor.setScriptExecutionContext(file.getCanonicalPath());
        try (AbstractInput fileInput = new FileInput(file)) {
            inputProcessor.processInput(fileInput, false);
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении файла, проверьте что он существует");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Произошла ошибка,принудительное завершение чтения файла", e);
        } finally {
            inputProcessor.exitContext();
            logger.log(Level.INFO, "-------------------- Конец выполнения файла: {0} ---------------------------------------------------------------------",
                    file.getCanonicalPath());
        }
        return new ExecutionResponse(EMPTY_RESULT);
    }
}