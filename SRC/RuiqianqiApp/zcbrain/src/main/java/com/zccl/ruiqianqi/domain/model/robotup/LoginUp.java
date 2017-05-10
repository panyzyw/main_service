package com.zccl.ruiqianqi.domain.model.robotup;

import android.os.Build;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_DEFAULT_ID;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_DEFAULT_SID;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_LOGIN;

/**
 * Created by ruiqianqi on 2017/3/8 0008.
 */

public class LoginUp {
    // 登录指令
    private String cmd = A_LOGIN;
    // 机器人ID
    private String id = STATE_DEFAULT_ID;
    // 机器人序列号
    private String serial = STATE_DEFAULT_SID;
    // 机器人版本
    private String version = Build.DISPLAY;
    // 电池电量
    private String battery = "";

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }
}
