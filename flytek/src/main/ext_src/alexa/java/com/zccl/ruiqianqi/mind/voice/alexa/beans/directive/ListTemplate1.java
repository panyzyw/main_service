package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

import java.util.List;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class ListTemplate1 extends BaseDirective {
    public Payload payload;

    public ListTemplate1(){
        header.namespace = "TemplateRuntime";
        header.name = "RenderTemplate";
    }

    public class Payload{
        /**
         * An opaque token that uniquely identifies the alert
         */
        public String token;
        public String type = "ListTemplate1";
        /**
         * Body text for the card
         */
        public String textField;
        public BodyTemplate1.Title title;
        public BodyTemplate1.Image skillIcon;
        public List<Item> listItems;
    }

    public static class Item{
        /**
         * Left text field content
         */
        public String leftTextField;
        /**
         * Right text field content
         */
        public String rightTextField;
    }

}
