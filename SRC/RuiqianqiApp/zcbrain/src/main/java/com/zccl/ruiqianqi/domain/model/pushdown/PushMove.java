package com.zccl.ruiqianqi.domain.model.pushdown;

import static com.zccl.ruiqianqi.config.RemoteProtocol.B_MOVE;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class PushMove extends BasePush{

    private CommandMove commandMove;

    public CommandMove getCommandMove() {
        return commandMove;
    }

    public void setCommandMove(CommandMove commandMove) {
        this.commandMove = commandMove;
    }

    public static class CommandMove {
        private String cmd = B_MOVE;
        private String type;
        private String speed;

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

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }
    }
}
