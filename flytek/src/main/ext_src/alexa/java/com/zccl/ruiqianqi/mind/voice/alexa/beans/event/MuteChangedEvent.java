package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class MuteChangedEvent extends BaseEvent {
    public Payload payload = new Payload();

    public MuteChangedEvent(){
        header.namespace = "Speaker";
        header.name = "MuteChanged";
    }

    public class Payload{
        /**
         * The absolute volume level scaled from 0 (min) to 100 (max).
         * Accepted values: Any long value between 0 and 100
         */
        public long volume;
        /**
         * A boolean value is used to mute/unmute a product's speaker.
         * The value is true when the speaker is muted, and false when unmuted
         */
        public boolean muted;
    }
}
