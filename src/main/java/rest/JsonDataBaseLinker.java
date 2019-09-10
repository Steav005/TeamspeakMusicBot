package rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonDataBaseLinker {
    private final String DATABASENAME = "logintoken.json";
    private static JsonDataBaseLinker singleton;
    private ConcurrentHashMap<String, Integer> database;

    private JsonDataBaseLinker(){
        ConcurrentHashMap<String, Integer> map = loadDataBase();
        if(map != null)
            database = map;
        else database = new ConcurrentHashMap<>();
    }

    public int getUserIDFromToken(String token) {
        return database.getOrDefault(token, -1);
    }

    public String getTokenFromUserID(int userID){
        if(database.containsValue(userID))
            for(Map.Entry<String, Integer> e: database.entrySet())
                if(e.getValue() == userID)
                    return e.getKey();

        return null;
    }

    public synchronized void removeUser(int userID){
        String token = getTokenFromUserID(userID);
        if(token != null){
            database.remove(token);
            saveDataBase();
        }
    }

    public synchronized String addUser(int userID){
        removeUser(userID);

        String token = getUniqueToken();
        database.putIfAbsent(token, userID);
        saveDataBase();

        return token;
    }

    private synchronized void saveDataBase(){
        Gson gson = new Gson();
        String json = gson.toJson(database);

        try (FileWriter file = new FileWriter(DATABASENAME)){
            file.write(json);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private synchronized ConcurrentHashMap<String, Integer> loadDataBase(){
        try {
            File file = new File(DATABASENAME);
            if (file.exists()) {
                InputStream is = new FileInputStream(file);
                String json = IOUtils.toString(is, "UTF-8");
                if(json.length() < 2) return null;

                ObjectMapper mapper = new ObjectMapper();
                TypeReference<ConcurrentHashMap<String, Integer>> typeReference =
                        new TypeReference<ConcurrentHashMap<String, Integer>>() {};

                return mapper.readValue(json, typeReference);
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private String getUniqueToken(){
        String token = java.util.UUID.randomUUID().toString();
        if(getUserIDFromToken(token) != -1) return getUniqueToken();
        return token;
    }

    public static JsonDataBaseLinker getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    private static synchronized JsonDataBaseLinker createSingleton(){
        if(singleton == null) singleton = new JsonDataBaseLinker();
        return singleton;
    }
}
