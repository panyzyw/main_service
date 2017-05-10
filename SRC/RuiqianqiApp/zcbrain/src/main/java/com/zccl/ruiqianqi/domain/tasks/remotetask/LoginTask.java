package com.zccl.ruiqianqi.domain.tasks.remotetask;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.robotdown.LoginDown;
import com.zccl.ruiqianqi.tools.JsonUtils;

import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_FAILURE;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_SUCCESS;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class LoginTask extends BaseTask {
    @Override
    public void run() {
        LoginDown loginDown = JsonUtils.parseJson(result, LoginDown.class);
        StatePresenter sp = StatePresenter.getInstance();
        if(null != loginDown && RET_SUCCESS.equals(loginDown.getRet())) {
            sp.setRobotState(STATE_LOGIN_SUCCESS);
            Robot robot = JsonUtils.parseJson(loginDown.getRobot(), Robot.class);
            sp.setRobot(robot);

            // 登录成功，开始心跳
            MindBusEvent.LoginEvent loginEvent = new MindBusEvent.LoginEvent();
            loginEvent.setText(STATE_LOGIN_SUCCESS);
            if(null != robot) {
                loginEvent.setRid(robot.getRid());
            }
            EventBus.getDefault().post(loginEvent);

        }else {
            sp.setRobotState(STATE_LOGIN_FAILURE);
            sp.setRobot(null);

            // 登录失败
            MindBusEvent.LoginEvent loginEvent = new MindBusEvent.LoginEvent();
            loginEvent.setText(STATE_LOGIN_FAILURE);
            EventBus.getDefault().post(loginEvent);
        }
    }
}
