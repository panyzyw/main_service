package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class UserInactivityReportEvent extends BaseEvent {
    public Payload payload = new Payload();

    public UserInactivityReportEvent(){
        header.namespace = "System";
        header.name = "UserInactivityReport";
    }

    public class Payload{
        /**
         * Time in seconds since the last user interaction
         */
        public long inactiveTimeInSeconds;
    }
}
