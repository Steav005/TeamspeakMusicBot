package rest;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerSettings;
import config.Database;
import org.apache.commons.lang.NullArgumentException;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Arrays;

public class MongoLinker {
    private static MongoLinker singleton;
    private MongoClient mongo;
    private String databaseName;

    private MongoLinker(){

    }

    public void connect(String address) throws NullArgumentException {
        if(address == null)
            throw new NullArgumentException("Database Address is null");
        if(mongo != null) mongo.close();

        mongo = MongoClients.create("mongodb://" + address);
        databaseName = "teamspeakBots";
    }

    private MongoCollection<Document> getTokenCollection(){
        MongoDatabase database = mongo.getDatabase(databaseName);
        return database.getCollection("token");
    }

    public int getUserIDFromToken(String token) {
        MongoCollection<Document> tokenCollection = getTokenCollection();

        try {
            Document first = tokenCollection.find(new Document("token", token)).first();
            int id = Integer.valueOf(first.get("userid").toString());
            return id;
        }catch (Exception e){
            return -1;
        }
    }

    public void removeUser(int userID){
        getTokenCollection().deleteOne(eq("userid", userID));
    }

    public String addUser(int userID){
        MongoCollection<Document> collection = getTokenCollection();

        removeUser(userID);

        String token = getUniqueToken();
        Document document = new Document("userid", userID)
                .append("token", token);
        collection.insertOne(document);

        return token;
    }

    private String getUniqueToken(){
        String token = java.util.UUID.randomUUID().toString();
        if(getUserIDFromToken(token) != -1) return getUniqueToken();
        return token;
    }

    public static MongoLinker getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    private static synchronized MongoLinker createSingleton(){
        if(singleton == null) singleton = new MongoLinker();
        return singleton;
    }
}
