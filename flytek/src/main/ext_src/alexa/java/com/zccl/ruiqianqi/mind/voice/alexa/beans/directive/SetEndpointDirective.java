package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/26 0026.
 */

public class SetEndpointDirective extends BaseDirective {

    public Payload payload;

    public SetEndpointDirective(){
        header.namespace = "System";
        header.name = "SetEndpoint";
    }

    public class Payload{
        /**
         * The AVS endpoint URL that supports your user's country settings. The endpoint URL may include the protocol and/or port.
         * For example: https://avs-alexa-na.amazon.com
         */
        public String endpoint;
    }
}
