package com.zccl.ruiqianqi.domain.model.robotdown;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_FLUSH;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_FAILURE;

/**
 * Created by ruiqianqi on 2017/3/10 0010.
 */

public class FlushDown {
    // 0代表成功
    // -1代表失败
    private String ret = RET_FAILURE;
    // 数据来源
    private String from;
    // 命令
    private String cmd = A_FLUSH;
    // 这个对应的是Robot json数据
    private String Robot;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getRobot() {
        return Robot;
    }

    public void setRobot(String robot) {
        Robot = robot;
    }

}
