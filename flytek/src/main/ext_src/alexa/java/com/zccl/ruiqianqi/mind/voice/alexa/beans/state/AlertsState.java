package com.zccl.ruiqianqi.mind.voice.alexa.beans.state;

import java.util.ArrayList;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/context">context</a>
 */

public class AlertsState {

    public StateHeader header = new StateHeader();
    public Payload payload = new Payload();

    public AlertsState(){
        header.namespace = "Alerts";
        header.name = "AlertsState";
    }

    public class Payload{
        public ArrayList<Alert> allAlerts = new ArrayList<>();
        public ArrayList<ActiveAlert> activeAlerts = new ArrayList<>();
    }

    public static class Alert{
        /**
         * Alert token returned by the Alexa Voice Service when the alert was set
         */
        public String token;
        /**
         * Identifies the alert type.
         * Accepted Values: TIMER or ALARM
         */
        public String type;
        /**
         * Time the alert is scheduled in ISO 8601 format
         */
        public String scheduledTime;
    }

    public static class ActiveAlert{
        /**
         * The token for alert that is currently firing
         */
        public String token;
        /**
         * Identifies the alert type.
         * Accepted Values: TIMER or ALARM
         */
        public String type;
        /**
         * Time the alert is scheduled in ISO 8601 format.
         */
        public String scheduledTime;
    }

}
