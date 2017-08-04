package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class SetAlertDirective extends BaseDirective {
    public Payload payload;

    public SetAlertDirective(){
        header.namespace = "Alerts";
        header.name = "SetAlert";
    }

    public class Payload{
        /**
         * An opaque token that uniquely identifies the alert
         */
        public String token;

        /**
         * Identifies whether the alert is a timer or alarm.
         * Accepted values: TIMER or ALARM
         */
        public String type;

        /**
         * The scheduled time for an alert in ISO 8601 format
         */
        public String scheduledTime;
    }
}
