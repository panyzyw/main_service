package com.zccl.ruiqianqi.mind.voice.alexa.beans.state;

/**
 * Created by ruiqianqi on 2017/7/24 0024.
 * <a href="https://developer.amazon.com/public/solutions/alexa/alexa-voice-service/reference/context">context</a>
 *
 * RecognizerState is only required if your client uses Cloud-Based Wake Word Verification.
 * 云端唤醒才用
 */
public class RecognizerState {

    public StateHeader header = new StateHeader();
    public Payload payload = new Payload();

    public RecognizerState(){
        header.namespace = "Notifications";
        header.name = "IndicatorState";
        payload.wakeword = "ALEXA";
    }

    /**
     * Parameter	Description	                        Type
     * wakeword	   Identifies the current wake word.    string
     *             Accepted Value: "ALEXA"
     */
    public class Payload{
        public String wakeword;
    }
}
