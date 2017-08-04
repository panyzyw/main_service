package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class DeleteAlertDirective extends BaseDirective {
    public Payload payload;

    public DeleteAlertDirective(){
        header.namespace = "Alerts";
        header.name = "DeleteAlert";
    }

    public class Payload{
        /**
         * An opaque token that uniquely identifies the alert
         */
        public String token;
    }
}
