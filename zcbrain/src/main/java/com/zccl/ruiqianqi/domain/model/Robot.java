package com.zccl.ruiqianqi.domain.model;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_DEFAULT_ID;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_DEFAULT_SID;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_DEFAULT_RID;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 */

public class Robot implements Cloneable{
    private String controller;
    private String id = STATE_DEFAULT_ID;
    private String serial = STATE_DEFAULT_SID;
    private String rid = STATE_DEFAULT_RID;
    private String rname;
    private boolean online;
    private int battery;
    private String addr;
    private String version;

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
