package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class BodyTemplate2 extends BaseDirective {
    public Payload payload;

    public BodyTemplate2(){
        header.namespace = "TemplateRuntime";
        header.name = "RenderTemplate";
    }

    public class Payload{
        /**
         * An opaque token that uniquely identifies the alert
         */
        public String token;
        public String type = "BodyTemplate2";
        /**
         * Body text for the card
         */
        public String textField;
        public BodyTemplate1.Title title;
        public BodyTemplate1.Image skillIcon;
        public BodyTemplate1.Image image;
    }

}
