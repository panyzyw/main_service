package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class SetMuteDirective extends BaseDirective {
    public Payload payload;

    public SetMuteDirective(){
        header.namespace = "Speaker";
        header.name = "SetMute";
    }

    public class Payload{
        /**
         * A boolean value is used to mute/unmute a product's speaker.
         * The value is true when the speaker is muted, and false when unmuted
         */
        public boolean mute;
    }

}
