package config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Config {
    public Server server;
    public Bot[] bots;
    public Api api;

    public static Config LoadConfig(String filepath) throws FileNotFoundException {
        File file = new File(filepath);
        JsonReader reader = new JsonReader(new FileReader(file));
        Gson g = new Gson();
        return g.fromJson(reader, Config.class);
    }
}


