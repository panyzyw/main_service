package com.zccl.ruiqianqi.domain.model.datadown;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class PushRemind extends BasePush{

    private CommandRemind commandRemind;

    public CommandRemind getCommandRemind() {
        return commandRemind;
    }

    public void setCommandRemind(CommandRemind commandRemind) {
        this.commandRemind = commandRemind;
    }

    public static class CommandRemind{
        private String cmd;
        private String id;
        private String time;
        private String title;
        private String content;
        private String seq;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSeq() {
            return seq;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }
    }
}
