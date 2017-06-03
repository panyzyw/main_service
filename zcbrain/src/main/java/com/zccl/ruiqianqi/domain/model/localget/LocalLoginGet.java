package com.zccl.ruiqianqi.domain.model.localget;

import static com.zccl.ruiqianqi.config.LocalProtocol.LOGIN_GET;

/**
 * Created by ruiqianqi on 2017/3/14 0014.
 */

public class LocalLoginGet {

    private String source;
    private String cmd = LOGIN_GET;
    private int id;
    private String role;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
