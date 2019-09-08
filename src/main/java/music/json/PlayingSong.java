package music.json;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import music.enums.Playmode;

public class PlayingSong extends Song {
    public long duration;
    public long position;

    public PlayingSong(AudioTrack track){
        super(track);
        duration = track.getDuration();
        position = track.getPosition();
    }
}
