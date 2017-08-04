package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class SetVolumeDirective extends BaseDirective {
    public Payload payload;

    public SetVolumeDirective(){
        header.namespace = "Speaker";
        header.name = "SetVolume";
    }

    public class Payload{
        /**
         * The absolute volume level scaled from 0 (min) to 100 (max).
         * Accepted values: Any value between 0 and 100
         */
        public long volume;
    }

}
