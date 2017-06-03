package com.zccl.ruiqianqi.domain.model.localget;

import static com.zccl.ruiqianqi.config.LocalProtocol.REMOVE_FRIEND_GET;

/**
 * Created by ruiqianqi on 2017/3/17 0017.
 */

public class LocalDelFriendGet {
    private String number;
    private String type;
    private String cmd = REMOVE_FRIEND_GET;
    private String id;
    private String role;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
