package com.zccl.ruiqianqi.domain.model.localpush;

import static com.zccl.ruiqianqi.config.LocalProtocol.LOGIN_PUSH;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_FAILURE;

/**
 * Created by ruiqianqi on 2017/3/14 0014.
 */

public class LocalLoginPush {

    // 登录成功返回0
    // 登录失败返回-1
    private String ret = RET_FAILURE;
    private String cmd = LOGIN_PUSH;
    // 这个是RID
    private String id;
    // 这个是RNAME
    private String name;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getRid() {
        return id;
    }

    public void setRid(String id) {
        this.id = id;
    }

    public String getRname() {
        return name;
    }

    public void setRname(String name) {
        this.name = name;
    }
}
