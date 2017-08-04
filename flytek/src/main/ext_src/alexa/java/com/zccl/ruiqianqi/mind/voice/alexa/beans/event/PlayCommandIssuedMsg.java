package com.zccl.ruiqianqi.mind.voice.alexa.beans.event;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class PlayCommandIssuedMsg extends BaseEvent{

    public Payload payload = new Payload();

    public PlayCommandIssuedMsg(){
        header.namespace = "PlaybackController";
        header.name = "PlayCommandIssued";
    }

    public class Payload{

    }

}
