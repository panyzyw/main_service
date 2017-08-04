package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class PreviousCommandIssuedMsg extends BaseEvent{

    public Payload payload = new Payload();

    public PreviousCommandIssuedMsg(){
        header.namespace = "PlaybackController";
        header.name = "PreviousCommandIssued";
    }

    public class Payload{

    }
    
}
