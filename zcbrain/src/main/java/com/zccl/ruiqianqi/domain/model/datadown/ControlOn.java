package com.zccl.ruiqianqi.domain.model.datadown;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_CONTROL_ON;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_FAILURE;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class ControlOn {
    // 0代表成功
    // -1代表失败
    private String ret = RET_FAILURE;
    // 控制者
    private String controller;
    // 命令
    private String cmd = A_CONTROL_ON;
    // 这个对应的是Robot json数据
    private String Robot;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
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
