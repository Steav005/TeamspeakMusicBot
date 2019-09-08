package text;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import music.BotAudioPlayer;

public class MessageBuilder {
    public static final int MAX_SONG_LETTER = 50;

    public static String songListBuilder(BotAudioPlayer player){
        StringBuilder sb = new StringBuilder();

        switch (player.getAudioPlayerState()){
            case PLAYING:
                sb.append("[PLAYING]");
                break;
            case PAUSED:
                sb.append("[PAUSED]");
                break;
            case STOPPED:
                sb.append("[NO SONGS IN PLAYLIST]");
                break;
        }

        switch (player.getPlaymode()){
            case LOOPONE:
                sb.append(" [LOOP ONE]");
                break;
            case LOOPALL:
                sb.append(" [LOOP ALL]");
                break;
            default:
                break;
        }

        AudioTrack[] playlist = player.getPlaylist();
        if(playlist.length > 0){
            String title = playlist[0].getInfo().title;
            if(title.length() > MAX_SONG_LETTER) title = title.substring(0, MAX_SONG_LETTER-3) + "...";
            sb.append("\n")
                    .append(title)
                    .append(" [")
                    .append(longToStringTime(playlist[0].getPosition()))
                    .append("/")
                    .append(longToStringTime(playlist[0].getDuration()))
                    .append("]");

            for (int i = 1; i < 11 && i < playlist.length; i++){
                title = playlist[i].getInfo().title;
                if(title.length() > MAX_SONG_LETTER) title = title.substring(0, MAX_SONG_LETTER-3) + "...";
                sb.append("\n")
                        .append(i)
                        .append(". ")
                        .append(title)
                        .append(" [")
                        .append(longToStringTime(playlist[i].getDuration()))
                        .append("]");
            }
        }
        return sb.toString();
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
