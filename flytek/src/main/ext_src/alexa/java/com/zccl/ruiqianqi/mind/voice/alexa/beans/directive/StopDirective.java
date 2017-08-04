package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class StopDirective extends BaseDirective {
    public Payload payload;

    public StopDirective(){
        header.namespace = "AudioPlayer";
        header.name = "Stop";
    }

    public class Payload{

    }
}
