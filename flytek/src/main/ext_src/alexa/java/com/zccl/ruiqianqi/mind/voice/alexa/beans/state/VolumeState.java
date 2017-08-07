package com.zccl.ruiqianqi.mind.voice.alexa.beans.state;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/context">context</a>
 */

public class VolumeState {

    public StateHeader header = new StateHeader();
    public Payload payload = new Payload();

    public VolumeState(){
        header.namespace = "Speaker";
        header.name = "VolumeState";
    }

    public class Payload{
        /**
         * Identifies current speaker volume.
         * Accepted Values: 0 to 100
         */
        public long volume;
        /**
         * Identifies mute state of the client's speaker
         */
        public boolean muted;
    }
}
