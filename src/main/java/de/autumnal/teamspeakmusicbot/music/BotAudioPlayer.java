package de.autumnal.teamspeakmusicbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.autumnal.teamspeakmusicbot.music.enums.AudioPlayerState;
import de.autumnal.teamspeakmusicbot.music.enums.Playmode;

import java.util.Stack;
import java.util.Vector;

public class BotAudioPlayer extends AudioEventAdapter {
    private Playmode playmode;
    private final AudioPlayer player;
    private AudioPlayerState state;
    private Vector<AudioTrack> queue;
    private Stack<AudioTrack> old;
    private static final int VOLUME = 16;
    private static final int MAX_QUEUE = 50;
    private long queueChangeID;

    public BotAudioPlayer(AudioPlayerManager audioPlayerManager){
        queue = new Vector<>();
        old = new Stack<>();
        this.playmode = Playmode.NORMAL;
        this.player = audioPlayerManager.createPlayer();
        this.player.setVolume(VOLUME);
        this.player.addListener(this);

        this.state = AudioPlayerState.STOPPED;
        queueChangeID = 0;
    }

    public synchronized void addTrack(AudioTrack track){
        if(queue.size() >= MAX_QUEUE) return;
        AudioTrack nTrack = track.makeClone();

        if(queue.size() != 0){
            queue.add(nTrack);
            increaseQueueChangeID();
            return;
        }

        queue.add(nTrack);
        increaseQueueChangeID();

        player.setPaused(false);
        state = AudioPlayerState.PLAYING;
        
        player.startTrack(nTrack, false);
    }

    public synchronized void previousSong(){
        if(playmode == Playmode.LOOPALL){
            if(queue.size() == 0) return;
            AudioTrack lastTrack = queue.lastElement();
            queue.remove(lastTrack);
            queue.add(0, lastTrack.makeClone());
            increaseQueueChangeID();

            player.startTrack(queue.firstElement(), false);
            state = AudioPlayerState.PLAYING;
            return;
        }

        if(old.size() == 0 ) return;

        AudioTrack track = old.pop();

        if(queue.size() > 0) queue.set(0, queue.firstElement().makeClone());

        queue.add(0, track.makeClone());
        increaseQueueChangeID();

        player.startTrack(queue.firstElement(), false);
        state = AudioPlayerState.PLAYING;
    }

    public synchronized void nextSong(){
        player.stopTrack();

        if(queue.size() > 0) {
            if(playmode == Playmode.NORMAL || playmode == Playmode.LOOPONE) {
                old.push(queue.firstElement().makeClone());
                queue.remove(0);
            }else if(playmode == Playmode.LOOPALL){
                queue.add(queue.remove(0).makeClone());
            }
        }

        if(queue.size() == 0) {
            state = AudioPlayerState.STOPPED;
            increaseQueueChangeID();
            return;
        }else{
            queue.set(0, queue.firstElement().makeClone());
            increaseQueueChangeID();

            player.startTrack(queue.firstElement(),false);
            state = AudioPlayerState.PLAYING;
        }
    }

    public synchronized void forward(long number){
        if(queue.size() == 0) return;

        long pos = queue.get(0).getPosition();
        long dur = queue.get(0).getDuration();

        if(pos + number >= dur) return;

        if(pos + number > 0) queue.get(0).setPosition(pos + number);
        else queue.get(0).setPosition(0);
    }

    public synchronized void jump(long number){
        if(queue.size() == 0) return;

        if(queue.get(0).getDuration() < number) return;

        if(number > 0) queue.get(0).setPosition(number);
        else queue.get(0).setPosition(0);
    }

    public synchronized void pauseSong(){
        if(state == AudioPlayerState.PLAYING) {
            player.setPaused(true);
        }
    }

    public synchronized void resumeSong(){
        if(state == AudioPlayerState.PAUSED) {
            player.setPaused(false);
        }
    }

    private synchronized void increaseQueueChangeID(){
        if(queueChangeID == Long.MAX_VALUE)
            queueChangeID = 0;
        queueChangeID += 1;

        while(queue.size() > 50){
            try {
                queue.remove(51);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public synchronized long getLastQueueChange() {
        return queueChangeID;
    }

    public synchronized void changePlaymode(Playmode playmode){
        if(playmode != null) this.playmode = playmode;
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        state = AudioPlayerState.PAUSED;
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        state = AudioPlayerState.PLAYING;
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        player.setPaused(false);
        state = AudioPlayerState.PLAYING;
    }

    @Override
    public synchronized void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason == AudioTrackEndReason.REPLACED || endReason == AudioTrackEndReason.STOPPED) return;

        switch (playmode){
            case NORMAL:
                nextSong();
                break;
            case LOOPONE:
                queue.set(0, queue.firstElement().makeClone());

                player.startTrack(queue.firstElement(), false);
                break;
            case LOOPALL:
                queue.add(queue.firstElement().makeClone());
                queue.remove(0);
                queue.set(0, queue.firstElement().makeClone());
                increaseQueueChangeID();

                player.startTrack(queue.firstElement(), false);
                break;
            default:
        }
    }

    public void destroy(){
        player.destroy();
    }

    public String getPlayerStatusMessage(){
        StringBuilder sb = new StringBuilder();

        sb.append("\r\n[").append(state.toString().toUpperCase()).append("]\r\n")
                .append("Playmode: ").append(playmode.toString().toUpperCase()).append("\r\n")
                .append("Playlist length: ").append(queue.size()).append("/").append(MAX_QUEUE);

        return sb.toString();
    }

    public String getPlaylistString(){
        StringBuilder sb = new StringBuilder();
        AudioTrack[] playlist = getPlaylist();

        if(playlist.length == 0)
            return "[Empty Playlist]";

        sb.append("[").append(state.toString().toUpperCase()).append("] ");
        sb.append("[").append(playmode.toString().toUpperCase()).append("]");

        for(int i = 0; i < 10 && i < playlist.length; i++){
            AudioTrack song = playlist[i];
            sb.append("\r\n");
            if(i != 0) sb.append(i).append(". ");

            sb.append(song.getInfo().title.substring(0, 50))
                    .append(" [");
            if(i == 0) sb.append(longToStringTime(song.getPosition()))
                            .append("/");
            sb.append(longToStringTime(song.getDuration())).append("]");
        }

        return sb.toString();
    }

    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    public Playmode getPlaymode(){
        return playmode;
    }

    public AudioPlayerState getAudioPlayerState(){
        return state;
    }

    public AudioTrack[] getPlaylist(){
        Object[] objs = queue.toArray();
        AudioTrack[] tracks = new AudioTrack[objs.length];
        for(int i = 0; i < objs.length; i++){
            tracks[i] = (AudioTrack) objs[i];
        }

        return tracks;
    }

    public static String longToStringTime(long time){
        time = time / 1000;
        long minutes = time % 3600;
        long hours = ((time - minutes) / 3600);
        long secs = minutes % 60;
        minutes = ((minutes - secs) / 60);

        StringBuilder stringBuilder = new StringBuilder();
        if(hours > 0) {
            if(hours < 10) stringBuilder.append(0);
            stringBuilder.append(hours).append(":");
        }

        if(minutes < 10) stringBuilder.append(0);
        stringBuilder.append(minutes).append(":");

        if(secs < 10) stringBuilder.append(0);
        stringBuilder.append(secs);

        return stringBuilder.toString();
    }
}
