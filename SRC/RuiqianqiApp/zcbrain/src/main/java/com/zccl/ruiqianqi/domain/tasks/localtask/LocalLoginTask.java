package com.zccl.ruiqianqi.domain.tasks.localtask;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.localpush.LocalLoginPush;

import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_DEFAULT_RID;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_SUCCESS;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class LocalLoginTask extends BaseLocalTask {

    @Override
    public void run() {
        StatePresenter sp = StatePresenter.getInstance();
        LocalLoginPush localLoginPush = new LocalLoginPush();
        if(STATE_LOGIN_SUCCESS.equals(sp.getRobotState())){
            Robot robotBak = sp.getRobot();
            if(null != robotBak && !STATE_DEFAULT_RID.equals(robotBak.getRid())){
                localLoginPush.setCmd(RET_SUCCESS);
                localLoginPush.setRname(robotBak.getRname());
                localLoginPush.setRid(robotBak.getRid());
            }
        }

        // 回复【视频APK】
        MindBusEvent.ForwardLocalEvent forwardLocalEvent = new MindBusEvent.ForwardLocalEvent();
        forwardLocalEvent.setText(new Gson().toJson(localLoginPush));
        EventBus.getDefault().post(forwardLocalEvent);

    }
}
