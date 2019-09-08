package manager;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class APIManager {
    private static APIManager singleton;
    private Config config;

    private APIManager(){
    }

    public void setConfig(Config c){
        config = c;
    }

    public List<SearchResult> youtubeSearch(String query, int resultsNumber){
        List<SearchResult> results;
        YouTube youTube;

        try{
            youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest httpRequest) {
                }
            }).setApplicationName("teamspeakbot").build();

            YouTube.Search.List search = youTube.search().list("id,snippet");
            search.setKey(config.api.youtube);
            search.setQ(query);
            search.setType("video");
            search.setFields("items(id/videoId,snippet/title)");
            search.setMaxResults((long) resultsNumber);

            SearchListResponse searchListResponse = search.execute();
            results = searchListResponse.getItems();

            if(results == null){
                results = new ArrayList<>();
            }

            return results;
        }catch (Exception e){
            e.printStackTrace();

            results = new ArrayList<>();
            return results;
        }
    }

    public static APIManager getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    private static synchronized APIManager createSingleton(){
        if(singleton == null) singleton = new APIManager();
        return singleton;
    }
}
