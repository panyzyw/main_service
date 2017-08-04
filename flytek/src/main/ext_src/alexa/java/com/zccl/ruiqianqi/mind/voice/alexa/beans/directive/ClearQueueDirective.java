package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class ClearQueueDirective extends BaseDirective {
    public Payload payload;

    public ClearQueueDirective(){
        header.namespace = "AudioPlayer";
        header.name = "ClearQueue";
    }

    public class Payload{
        /**
         * A string value used to determine clear queue behavior.
         * Accepted values: CLEAR_ENQUEUED and CLEAR_ALL
         */
        public String clearBehavior;
    }
}
