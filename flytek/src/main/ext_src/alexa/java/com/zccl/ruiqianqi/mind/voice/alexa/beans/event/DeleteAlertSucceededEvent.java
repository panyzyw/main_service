package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class DeleteAlertSucceededEvent extends BaseEvent {
    public Payload payload = new Payload();

    public DeleteAlertSucceededEvent(){
        header.namespace = "Alerts";
        header.name = "DeleteAlertSucceeded";
    }

    public class Payload{
        /**
         * An opaque token that uniquely identifies the alert
         */
        public String token;
    }
}
