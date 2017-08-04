package com.zccl.ruiqianqi.mind.voice.alexa.beans.state;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/context">context</a>
 */

public class PlaybackState {

    public StateHeader header = new StateHeader();
    public Payload payload = new Payload();

    public PlaybackState(){
        header.namespace = "AudioPlayer";
        header.name = "PlaybackState";
    }

    public class Payload{
        public String token;
        public long offsetInMilliseconds;
        public String playerActivity;
    }
}
