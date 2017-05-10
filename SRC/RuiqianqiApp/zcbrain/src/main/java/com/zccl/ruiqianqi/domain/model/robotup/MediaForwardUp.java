package com.zccl.ruiqianqi.domain.model.robotup;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_MEDIA_FORWARD_2_SERVER;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class MediaForwardUp {
    private String cmd = A_MEDIA_FORWARD_2_SERVER;
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
}
