package de.autumnal.teamspeakmusicbot.music.json;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class Song {
    public String name;
    public String uri;
    public long length;

    public Song(String name, String uri, long length){
        this.name = name;
        this.uri = uri;
        this.length = length;
    }

    public static Song[] generateList(AudioTrack[] tracks, int length){
        Song[] songs = new Song[Math.min(length, tracks.length - 1)]; //Minus Playing Track
        for(int i = 0; i < songs.length; i++){
            AudioTrack track = tracks[i + 1]; //Minus Playing Track
            songs[i] = new Song(track.getInfo().title, track.getInfo().uri, track.getDuration());
        }
        return songs;
    }
}
