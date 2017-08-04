package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class StopCaptureDirective extends BaseDirective {

    public Payload payload;

    public StopCaptureDirective(){
        header.namespace = "SpeechRecognizer";
        header.name = "StopCapture";
    }

    public class Payload{

    }
}
