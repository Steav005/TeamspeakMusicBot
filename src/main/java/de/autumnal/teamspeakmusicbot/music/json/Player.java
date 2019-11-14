package de.autumnal.teamspeakmusicbot.music.json;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.autumnal.teamspeakmusicbot.music.BotAudioPlayer;
import de.autumnal.teamspeakmusicbot.music.enums.AudioPlayerState;
import de.autumnal.teamspeakmusicbot.music.enums.Playmode;

public class Player {
    public Playmode playmode;
    public AudioPlayerState playerState;
    public PlayingSong playingSong;
    public Song[] playlist;
    public long queueChangeID;

    public Player(BotAudioPlayer audioPlayer, long queueChangeID, int maxQueue){
        playmode = audioPlayer.getPlaymode();
        playerState = audioPlayer.getAudioPlayerState();

        this.queueChangeID = audioPlayer.getLastQueueChange();
        AudioTrack[] queue = audioPlayer.getPlaylist();

        //Check if queue is actually empty
        if(queue.length == 0){
            playingSong = null;
            playlist = new Song[0];
            return;
        }

        playingSong = new PlayingSong(queue[0]);

        if(queueChangeID != this.queueChangeID)
            playlist = Song.generateList(queue, maxQueue);
    }
}
