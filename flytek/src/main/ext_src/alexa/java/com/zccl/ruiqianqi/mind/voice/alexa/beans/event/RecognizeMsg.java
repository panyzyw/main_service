package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

import com.zccl.ruiqianqi.tools.CheckUtils;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class RecognizeMsg {

    public Header header = new Header();
    public Payload payload = new Payload();

    public RecognizeMsg(){
        header.namespace = "SpeechRecognizer";
        header.name = "Recognize";

        payload.profile = "FAR_FIELD";
        payload.format = "AUDIO_L16_RATE_16000_CHANNELS_1";
    }

    public class Header{
        public String namespace;
        public String name;
        /**
         * A unique ID used to represent a specific message.
         */
        public String messageId;
        /**
         * A unique identifier that your client must create for each Recognize event sent to Alexa.
         * This parameter is used to correlate directives sent in response to a specific Recognize event.
         */
        public String dialogRequestId = CheckUtils.getRandomString();;
    }

    public class Payload{
        /**
         * Accepted values: "CLOSE_TALK", "NEAR_FIELD", "FAR_FIELD".
         */
        public String profile;
        /**
         * Accepted value: "AUDIO_L16_RATE_16000_CHANNELS_1"
         */
        public String format;

        /**
         * Includes information about how an interaction with AVS was initiated.
         * IMPORTANT: initiator is required
         * i)  for wake word enabled products that use cloud-based wake word verification, and
         * ii) when it is included in an ExpectSpeech directive.
         */
        public Initiator initiator = new Initiator();
    }

    private static class Initiator{
        /**
         * Represents the action taken by the user to start streaming audio to AVS.
         * Accepted values: "PRESS_AND_HOLD", "TAP", and "WAKEWORD".
         */
        private String type;
        private Payload_ payload = new Payload_();
    }

    private static class Payload_{
        private WakeWordIndices wakeWordIndices = new WakeWordIndices();
    }

    /**
     * This object is only required for wake word enabled products that use cloud-based wake word verification.
     */
    private static class WakeWordIndices{
        private long startIndexInSamples;
        private long endIndexInSamples;
    }

}
