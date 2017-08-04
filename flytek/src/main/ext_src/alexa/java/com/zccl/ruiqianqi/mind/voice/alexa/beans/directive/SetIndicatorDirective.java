package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class SetIndicatorDirective extends BaseDirective {
    public Payload payload;

    public SetIndicatorDirective(){
        header.namespace = "Notifications";
        header.name = "SetIndicator";
    }

    public class Payload{
        public boolean persistVisualIndicator;
        public boolean playAudioIndicator;
        public Asset asset;
    }

    public class Asset{
        public String assetId;
        public String url;
    }
}
