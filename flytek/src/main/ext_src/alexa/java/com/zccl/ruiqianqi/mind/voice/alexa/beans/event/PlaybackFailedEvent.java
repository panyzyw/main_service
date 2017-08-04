package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class PlaybackFailedEvent extends BaseEvent {
    public Payload payload = new Payload();

    public PlaybackFailedEvent(){
        header.namespace = "AudioPlayer";
        header.name = "PlaybackFailed";
    }

    public class Payload{
        /**
         * An opaque token provided by the Play directive that represents the stream that failed to playback
         */
        public String token;

        public CurrentPlaybackState currentPlaybackState;
        public Error error;
    }

    public class CurrentPlaybackState{
        /**
         * An opaque token provided by the Play directive
         */
        public String token;
        /**
         * Identifies a track's current offset in milliseconds
         */
        public long offsetInMilliseconds;
        /**
         * Identifies the player state.
         * Accepted values: PLAYING, STOPPED, PAUSED, FINISHED, BUFFER_UNDERRUN, or IDLE
         */
        public String playerActivity;
    }

    public class Error{
        public String type;
        public String message;
    }

}
