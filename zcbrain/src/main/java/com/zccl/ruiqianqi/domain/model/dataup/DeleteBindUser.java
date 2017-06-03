package com.zccl.ruiqianqi.domain.model.dataup;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_BINDER_USER_DELETE;

/**
 * Created by ruiqianqi on 2017/5/5 0005.
 */

public class DeleteBindUser {
    private String cmd = A_BINDER_USER_DELETE;

    private String id;
    private String robot_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRobot_id() {
        return robot_id;
    }

    public void setRobot_id(String robot_id) {
        this.robot_id = robot_id;
    }
}
