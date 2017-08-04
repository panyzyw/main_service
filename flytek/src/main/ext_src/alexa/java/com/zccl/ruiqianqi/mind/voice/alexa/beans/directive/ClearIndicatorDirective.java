package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class ClearIndicatorDirective extends BaseDirective {
    public Payload payload;

    public ClearIndicatorDirective(){
        header.namespace = "Notifications";
        header.name = "ClearIndicator";
    }

    public class Payload{

    }

}
