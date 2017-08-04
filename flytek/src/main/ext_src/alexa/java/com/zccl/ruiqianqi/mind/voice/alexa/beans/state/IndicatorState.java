package com.zccl.ruiqianqi.mind.voice.alexa.beans.state;

/**
 * Created by ruiqianqi on 2017/7/24 0024.
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/context">context</a>
 */

public class IndicatorState {
    public StateHeader header = new StateHeader();
    public Payload payload = new Payload();

    public IndicatorState(){
        header.namespace = "Notifications";
        header.name = "IndicatorState";
    }

    public class Payload{
        public boolean isEnabled;
        public boolean isVisualIndicatorPersisted;
    }
}
