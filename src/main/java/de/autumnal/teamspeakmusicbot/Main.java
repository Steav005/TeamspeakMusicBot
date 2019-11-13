package de.autumnal.teamspeakmusicbot;

import de.autumnal.teamspeakmusicbot.config.Config;

import de.autumnal.teamspeakmusicbot.manager.BotManager;
import de.autumnal.teamspeakmusicbot.rest.JsonDataBaseLinker;
import de.autumnal.teamspeakmusicbot.rest.RestServer;

public class Main {
    public static void main(String[] args){
        try {
            Config c = Config.LoadConfig("config.json");

            //Preload Database
            JsonDataBaseLinker db = JsonDataBaseLinker.getInstance();

            //Initiate RestServer
            RestServer server = new RestServer();
            server.start();

            BotManager.getInstance().start(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
