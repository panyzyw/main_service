package com.zccl.ruiqianqi.domain.model.robotup;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_FLUSH;

/**
 * Created by ruiqianqi on 2017/3/10 0010.
 */

public class FlushUpBattery {
    private String cmd = A_FLUSH;
    private String battery;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }
}
