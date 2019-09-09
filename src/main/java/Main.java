import config.Config;
import manager.BotManager;
import rest.MongoLinker;

public class Main {
    public static void main(String[] args){
        try {
            Config c = Config.LoadConfig("config.json");


            MongoLinker mongo = MongoLinker.getInstance();
            mongo.connect(c.database);
            System.out.println(mongo.getUserIDFromToken("lamen"));
            String t = mongo.addUser(15);
            System.out.println(t);
            System.out.println(mongo.getUserIDFromToken(t));

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
