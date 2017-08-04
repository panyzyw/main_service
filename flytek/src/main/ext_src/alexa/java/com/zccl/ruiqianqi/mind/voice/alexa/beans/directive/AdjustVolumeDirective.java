package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class AdjustVolumeDirective extends BaseDirective {
    public Payload payload;

    public AdjustVolumeDirective(){
        header.namespace = "Speaker";
        header.name = "AdjustVolume";
    }

    public class Payload{
        /**
         * The relative volume adjustment. A positive or negative long value used to increase or decrease volume in relation to the current volume setting.
         * Accepted values: Any value between -100 and 100, inclusive
         */
        public long volume;
    }

}
