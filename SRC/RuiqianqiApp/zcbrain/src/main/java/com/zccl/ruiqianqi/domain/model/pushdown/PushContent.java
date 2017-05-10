package com.zccl.ruiqianqi.domain.model.pushdown;

import static com.zccl.ruiqianqi.config.RemoteProtocol.B_CONTENT_PUSH;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class PushContent extends BasePush {

    public static class BaseCommand{
        protected String cmd = B_CONTENT_PUSH;
        protected String type;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class CommandMedia extends BaseCommand{
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public static class CommandTxt extends BaseCommand{
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
