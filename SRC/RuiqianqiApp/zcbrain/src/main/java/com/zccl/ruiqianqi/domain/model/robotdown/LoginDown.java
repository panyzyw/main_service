package com.zccl.ruiqianqi.domain.model.robotdown;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_LOGIN;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_FAILURE;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class LoginDown {
    // 0代表成功
    // -1代表失败
    private String ret = RET_FAILURE;
    // 时间戳
    private String now;
    // 命令
    private String cmd = A_LOGIN;
    // 这个对应的是Robot json数据
    private String Robot;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
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
