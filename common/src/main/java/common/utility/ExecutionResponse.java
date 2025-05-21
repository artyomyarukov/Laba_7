package common.utility;

/**
 * Класс для возврата результатов выполнения команд
 */
import lombok.Data;

import java.io.Serializable;

@Data
public class ExecutionResponse implements Serializable {
    private final String output;
    private final Exception error;

    public ExecutionResponse(String result) {
        this.output = result;
        this.error = null;
    }

    public ExecutionResponse(Exception error) {
        this.error = error;
        this.output = null;
    }
}