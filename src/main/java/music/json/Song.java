package music.json;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Song {
    public String title;
    public String uri;

    public Song(AudioTrack track){
        title = track.getInfo().title;
        uri = track.getInfo().uri;
    }
}
