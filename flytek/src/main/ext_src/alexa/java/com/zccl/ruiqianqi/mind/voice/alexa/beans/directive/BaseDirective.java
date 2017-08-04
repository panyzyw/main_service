package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * A catch-all Directive to classify return responses from the Amazon Alexa v20160207 API
 * Will handle calls to:
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechrecognizer">Speech Recognizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/alerts">Alerts</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/audioplayer">Audio Player</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/playbackcontroller">Playback Controller</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speaker">Speaker</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/speechsynthesizer">Speech Synthesizer</a>
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/system">System</a>
 */
public class BaseDirective {

    public Header header;

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
        public String dialogRequestId;
    }
}
