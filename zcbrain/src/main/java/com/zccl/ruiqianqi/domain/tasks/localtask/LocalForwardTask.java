package com.zccl.ruiqianqi.domain.tasks.localtask;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;

import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_MEDIA_FORWARD_2_SERVER;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class LocalForwardTask extends BaseLocalTask {
    @Override
    public void run() {
        // 转发给【服务器】
        MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
        forwardSocketEvent.setCmd(A_MEDIA_FORWARD_2_SERVER);
        forwardSocketEvent.setText(result);
        EventBus.getDefault().post(forwardSocketEvent);
    }
}
