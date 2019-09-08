package music.json;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import music.BotAudioPlayer;
import music.enums.AudioPlayerState;
import music.enums.Playmode;

public class Player {
    public Playmode playmode;
    public AudioPlayerState audioPlayerState;
    public PlayingSong playingSong;
    public Song[] playlist;

    public Player(BotAudioPlayer player){
        playmode = player.getPlaymode();
        audioPlayerState = player.getAudioPlayerState();

        AudioTrack[] playlist = player.getPlaylist();
        if(playlist.length > 0) playingSong = new PlayingSong(playlist[0]);

        this.playlist = new Song[Math.min(10, playlist.length - 1)];
        for(int i = 1; i < 11 && i < playlist.length; i++){
            this.playlist[i-1] = new Song(playlist[i]);
        }
    }
}
