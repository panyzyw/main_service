package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class SettingsUpdatedEvent extends BaseEvent {
    public Payload payload = new Payload();

    public SettingsUpdatedEvent(){
        header.namespace = "Settings";
        header.name = "SettingsUpdated";
    }

    public class Payload{
        /**
         * Accepted Keys	Accepted Values
         * locale	        en-US, en-GB, de-DE
         */
        public Map<String, String> settings = new HashMap<>();
    }
}
