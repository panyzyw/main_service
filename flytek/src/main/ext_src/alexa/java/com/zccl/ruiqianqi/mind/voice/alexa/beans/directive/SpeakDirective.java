package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class SpeakDirective extends BaseDirective {
    public Payload payload;

    public SpeakDirective(){
        header.namespace = "SpeechSynthesizer";
        header.name = "Speak";
    }

    public class Payload{
        /**
         * A unique identifier for audio content. The URL always follows the prefix cid:
         * Example: cid:
         */
        public String url;
        /**
         * Provides the format of returned audio.
         * Accepted value: "AUDIO_MPEG"
         */
        public String format;
        /**
         * An opaque token that represents the current text-to-speech (TTS) object
         */
        public String token;
    }
}
