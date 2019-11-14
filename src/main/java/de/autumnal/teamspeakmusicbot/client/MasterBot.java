package de.autumnal.teamspeakmusicbot.client;

import com.github.manevolent.ts3j.api.TextMessageTargetMode;
import com.github.manevolent.ts3j.event.ClientMovedEvent;
import com.github.manevolent.ts3j.event.ConnectedEvent;
import com.github.manevolent.ts3j.event.DisconnectedEvent;
import com.github.manevolent.ts3j.event.TextMessageEvent;
import de.autumnal.teamspeakmusicbot.manager.BotManager;
import de.autumnal.teamspeakmusicbot.manager.Command;
import de.autumnal.teamspeakmusicbot.music.enums.Playmode;
import de.autumnal.teamspeakmusicbot.rest.JsonDataBaseLinker;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MasterBot extends TeamspeakBot {
    public final int BotChannelID;

    public MasterBot(File identityFile, int botChannelID) throws IOException, TimeoutException {
        super(identityFile);
        BotChannelID = botChannelID;
    }

    @Override
    public void onTextMessage(TextMessageEvent e) {
        if(e.getTargetMode() != TextMessageTargetMode.CLIENT
                || e.getMessage().isEmpty()
                || e.getInvokerId() == client.getClientId()) return;

        //BotManager.getInstance().BotJoinChannel(getClientChannelID(e.getInvokerId()));
        SlaveBot bot = BotManager.getInstance().getBotByUser(e.getInvokerId());

        if(e.getMessage().startsWith("!"))
            handleCommand(e, bot);
        else
            bot.addTrackToPlayer(e.getInvokerId(), e.getMessage().replace("[URL]", "").replace("[/URL]", ""));
    }

    @Override
    public void onDisconnected(DisconnectedEvent e) {
        //Try to reconnect
        BotManager.getInstance().connectMaster();
    }

    private void handleCommand(TextMessageEvent e, SlaveBot bot){
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
                if(bot == null) return;
                try {
                    client.sendPrivateMessage(e.getInvokerId(), bot.getPlayer().getPlayerStatusMessage());
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                return;
            case JOIN:
                BotManager.getInstance().BotJoinChannel(getClientChannelID(e.getInvokerId()));
                return;
            case LEAVE:
                try {
                    bot.disconnect();
                } catch (Exception f){
                    //Ignore
                }
                return;
            case NEXT:
                if(bot == null) return;
                bot.getPlayer().nextSong();
                return;
            case PREV:
                if(bot == null) return;
                bot.getPlayer().previousSong();
                return;
            case PAUSE:
                if(bot == null) return;
                bot.getPlayer().pauseSong();
                return;
            case RESUME:
                if(bot == null) return;
                bot.getPlayer().resumeSong();
                return;
            case PLAYMODE:
                Playmode mode = Playmode.valueOf(cmd[1].toUpperCase());
                if(bot == null) return;
                bot.getPlayer().changePlaymode(mode);
                return;
            case FORWARD:
                if(bot == null) return;
                bot.getPlayer().forward((long)(Double.parseDouble(cmd[1]) * 1000));
                return;
            case JUMP:
                if(bot == null) return;
                bot.getPlayer().jump((long)(Double.parseDouble(cmd[1]) * 1000));
                return;
            case TOKEN:
                try {
                    client.sendPrivateMessage(e.getInvokerId(), JsonDataBaseLinker.getInstance().getTokenFromUserID(e.getInvokerId()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            case NEWTOKEN:
                try{
                    client.sendPrivateMessage(e.getInvokerId(), JsonDataBaseLinker.getInstance().addUser(e.getInvokerId()));
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
            //    if(bot == null) return;
            //    try {
            //        client.sendPrivateMessage(e.getInvokerId(), bot.getPlayer().getPlaylistString());
            //    } catch (Exception ex) {
            //        ex.printStackTrace();
            //    }
            default: //Add case
                if(bot == null) return;

                bot.addTrackToPlayer(e.getInvokerId(), e.getMessage());
                return;
        }
    }

    public void joinBotChannel(){
        if(getCurrentChannelID() == BotChannelID) return;
        try {
            client.joinChannel(BotChannelID, "");
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    @Override
    public void onConnected(ConnectedEvent e) {
        try {
            client.subscribeAll();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        joinBotChannel();
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        if(e.getClientId() != client.getClientId()) return;

        joinBotChannel();
    }
}
