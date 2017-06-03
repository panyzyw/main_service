package com.zccl.ruiqianqi.domain.model.datadown;

import com.zccl.ruiqianqi.config.RemoteProtocol;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class PushText extends BasePush {

    private CommandText commandText;

    public CommandText getCommandText() {
        return commandText;
    }

    public void setCommandText(CommandText commandText) {
        this.commandText = commandText;
    }

    public static class CommandText{
        /**
         * {@link RemoteProtocol#B_TEXT_QUESTION}
         * {@link RemoteProtocol#B_TEXT_TALK}
         */
        private String cmd;
        private String type;

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
}
