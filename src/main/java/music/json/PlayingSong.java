package music.json;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class PlayingSong extends Song {
    public long position;

    public PlayingSong(String name, String uri, long length, long position) {
        super(name, uri, length);
        this.position = position;
    }

    public PlayingSong(AudioTrack track){
        super(track.getInfo().title, track.getInfo().uri, track.getDuration());
        this.position = track.getPosition();
    }
}
