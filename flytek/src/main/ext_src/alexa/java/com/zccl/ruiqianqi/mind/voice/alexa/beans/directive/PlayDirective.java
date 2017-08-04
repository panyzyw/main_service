package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class PlayDirective extends BaseDirective {
    public Payload payload;

    public PlayDirective(){
        header.namespace = "AudioPlayer";
        header.name = "Play";
    }

    public class Payload{
        public String playBehavior;
        public AudioItem audioItem;
    }

    public class AudioItem{
        public String audioItemId;
        public Stream stream;
    }

    public class Stream{
        public String url;
        public String streamFormat = "AUDIO_MPEG";
        public long offsetInMilliseconds;
        public String expiryTime;
        public String token;
        public String expectedPreviousToken;
        public ProgressReport progressReport;
    }

    public class ProgressReport{
        public long progressReportDelayInMilliseconds;
        public long progressReportIntervalInMilliseconds;
    }
}
