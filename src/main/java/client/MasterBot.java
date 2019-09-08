package client;

import com.github.manevolent.ts3j.api.TextMessageTargetMode;
import com.github.manevolent.ts3j.event.ClientMovedEvent;
import com.github.manevolent.ts3j.event.ConnectedEvent;
import com.github.manevolent.ts3j.event.TextMessageEvent;
import manager.BotManager;

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

        BotManager.getInstance().BotJoinChannel(getClientChannelID(e.getInvokerId()));
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
