package de.autumnal.teamspeakmusicbot.client;

import com.github.manevolent.ts3j.api.Client;
import com.github.manevolent.ts3j.audio.Microphone;
import com.github.manevolent.ts3j.command.CommandException;
import com.github.manevolent.ts3j.event.ClientLeaveEvent;
import com.github.manevolent.ts3j.event.DisconnectedEvent;
import com.github.manevolent.ts3j.event.TS3Listener;
import com.github.manevolent.ts3j.identity.LocalIdentity;
import com.github.manevolent.ts3j.protocol.socket.client.LocalTeamspeakClientSocket;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class TeamspeakBot implements TS3Listener {
    protected final LocalTeamspeakClientSocket client;
    protected final LocalIdentity identity;
    protected String nickname;

    public TeamspeakBot(File identityFile) throws IOException, TimeoutException {
        client = new LocalTeamspeakClientSocket();

        //Identity
        identity = LocalIdentity.read(identityFile);
        client.setIdentity(identity);
        client.addListener(this);
        client.setNickname(identityFile.getName().replace(".ini", ""));
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
        reloadNickname();
    }

    public void reloadNickname(){
        try{
            if(client.getNickname().equals(nickname)) return;
            if(nickname != null && nickname.length() > 0)
                client.setNickname(nickname);
        }catch (Exception e){
            System.err.println("Couldn't change own Nickname");
            //e.printStackTrace();
        }
    }

    protected void setMicrophone(Microphone mic) {
        try {
            client.setMicrophone(mic);
        }catch (Exception e){
            System.err.println("Couldn't set Microphone");
            e.printStackTrace();
        }
    }

    public void connect(String address) throws IOException, TimeoutException {
        client.connect(address, 10000);
    }

    public void connect(String address, String password) throws IOException, TimeoutException {
        client.connect(address, password, 10000);
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        //This happens quite a lot.
        //subject to change
        reloadNickname();
    }

    public ArrayList<Client> getClientList(){
        try{
            Iterable<Client> clients = client.listClients();
            ArrayList<Client> clientsArray = new ArrayList<>();
            for(Client c : clients) {
                clientsArray.add(c);
            }
            return clientsArray;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
}
