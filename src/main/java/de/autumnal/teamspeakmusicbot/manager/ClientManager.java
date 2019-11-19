package de.autumnal.teamspeakmusicbot.manager;

import com.github.manevolent.ts3j.api.Client;
import de.autumnal.teamspeakmusicbot.client.TeamspeakBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ClientManager {
    private static ClientManager singleton;
    private final HashMap<Integer, TeamspeakUser> USER_MAP;
    private final Timer TIMER;
    private final UpdateTask UPDATE_TASK;

    private ClientManager(){
        USER_MAP = new HashMap<>();

        UPDATE_TASK = new UpdateTask();
        TIMER = new Timer();
        TIMER.schedule(UPDATE_TASK, 0, 2000);
    }


    public synchronized TeamspeakUser getUserByDatabaseID(int databaseID){
        try{
            return USER_MAP.get(databaseID);
        }catch (Exception e){
            System.out.println("Couldn't find DatabaseID: " + databaseID + " in UserMap");
            return null;
        }
    }

    public synchronized TeamspeakUser getUserByUserID(int userID){
        for (TeamspeakUser u: USER_MAP.values()) {
            if(u.getUserID() == userID)
                return u;
        }
        return null;
    }

    public synchronized int getUserCountbyChannelID(int channelID){
        int i = 0;
        for (TeamspeakUser u: USER_MAP.values()) {
            if (u.getChannelID() == channelID)
                i++;
        }
        return i;
    }

    private synchronized void updateUserMap(ArrayList<Client> clients){
        USER_MAP.clear();
        for (Client c: clients) {
            USER_MAP.put(c.getDatabaseId(), new TeamspeakUser(c.getId(), c.getDatabaseId(), c.getChannelId()));
        }
    }

    public void forceUpdate(){
        UPDATE_TASK.run();
    }

    private static synchronized ClientManager createSingleton(){
        if(singleton == null) singleton = new ClientManager();
        return singleton;
    }

    public static ClientManager getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    private class UpdateTask extends TimerTask {
        private final BotManager MANAGER;

        UpdateTask(){
            MANAGER = BotManager.getInstance();
        }

        public void run() {
            try {
                updateUserMap(MANAGER.getClientList());
            } catch (Exception ex) {
                System.err.println("Error updating UserMap");
            }
        }
    }
}
