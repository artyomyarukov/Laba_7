package server.commands;

import server.utility.HistoryList;

public interface IHistoryProvider {

    HistoryList getHistoryByClientID(String clientID);

}
