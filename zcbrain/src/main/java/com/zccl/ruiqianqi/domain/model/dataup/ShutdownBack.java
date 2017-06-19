package com.zccl.ruiqianqi.domain.model.dataup;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_ORDER_CALLBACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_LOG_COLLECT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PUSH_TIMED_SHUTDOWN;

/**
 * Created by ruiqianqi on 2017/3/22 0022.
 */

public class ShutdownBack {

    // 关机
    public static final int ACTION_SHUTDOWN = 0;
    // 重启
    public static final int ACTION_REBOOT = 1;

    private String cmd = A_ORDER_CALLBACK;
    private String command;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public static class Shutdown{
        private String cmd = B_PUSH_TIMED_SHUTDOWN;
        private int countdownTime = 0;
        private int type = 0;

        public int getCountdownTime() {
            return countdownTime;
        }

        public void setCountdownTime(int countdownTime) {
            this.countdownTime = countdownTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

}
