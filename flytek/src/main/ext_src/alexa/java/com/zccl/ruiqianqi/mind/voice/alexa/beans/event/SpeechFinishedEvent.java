package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 *
 * The SpeechFinished event should be sent to AVS after your client processes the Speak directive and
 * when playback of synthesized speech is finished.
 */

public class SpeechFinishedEvent extends BaseEvent {
    public Payload payload = new Payload();

    public SpeechFinishedEvent(){
        header.namespace = "SpeechSynthesizer";
        header.name = "SpeechFinished";
    }

    public class Payload{
        /**
         * The opaque token provided by the Speak directive
         */
        public String token;
    }
}
