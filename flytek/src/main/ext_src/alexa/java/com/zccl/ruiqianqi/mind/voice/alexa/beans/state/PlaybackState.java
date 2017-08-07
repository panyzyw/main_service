package com.zccl.ruiqianqi.mind.voice.alexa.beans.state;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/context">context</a>
 */

public class PlaybackState {

    public StateHeader header = new StateHeader();
    public Payload payload = new Payload();

    public PlaybackState(){
        header.namespace = "AudioPlayer";
        header.name = "PlaybackState";
    }

    public class Payload{
        /**
         * An opaque token provided in the Play directive
         */
        public String token;
        /**
         * Identifies a track's current offset in milliseconds
         */
        public long offsetInMilliseconds;
        /**
         * Identifies the component state of AudioPlayer.
         * Accepted Values: IDLE, PLAYING, STOPPED, PAUSED, BUFFER_UNDERRUN, and FINISHED.
         *
         * Player Activity	Description
         * IDLE	            Nothing was playing, no enqueued items.
         * PLAYING	        Stream was playing.
         * PAUSED	        Stream was paused.
         * BUFFER_UNDERRUN	Buffer underrun.
         * FINISHED	        Stream was finished playing.
         * STOPPED	        Stream was interrupted
         */
        public String playerActivity;
    }
}
