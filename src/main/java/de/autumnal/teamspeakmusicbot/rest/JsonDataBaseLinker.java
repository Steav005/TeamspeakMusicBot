package de.autumnal.teamspeakmusicbot.rest;

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

    public int getUserDatabaseIDFromToken(String token) {
        if(token == null) return -1;
        return database.getOrDefault(token, -1);
    }

    public String getTokenFromUserDatabaseID(int userDatabaseID){
        if(database.containsValue(userDatabaseID))
            for(Map.Entry<String, Integer> e: database.entrySet())
                if(e.getValue() == userDatabaseID)
                    return e.getKey();

        return null;
    }

    public synchronized void removeUser(int userDatabaseID){
        String token = getTokenFromUserDatabaseID(userDatabaseID);
        if(token != null){
            database.remove(token);
            saveDataBase();
        }
    }

    public synchronized String addUser(int userDatabaseID){
        removeUser(userDatabaseID);

        String token = getUniqueToken();
        database.putIfAbsent(token, userDatabaseID);
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
        if(getUserDatabaseIDFromToken(token) != -1) return getUniqueToken();
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
