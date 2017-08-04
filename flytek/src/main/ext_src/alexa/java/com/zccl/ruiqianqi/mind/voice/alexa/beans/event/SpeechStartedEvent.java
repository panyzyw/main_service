package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 *
 * The SpeechStarted event should be sent to AVS after your client processes the Speak directive and
 * begins playback of synthesized speech
 */

public class SpeechStartedEvent extends BaseEvent {
    public Payload payload = new Payload();

    public SpeechStartedEvent(){
        header.namespace = "SpeechSynthesizer";
        header.name = "SpeechStarted";
    }

    public class Payload{
        /**
         * The opaque token provided by the Speak directive
         */
        public String token;
    }
}
