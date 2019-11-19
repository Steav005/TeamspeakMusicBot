package de.autumnal.teamspeakmusicbot.client;

import com.github.manevolent.ts3j.api.TextMessageTargetMode;
import com.github.manevolent.ts3j.event.*;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.autumnal.teamspeakmusicbot.config.Bot;
import de.autumnal.teamspeakmusicbot.manager.BotManager;
import de.autumnal.teamspeakmusicbot.manager.ClientManager;
import de.autumnal.teamspeakmusicbot.manager.Command;
import de.autumnal.teamspeakmusicbot.manager.TeamspeakUser;
import de.autumnal.teamspeakmusicbot.music.BotAudioPlayer;
import de.autumnal.teamspeakmusicbot.music.enums.Playmode;
import de.autumnal.teamspeakmusicbot.rest.JsonDataBaseLinker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class SlaveBot extends TeamspeakBot {
    private BotAudioPlayer player;
    private int wantedChannel;
    public final Bot bot;

    public SlaveBot(File identityFile, Bot bot) throws IOException, TimeoutException {
        super(identityFile);

        this.bot = bot;
        wantedChannel = -1;
        resetPlayer();
    }

    private void resetPlayer(){
        if(player != null) player.destroy();

        player = BotManager.getInstance().getNewPlayer();
        setMicrophone(player.getSendHandler());
    }

    public int getWantedChannel() {
        return wantedChannel;
    }

    public void setWantedChannel(int wantedChannel) {
        this.wantedChannel = wantedChannel;
    }

    public boolean isOccupied() {
        return wantedChannel != -1;
    }

    public BotAudioPlayer getPlayer() {
        return player;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        TeamspeakUser invoker = ClientManager.getInstance().getUserByUserID(e.getInvokerId());

        if(e.getTargetMode() != TextMessageTargetMode.CLIENT
                || e.getMessage().isEmpty()
                || e.getInvokerId() == client.getClientId()
                || invoker.getChannelID() == wantedChannel) return;

        if(e.getMessage().startsWith("!"))
            handleCommand(e);
        else
            addTrackToPlayer(e.getMessage().replace("[URL]", "").replace("[/URL]", ""));
    }

    public void disconnect() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        client.disconnect();
    }

    public void handleCommand(TextMessageEvent e){
        String[] cmd = e.getMessage().substring(1).split(" ");

        switch (Command.valueOf(cmd[0].toUpperCase())){
            //case HELP:
            //    try {
            //        client.sendPrivateMessage(e.getInvokerId(), Command.getCommandList());
            //    } catch (Exception ex) {
            //        ex.printStackTrace();
            //    }
            //    return;
            case STATUS:
                try{
                    client.sendPrivateMessage(e.getInvokerId(), player.getPlayerStatusMessage());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return;
            case LEAVE:
                try {
                    client.disconnect();
                } catch (Exception f){
                    //Ignore
                }
                return;
            case NEXT:
                player.nextSong();
                return;
            case PREV:
                player.previousSong();
                return;
            case PAUSE:
                player.pauseSong();
                return;
            case RESUME:
                player.resumeSong();
                return;
            case PLAYMODE:
                Playmode mode = Playmode.valueOf(cmd[1].toUpperCase());
                player.changePlaymode(mode);
                return;
            case FORWARD:
                player.forward((long)(Double.parseDouble(cmd[1]) * 1000));
                return;
            case JUMP:
                player.jump((long)(Double.parseDouble(cmd[1]) * 1000));
                return;
            case TOKEN:
                try {
                    client.sendPrivateMessage(e.getInvokerId(), JsonDataBaseLinker.getInstance().getTokenFromUserDatabaseID(
                            client.getClientInfo(e.getInvokerId()).getDatabaseId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            case NEWTOKEN:
                try{
                    client.sendPrivateMessage(e.getInvokerId(), JsonDataBaseLinker.getInstance().addUser(
                            client.getClientInfo(e.getInvokerId()).getDatabaseId()));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return;
            case ADDRESS:
                try {
                    client.sendPrivateMessage(e.getInvokerId(), BotManager.getInstance().getRestAddress());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return;
            //case LIST:
            //    try {
            //        client.sendPrivateMessage(e.getInvokerId(), player.getPlaylistString());
            //    }catch (Exception ex){
            //        ex.printStackTrace();
            //    }
            //    return;
            default: //ADD case
                addTrackToPlayer(e.getMessage());
                return;
        }
    }

    @Override
    public void onConnected(ConnectedEvent e) {
        try {
            if (wantedChannel == -1) {
                client.disconnect();
                return;
            }

            client.setDescription("");
            client.subscribeAll();
            client.joinChannel(wantedChannel, "");
        }catch (Exception f){
            f.printStackTrace();
        }
    }

    @Override
    public void onDisconnected(DisconnectedEvent e) {
        //System.out.println("Disconnected");
        //wantedChannel = -1;
        //resetPlayer();
        BotManager.getInstance().reloadBot(this);
    }

    public void addTrackToPlayer(final String query){
        if (player == null) {
            System.err.println("Track add failed, because there is no player");
            return;
        }

        System.out.println(query);

        BotManager.getInstance().getAudioPlayerManager().loadItem(query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                player.addTrack(audioTrack);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                for(AudioTrack track: audioPlaylist.getTracks()){
                    player.addTrack(track);
                }
            }

            @Override
            public void noMatches() {
                System.out.println("NoMatches");
                //MessageManager.getInstance().createMusicSearchMessage(guild, botID, userID, query);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                System.out.println("Load Failed");
                //BotManager.getInstance().getRandomBot(guild).getUserById(userID).openPrivateChannel().complete().sendMessage("Something went wrong, please try again!").queue();
            }
        });
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        try {
            if (e.getClientId() != client.getClientId()) {
                ClientManager clientManager = ClientManager.getInstance();
                clientManager.forceUpdate();
                int clientsInChannel = clientManager.getUserCountbyChannelID(wantedChannel);
                if(clientsInChannel == 1) client.disconnect();
                return;
            }

            if (wantedChannel == -1 || e.getTargetChannelId() != wantedChannel)
                client.disconnect();
            return;
        } catch (Exception f){
            f.printStackTrace();
            return;
        }
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        ClientManager clientManager = ClientManager.getInstance();
        clientManager.forceUpdate();
        int clientsInChannel = clientManager.getUserCountbyChannelID(wantedChannel);
        if(clientsInChannel == 1){
            try {
                client.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
