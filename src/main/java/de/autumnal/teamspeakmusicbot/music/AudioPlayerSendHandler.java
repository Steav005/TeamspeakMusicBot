package de.autumnal.teamspeakmusicbot.music;

import com.github.manevolent.ts3j.audio.Microphone;
import com.github.manevolent.ts3j.enums.CodecType;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;

public class AudioPlayerSendHandler implements Microphone {
    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    public AudioPlayerSendHandler(AudioPlayer player){
        audioPlayer = player;
    }

    @Override
    public boolean isReady() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        return lastFrame != null;
    }

    @Override
    public CodecType getCodec() {
        return CodecType.OPUS_MUSIC;
    }

    @Override
    public byte[] provide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }

        byte[] data = lastFrame != null ? lastFrame.getData() : new byte[0];
        lastFrame = null;

        return data;
    }
}