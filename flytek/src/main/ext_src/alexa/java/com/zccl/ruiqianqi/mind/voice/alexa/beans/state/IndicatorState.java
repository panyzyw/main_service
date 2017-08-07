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
        /**
         * Indicates there are new or pending notifications that have not been communicated to the user.
         * Note: Any indicator that has not been cleared is considered enabled.
         */
        public boolean isEnabled;
        /**
         * Corresponds to the persistVisualIndicator value of the last SetIndicator directive received.
         * If persistVisualIndicator was true for the last directive received, upon reconnecting,
         * isVisualIndicatorPersisted must be true
         */
        public boolean isVisualIndicatorPersisted;
    }
}
