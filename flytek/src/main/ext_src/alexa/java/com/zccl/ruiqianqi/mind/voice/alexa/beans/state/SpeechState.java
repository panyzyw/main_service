package com.zccl.ruiqianqi.mind.voice.alexa.beans.state;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/context">context</a>
 */

public class SpeechState {

    public StateHeader header = new StateHeader();
    public Payload payload = new Payload();

    public SpeechState(){
        header.namespace = "SpeechSynthesizer";
        header.name = "SpeechState";
    }

    public class Payload{
        /**
         * An opaque token provided in the Speak directive.
         */
        public String token;
        /**
         * Identifies the current offset of TTS in milliseconds.
         */
        public long offsetInMilliseconds;
        /**
         * Identifies the component state of SpeechSynthesizer.
         * Accepted Values: PLAYING or FINISHED
         *
         * Player Activity	Description
         * PLAYING	        Speech was playing.
         * FINISHED	        Speech was finished playing
         */
        public String playerActivity;
    }
}
