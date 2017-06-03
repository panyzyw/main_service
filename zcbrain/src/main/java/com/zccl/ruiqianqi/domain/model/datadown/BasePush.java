package com.zccl.ruiqianqi.domain.model.datadown;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_ORDER_PUSH;

/**
 * Created by ruiqianqi on 2017/3/22 0022.
 */

public class BasePush {
    protected String time_server;
    protected String cmd = A_ORDER_PUSH;
    protected String time_app;
    protected String command;

    public String getTime_server() {
        return time_server;
    }

    public void setTime_server(String time_server) {
        this.time_server = time_server;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getTime_app() {
        return time_app;
    }

    public void setTime_app(String time_app) {
        this.time_app = time_app;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
