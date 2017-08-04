package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class ResetUserInactivityDirective extends BaseDirective {

    public Payload payload;

    public ResetUserInactivityDirective(){
        header.namespace = "System";
        header.name = "ResetUserInactivity";
    }

    public class Payload{

    }
}
