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
    private ConcurrentHashMap<String, String> database;

    private JsonDataBaseLinker(){
        ConcurrentHashMap<String, String> map = loadDataBase();
        if(map != null)
            database = map;
        else database = new ConcurrentHashMap<>();
    }

    public String getUserUniqueIDFromToken(String token) {
        if(token == null) return "";
        return database.getOrDefault(token, "");
    }

    public String getTokenFromUserUniqueID(String uid){
        if(database.containsValue(uid))
            for(Map.Entry<String, String> e: database.entrySet())
                if(e.getValue().equals(uid))
                    return e.getKey();

        return null;
    }

    public synchronized void removeUser(String uid){
        String token = getTokenFromUserUniqueID(uid);
        if(token != null){
            database.remove(token);
            saveDataBase();
        }
    }

    public synchronized String addUser(String uid){
        removeUser(uid);

        String token = getUniqueToken();
        database.putIfAbsent(token, uid);
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

    private synchronized ConcurrentHashMap<String, String> loadDataBase(){
        try {
            File file = new File(DATABASENAME);
            if (file.exists()) {
                InputStream is = new FileInputStream(file);
                String json = IOUtils.toString(is, "UTF-8");
                if(json.length() < 2) return null;

                ObjectMapper mapper = new ObjectMapper();
                TypeReference<ConcurrentHashMap<String, String>> typeReference =
                        new TypeReference<ConcurrentHashMap<String, String>>() {};

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
        if(!getUserUniqueIDFromToken(token).equals("")) return getUniqueToken();
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
