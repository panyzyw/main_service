package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
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
public class Event {

    Header header;
    Payload payload;

    /**
     * 识别上行事件
     * @return
     */
    public static String getSpeechRecognizerEvent() {
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechRecognizer")
                .setHeaderName("Recognize")
                .setHeaderMessageId(getUuid())
                .setHeaderDialogRequestId("dialogRequest-321")
                .setPayloadFormat("AUDIO_L16_RATE_16000_CHANNELS_1")
                .setPayloadProfile("FAR_FIELD");
        return builder.toJson();
    }

    /**
     * 等待说话超时事件
     * @return
     */
    public static String getExpectSpeechTimedOutEvent() {
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechRecognizer")
                .setHeaderName("ExpectSpeechTimedOut")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }

    public static String getVolumeChangedEvent(long volume, boolean isMute) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("Speaker")
                .setHeaderName("VolumeChanged")
                .setHeaderMessageId(getUuid())
                .setPayloadVolume(volume)
                .setPayloadMuted(isMute);
        return builder.toJson();
    }

    public static String getMuteEvent(boolean isMute) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("Speaker")
                .setHeaderName("VolumeChanged")
                .setHeaderMessageId(getUuid())
                .setPayloadMuted(isMute);
        return builder.toJson();
    }



    public static String getSpeechNearlyFinishedEvent(String token, long offsetInMilliseconds) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechSynthesizer")
                .setHeaderName("PlaybackNearlyFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token)
                .setPlayloadOffsetInMilliseconds(offsetInMilliseconds);
        return builder.toJson();
    }

    public static String getPlaybackNearlyFinishedEvent(String token, long offsetInMilliseconds) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("AudioPlayer")
                .setHeaderName("PlaybackNearlyFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token)
                .setPlayloadOffsetInMilliseconds(offsetInMilliseconds);
        return builder.toJson();
    }

    public static String getSetAlertSucceededEvent(String token) {
        return getAlertEvent(token, "SetAlertSucceeded");
    }

    public static String getSetAlertFailedEvent(String token) {
        return getAlertEvent(token, "SetAlertFailed");
    }

    public static String getDeleteAlertSucceededEvent(String token) {
        return getAlertEvent(token, "DeleteAlertSucceeded");
    }

    public static String getDeleteAlertFailedEvent(String token) {
        return getAlertEvent(token, "DeleteAlertFailed");
    }

    public static String getAlertStartedEvent(String token) {
        return getAlertEvent(token, "AlertStarted");
    }

    public static String getAlertStoppedEvent(String token) {
        return getAlertEvent(token, "AlertStopped");
    }

    public static String getAlertEnteredForegroundEvent(String token) {
        return getAlertEvent(token, "AlertEnteredForeground");
    }

    public static String getAlertEnteredBackgroundEvent(String token) {
        return getAlertEvent(token, "AlertEnteredBackground");
    }

    private static String getAlertEvent(String token, String type) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("Alerts")
                .setHeaderName(type)
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getSpeechStartedEvent(String token) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechSynthesizer")
                .setHeaderName("SpeechStarted")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getSpeechFinishedEvent(String token) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("SpeechSynthesizer")
                .setHeaderName("SpeechFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getPlaybackStartedEvent(String token) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("AudioPlayer")
                .setHeaderName("PlaybackStarted")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getPlaybackFinishedEvent(String token) {
        Builder builder = new Builder();
        builder.setHeaderNamespace("AudioPlayer")
                .setHeaderName("PlaybackFinished")
                .setHeaderMessageId(getUuid())
                .setPayloadToken(token);
        return builder.toJson();
    }

    public static String getSynchronizeStateEvent() {
        Builder builder = new Builder();
        builder.setHeaderNamespace("System")
                .setHeaderName("SynchronizeState")
                .setHeaderMessageId(getUuid());
        return builder.toJson();
    }

    private static String getUuid() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public static class Header {

        String namespace;
        String name;
        String messageId;
        String dialogRequestId;

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getDialogRequestId() {
            return dialogRequestId;
        }

        public void setDialogRequestId(String dialogRequestId) {
            this.dialogRequestId = dialogRequestId;
        }
    }

    /**
     * 事件共用的携带体
     */
    public static class Payload {
        String token;
        String profile;
        String format;
        boolean muted;
        long volume;
        long offsetInMilliseconds;

        public String getProfile() {
            return profile;
        }

        public String getFormat() {
            return format;
        }

    }

    /**
     * 事件包装
     */
    public static class EventWrapper {
        Event event;
        List<Event> context;

        public Event getEvent() {
            return event;
        }

        public List<Event> getContext() {
            return context;
        }

        public String toJson() {
            return new Gson().toJson(this) + "\n";
        }
    }

    /**
     * 建造者模式
     */
    public static class Builder {
        // 上下文状态
        List<Event> context = new ArrayList<>();
        // 事件体
        Event event = new Event();
        // 事件头
        Header header = new Header();
        // 携带的数据
        Payload payload = new Payload();

        public Builder() {
            event.setPayload(payload);
            event.setHeader(header);
        }

        public EventWrapper build() {
            EventWrapper wrapper = new EventWrapper();
            wrapper.event = event;

            if (context != null && !context.isEmpty() && !(context.size() == 1 && context.get(0) == null)) {
                wrapper.context = context;
            }

            return wrapper;
        }

        public String toJson() {
            return build().toJson();
        }

        public Builder setContext(List<Event> context) {
            if (context == null) {
                return this;
            }
            this.context = context;
            return this;
        }

        public Builder setHeaderNamespace(String namespace) {
            header.namespace = namespace;
            return this;
        }

        public Builder setHeaderName(String name) {
            header.name = name;
            return this;
        }

        public Builder setHeaderMessageId(String messageId) {
            header.messageId = messageId;
            return this;
        }

        public Builder setHeaderDialogRequestId(String dialogRequestId) {
            header.dialogRequestId = dialogRequestId;
            return this;
        }

        public Builder setPayloadProfile(String profile) {
            payload.profile = profile;
            return this;
        }

        public Builder setPayloadFormat(String format) {
            payload.format = format;
            return this;
        }

        public Builder setPayloadMuted(boolean muted) {
            payload.muted = muted;
            return this;
        }

        public Builder setPayloadVolume(long volume) {
            payload.volume = volume;
            return this;
        }

        public Builder setPayloadToken(String token) {
            payload.token = token;
            return this;
        }

        public Builder setPlayloadOffsetInMilliseconds(long offsetInMilliseconds) {
            payload.offsetInMilliseconds = offsetInMilliseconds;
            return this;
        }
    }
}


