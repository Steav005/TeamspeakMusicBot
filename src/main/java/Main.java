import config.Config;
import manager.BotManager;

public class Main {
    public static void main(String[] args){
        try {
            Config c = Config.LoadConfig("config.json");

            BotManager.getInstance().start(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
