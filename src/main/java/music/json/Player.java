package music.json;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import music.BotAudioPlayer;
import music.enums.AudioPlayerState;
import music.enums.Playmode;

public class Player {
    public Playmode playmode;
    public AudioPlayerState playerState;
    public PlayingSong playingSong;
    public Song[] playlist;

    public Player(BotAudioPlayer audioPlayer, long queueChangeID, int maxQueue){
        playmode = audioPlayer.getPlaymode();
        playerState = audioPlayer.getAudioPlayerState();

        AudioTrack[] queue = audioPlayer.getPlaylist();
        playingSong = new PlayingSong(queue[0]);

        if(queueChangeID != audioPlayer.getLastQueueChange())
            playlist = Song.generateList(queue, maxQueue);
    }
}
