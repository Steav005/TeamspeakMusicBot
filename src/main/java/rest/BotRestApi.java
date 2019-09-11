package rest;

import client.SlaveBot;
import com.google.gson.Gson;
import manager.BotManager;
import music.BotAudioPlayer;
import music.enums.Playmode;
import music.json.Player;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("{userid}")
public class BotRestApi {

    @GET
    @Path("/teapot")
    public Response getMessage(){
        return Response.status(418).build();
    }

    @GET
    @Path("/player/add/{url}")
    public Response add(@PathParam("userid") String id, @PathParam("url") String url){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.addTrackToPlayer(user, url);

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/pause")
    public Response pause(@PathParam("userid") String id){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().pauseSong();

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/resume")
    public Response resume(@PathParam("userid") String id){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().resumeSong();

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/next")
    public Response next(@PathParam("userid") String id){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().nextSong();

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/previous")
    public Response previous(@PathParam("userid") String id){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().previousSong();

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/playmode/{playmode}")
    public Response playmode(@PathParam("userid") String id, @PathParam("playmode") String playmode){
        try {
            Playmode mode = Playmode.valueOf(playmode.toUpperCase());
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().changePlaymode(mode);

            return Response.ok().build();
        } catch (IllegalArgumentException f){
            return Response.status(404).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/forward/{sec}")
    public Response forward(@PathParam("userid") String id, @PathParam("sec") long sec){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().forward(sec);

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/jump/{sec}")
    public Response jump(@PathParam("userid") String id, @PathParam("sec") long sec){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().jump(sec);

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/status/{queuechangeid}/{length}")
    public Response list(@PathParam("userid") String id, @PathParam("queuechangeid") long changeid, @PathParam("length") int length){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            return Response.ok(new Gson().toJson(new Player(bot.getPlayer(), changeid, length)), MediaType.APPLICATION_JSON_TYPE).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/bot/join")
    public Response join(@PathParam("userid") String id){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            int channel = BotManager.getInstance().getChannelByUser(user);
            if (channel == -1) return Response.status(400).build();

            if(!BotManager.getInstance().BotJoinChannel(channel))
                return Response.status(400).build();

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/bot/leave")
    public Response leave(@PathParam("userid") String id){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.disconnect();

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/bot/list")
    public Response botList(@PathParam("userid") String id){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(id);
            if (user == -1) return Response.status(401).build();

            return Response.ok(new Gson().toJson(BotManager.getInstance().getBotUIDList()), MediaType.APPLICATION_JSON_TYPE).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }
}
