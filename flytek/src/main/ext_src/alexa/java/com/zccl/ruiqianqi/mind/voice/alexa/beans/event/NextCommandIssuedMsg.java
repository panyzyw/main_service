package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class NextCommandIssuedMsg extends BaseEvent{

    public Payload payload = new Payload();

    public NextCommandIssuedMsg(){
        header.namespace = "PlaybackController";
        header.name = "NextCommandIssued";
    }

    public class Payload{

    }
    
}
