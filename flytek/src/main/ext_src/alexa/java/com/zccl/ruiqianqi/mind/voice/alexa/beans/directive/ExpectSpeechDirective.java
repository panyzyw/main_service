package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class ExpectSpeechDirective extends BaseDirective {
    public Payload payload;

    public ExpectSpeechDirective(){
        header.namespace = "SpeechRecognizer";
        header.name = "ExpectSpeech";
    }

    public class Payload{
        /**
         * Specifies, in milliseconds, how long your client should wait for the microphone to open and
         * begin streaming user speech to AVS. If the microphone is not opened within the specified timeout window,
         * then the ExpectSpeechTimedOut event must be sent.
         */
        public long timeoutInMilliseconds;

        /**
         * An opaque string passed from AVS to your client.
         * This object must be sent back to AVS as the initiator in the subsequent Recognize event
         */
        public String initiator;
    }
}
