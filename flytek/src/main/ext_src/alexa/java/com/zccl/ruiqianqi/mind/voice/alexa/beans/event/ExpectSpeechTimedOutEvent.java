package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class ExpectSpeechTimedOutEvent extends BaseEvent{
    public Payload payload = new Payload();

    public ExpectSpeechTimedOutEvent(){
        header.namespace = "SpeechRecognizer";
        header.name = "ExpectSpeechTimedOut";
    }

    /**
     * An empty payload should be sent
     */
    public class Payload{

    }
}
