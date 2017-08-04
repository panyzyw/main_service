package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class SynchronizeStateMsg extends BaseEvent{

    public Payload payload = new Payload();

    public SynchronizeStateMsg(){
        header.namespace = "System";
        header.name = "SynchronizeState";
    }

    /**
     * An empty payload should be sent
     */
    public class Payload{

    }

}
