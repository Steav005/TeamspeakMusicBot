package de.autumnal.teamspeakmusicbot.manager;

import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import de.autumnal.teamspeakmusicbot.client.MasterBot;
import de.autumnal.teamspeakmusicbot.client.SlaveBot;
import com.sedmelluq.discord.lavaplayer.format.OpusAudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import de.autumnal.teamspeakmusicbot.config.Bot;
import de.autumnal.teamspeakmusicbot.config.Config;
import de.autumnal.teamspeakmusicbot.music.BotAudioPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class BotManager {
    private final AudioPlayerManager AUDIOPLAYERMANAGER;
    private static BotManager singleton;
    private MasterBot master;
    private Config config;
    private ArrayList<SlaveBot> SlaveBots;
    private String restAddress;
    public static final AudioDataFormat AUDIO_FORMAT = new OpusAudioDataFormat(2,48000,960);

    private BotManager(){
        this.AUDIOPLAYERMANAGER = new DefaultAudioPlayerManager();
        //Reduced to 1 channel so that the .provide() byte[] does not exceed ~500 entries
        //Apparently the byte array size gets normalized below 500 by changing the volume; Feels bad man
        AUDIOPLAYERMANAGER.getConfiguration().setOutputFormat(AUDIO_FORMAT);

        AudioSourceManagers.registerRemoteSources(AUDIOPLAYERMANAGER);
        //AudioSourceManagers.registerLocalSource(AUDIOPLAYERMANAGER);

        SlaveBots = new ArrayList<>();
    }

    public void start(Config conf){
        this.config = conf;
        restAddress = conf.restaddress;
        String masterIdentity = "";

        try {
            for (Bot b: conf.bots) {
                if (!b.master) continue;

                master = new MasterBot(new File(b.identity), conf.server.masterChannelID);
                master.setNickname(b.name);

                connectMaster();

                masterIdentity = b.identity;
                break;
            }
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        for(Bot b: conf.bots){
            if(b.identity.toLowerCase().equals(masterIdentity.toLowerCase())) continue;

            SlaveBot bot;
            try {
                bot = new SlaveBot(new File(b.identity), b);
                bot.setNickname(b.name);
                SlaveBots.add(bot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void connectMaster(){
        try {
            master.reloadNickname();
            if (config.server.password != null)
                master.connect(config.server.address, config.server.password);
            else master.connect(config.server.address);
        }catch(Exception e){
            e.printStackTrace();
            connectMaster();
        }
    }

    public int getChannelByUser(int userid){
        return master.getClientChannelID(userid);
    }

    public SlaveBot getBotByUser(int userId){
        try {
            int channelId = getChannelByUser(userId);
            if(channelId == -1) return null;

            for (SlaveBot b: SlaveBots){
                if(b.getWantedChannel() == channelId)
                    return b;
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }

    public void reloadBot(SlaveBot bot){
        try {
            SlaveBots.remove(bot);
            SlaveBots.add(new SlaveBot(new File(bot.bot.identity), bot.bot));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean BotJoinChannel(int channelID){
        ArrayList<SlaveBot> freeBots = new ArrayList<>();

        for(SlaveBot b: SlaveBots){
            if(b.getWantedChannel() == channelID) return false;
            if(b.getWantedChannel() == -1)
                freeBots.add(b);
        }

        if(freeBots.isEmpty()) return false;

        SlaveBot bot = freeBots.get(ThreadLocalRandom.current().nextInt(0, freeBots.size()));
        bot.setWantedChannel(channelID);

        try {
            if (config.server.password != null)
                bot.connect(config.server.address, config.server.password);
            else bot.connect(config.server.address);
        }catch (Exception e){
            e.printStackTrace();
            bot.setWantedChannel(-1);
            return false;
        }
        return true;
    }

    public String[] getBotUIDList(){
        SlaveBot[] botList = new SlaveBot[SlaveBots.size()];
        botList = SlaveBots.toArray(botList);
        String[] botsUIDList = new String[botList.length];

        for (int i = 0; i < botList.length; i++){
            botsUIDList[i] = botList[i].bot.identity;
        }

        return botsUIDList;
    }

    public BotAudioPlayer getNewPlayer(){
        return new BotAudioPlayer(AUDIOPLAYERMANAGER);
    }

    public AudioPlayerManager getAudioPlayerManager(){
        return AUDIOPLAYERMANAGER;
    }

    public String getRestAddress(){
        return restAddress;
    }

    public static BotManager getInstance(){
        if(singleton != null) return singleton;

        return createSingleton();
    }

    public static AudioConfiguration getAudioConfiguration(){
        return getInstance().AUDIOPLAYERMANAGER.getConfiguration();
    }

    private static synchronized BotManager createSingleton(){
        if(singleton == null) singleton = new BotManager();
        return singleton;
    }
}
