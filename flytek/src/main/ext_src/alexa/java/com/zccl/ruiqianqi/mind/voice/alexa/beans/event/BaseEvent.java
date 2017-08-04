package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

import java.util.UUID;

/**
 * A catch-all Event to classify return responses from the Amazon Alexa v20160207 API
 * Will handle calls to:
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechrecognizer">Speech Recognizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/alerts">Alerts</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/audioplayer">Audio Player</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/playbackcontroller">Playback Controller</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speaker">Speaker</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechsynthesizer">Speech Synthesizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/system">System</a>
 *
 * Value	    Optimal Listening Distance
 * CLOSE_TALK	0 to 2.5 ft.  PRESS_AND_HOLD	Audio stream initiated by pressing a button (physical or GUI) and terminated by releasing it.
 * NEAR_FIELD	0 to 5 ft.    TAP	            Audio stream initiated by the tap and release of a button (physical or GUI) and terminated when a StopCapture directive is received.
 * FAR_FIELD	0 to 20+ ft.  WAKEWORD	        Audio stream initiated by the use of a wake word and terminated when a StopCapture directive is received.
 */
public class BaseEvent {
    public Header header = new Header();

    public class Header{
        public String namespace;
        public String name;
        /**
         * A unique ID used to represent a specific message.
         */
        public String messageId = getUuid();
    }

    private static String getUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
