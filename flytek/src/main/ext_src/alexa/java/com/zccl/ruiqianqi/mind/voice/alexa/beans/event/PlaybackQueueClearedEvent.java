package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class PlaybackQueueClearedEvent extends BaseEvent {
    public Payload payload = new Payload();

    public PlaybackQueueClearedEvent(){
        header.namespace = "AudioPlayer";
        header.name = "PlaybackQueueCleared";
    }

    public class Payload{

    }
}
