package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

import java.util.List;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class RenderPlayerInfoDirective extends BaseDirective {
    public Payload payload;

    public RenderPlayerInfoDirective(){
        header.namespace = "TemplateRuntime";
        header.name = "RenderPlayerInfo";
    }

    public class Payload{
        public String audioItemId;
        public Content content;
        public List<Control> controls;
    }

    public class Content{
        public String title;
        public String titleSubtext1;
        public String titleSubtext2;
        public String header;
        public String headerSubtext1;
        public long mediaLengthInMilliseconds;
        public BodyTemplate1.Image art;
        public Provider provider;
    }

    public class Provider{
        public String name;
        public BodyTemplate1.Image logo;
    }

    public class Control{
        public String type = "BUTTON";
        public String name;
        public boolean enabled;
        public boolean selected;
    }
}
