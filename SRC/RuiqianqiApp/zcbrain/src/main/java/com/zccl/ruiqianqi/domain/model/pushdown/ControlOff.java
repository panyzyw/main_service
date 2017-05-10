package com.zccl.ruiqianqi.domain.model.pushdown;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_CONTROL_OFF;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class ControlOff {
    private String cmd = A_CONTROL_OFF;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
