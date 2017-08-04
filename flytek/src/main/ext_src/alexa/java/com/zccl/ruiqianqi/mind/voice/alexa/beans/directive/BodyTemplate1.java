package com.zccl.ruiqianqi.mind.voice.alexa.beans.directive;

import java.util.List;

/**
 * Created by ruiqianqi on 2017/7/27 0027.
 */

public class BodyTemplate1 extends BaseDirective {
    public Payload payload;

    public BodyTemplate1(){
        header.namespace = "TemplateRuntime";
        header.name = "RenderTemplate";
    }

    public class Payload{
        /**
         * An opaque token that uniquely identifies the alert
         */
        public String token;
        public String type = "BodyTemplate1";
        /**
         * Body text for the card
         */
        public String textField;
        public Title title;
        public Image skillIcon;
    }

    public static class Title{
        /**
         * The title.
         */
        public String mainTitle;
        /**
         * The subtitle.
         */
        public String subTitle;
    }

    /**
     * The icon/logo for the skill delivering metadata. This is an optional parameter for the content provider and
     * may not be included in the JSON (or may have a null value). The image structure contains information
     * such as url, size, widthPixels and heightPixels. For more information, see image structure below
     */
    public static class Image{
        public String contentDescription;
        public List<ImageInfo> sources;
    }

    public static class ImageInfo{
        public String url;
        public String darkBackgroundUrl;
        public String size;
        public long widthPixels;
        public long heightPixels;
    }
}
