package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class PauseCommandIssuedMsg extends BaseEvent{

    public Payload payload = new Payload();

    public PauseCommandIssuedMsg(){
        header.namespace = "PlaybackController";
        header.name = "PauseCommandIssued";
    }

    public class Payload{

    }
    
}
