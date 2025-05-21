package server.utility;


import common.commands.CommandDefinition;

import java.util.ArrayList;
import java.util.List;

public class HistoryList {
    private final List<CommandDefinition> historyList = new ArrayList<>();

    public void addCommand(CommandDefinition command) {
        historyList.add(command);
    }

    public String getLastNCommands(int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = historyList.size() - 1; i >= 0 && i > historyList.size() - n; i--) {
            sb.append(historyList.get(i) + "\n");
        }
        return sb.toString();
    }


}
