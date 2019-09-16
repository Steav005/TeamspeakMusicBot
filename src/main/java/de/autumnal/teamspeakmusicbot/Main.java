package de.autumnal.teamspeakmusicbot;

import de.autumnal.teamspeakmusicbot.config.Config;

import de.autumnal.teamspeakmusicbot.manager.BotManager;
import de.autumnal.teamspeakmusicbot.rest.JsonDataBaseLinker;
import de.autumnal.teamspeakmusicbot.rest.RestServer;

public class Main {
    public static void main(String[] args){
        try {
            Config c = Config.LoadConfig("de.autumnal.teamspeakmusicbot.config.json");

            //Preload Database
            JsonDataBaseLinker db = JsonDataBaseLinker.getInstance();

            //Initiate RestServer
            RestServer server = new RestServer();
            server.start();

            BotManager.getInstance().start(c);

            //while (true){
            //    try {
            //        Thread.sleep(1000);
            //    }catch (Exception e){
            //        e.printStackTrace();
            //    }
            //}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
