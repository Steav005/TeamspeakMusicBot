import config.Config;

import rest.JsonDataBaseLinker;
import rest.RestServer;

public class Main {
    public static void main(String[] args){
        try {
            Config c = Config.LoadConfig("config.json");

            //Preload Database
            JsonDataBaseLinker db = JsonDataBaseLinker.getInstance();

            //Initiate RestServer
            RestServer server = new RestServer();
            server.start();

            while (true){
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            //BotManager.getInstance().start(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
