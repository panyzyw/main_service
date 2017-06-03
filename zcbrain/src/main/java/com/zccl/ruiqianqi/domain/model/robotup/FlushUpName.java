package com.zccl.ruiqianqi.domain.model.robotup;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_FLUSH;

/**
 * Created by ruiqianqi on 2017/3/10 0010.
 */

public class FlushUpName {
    private String cmd = A_FLUSH;
    private String rname;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }
}
