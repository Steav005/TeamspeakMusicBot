package de.autumnal.teamspeakmusicbot.rest;

import de.autumnal.teamspeakmusicbot.client.SlaveBot;
import com.google.gson.Gson;
import de.autumnal.teamspeakmusicbot.manager.BotManager;
import de.autumnal.teamspeakmusicbot.music.enums.Playmode;
import de.autumnal.teamspeakmusicbot.music.json.Player;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("v1.0")
public class BotRestApiV1 {

    @GET
    @Path("/teapot")
    public Response getMessage(){
        return Response.status(418).build();
    }

    @POST
    @Path("/player/song")
    public Response add(@HeaderParam("token") String token, String song){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(token);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null || song == null) return Response.status(400).build();

            bot.addTrackToPlayer(user, song);

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/player")
    public Response pause(@HeaderParam("token") String token, String cmd){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(token);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            PlayerCommand command = PlayerCommand.valueOf(cmd.toUpperCase());
            switch(command){
                case PAUSE:
                    bot.getPlayer().pauseSong();
                    break;
                case RESUME:
                    bot.getPlayer().resumeSong();
                    break;
                case NEXT:
                    bot.getPlayer().nextSong();
                    break;
                case PREVIOUS:
                    bot.getPlayer().previousSong();
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            return Response.ok().build();
        }catch (IllegalArgumentException e){
            return Response.status(400).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/player/playmode")
    public Response playmode(@HeaderParam("token") String token, String playmode){
        try {
            Playmode mode = Playmode.valueOf(playmode.toUpperCase());
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(token);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();

            bot.getPlayer().changePlaymode(mode);

            return Response.ok().build();
        } catch (IllegalArgumentException f){
            return Response.status(400).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @PUT
    @Path("/player/position")
    public Response forward(@HeaderParam("token") String token, @DefaultValue("-1") long pos){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(token);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();
            if(pos == -1) return Response.status(400).build();

            bot.getPlayer().jump(pos);

            return Response.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/player/queue")
    public Response list(@HeaderParam("token") String token, @DefaultValue("-1") @HeaderParam("queuechangeid") long changeid, @DefaultValue("50") @HeaderParam("length") int length){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(token);
            if (user == -1) return Response.status(401).build();
            SlaveBot bot = BotManager.getInstance().getBotByUser(user);
            if (bot == null) return Response.status(400).build();
            if (changeid == bot.getPlayer().getLastQueueChange())
                return Response.status(304).build();

            return Response.ok(new Gson().toJson(new Player(bot.getPlayer(), changeid, length)), MediaType.APPLICATION_JSON_TYPE).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/bot")
    public Response join(@HeaderParam("token") String token, String cmd){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(token);
            if (user == -1) return Response.status(401).build();

            BotCommand command = BotCommand.valueOf(cmd);
            switch (command){
                case JOIN:
                    int channel = BotManager.getInstance().getChannelByUser(user);
                    if (channel == -1) return Response.status(400).build();

                    if(!BotManager.getInstance().BotJoinChannel(channel))
                        return Response.status(400).build();
                    break;
                case LEAVE:
                    SlaveBot bot = BotManager.getInstance().getBotByUser(user);
                    if (bot == null) return Response.status(400).build();

                    bot.disconnect();
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            return Response.ok().build();
        }catch (IllegalArgumentException e){
            return Response.status(400).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/bot")
    public Response botList(@HeaderParam("token") String token){
        try {
            int user = JsonDataBaseLinker.getInstance().getUserIDFromToken(token);
            if (user == -1) return Response.status(401).build();

            return Response.ok(new Gson().toJson(BotManager.getInstance().getBotUIDList()), MediaType.APPLICATION_JSON_TYPE).build();
        }catch (Exception e){
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    private enum PlayerCommand {
        PREVIOUS,
        NEXT,
        PAUSE,
        RESUME;
    }

    private enum BotCommand {
        JOIN,
        LEAVE
    }
}
