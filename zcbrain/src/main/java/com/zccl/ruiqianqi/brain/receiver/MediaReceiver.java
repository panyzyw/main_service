package com.zccl.ruiqianqi.brain.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.config.LocalProtocol.ACTION_MEDIA_SEND;
import static com.zccl.ruiqianqi.config.LocalProtocol.MEDIA_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_ROBOT_MEDIA_CONTROL;

/**
 * Created by ruiqianqi on 2017/6/2 0002.
 */

public class MediaReceiver extends BroadcastReceiver {

    // 类标志
    private static String TAG = MediaReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent){
            return;
        }
        LogUtils.e(TAG, "action = " + intent.getAction());

        // 往手机端发送多媒体命令
        if (ACTION_MEDIA_SEND.equals(intent.getAction())) {
            String media_result = intent.getStringExtra(MEDIA_RESULT);
            if(StringUtils.isEmpty(media_result))
                return;
            MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
            forwardSocketEvent.setCmd(B_ROBOT_MEDIA_CONTROL);
            forwardSocketEvent.setText(media_result);
            EventBus.getDefault().post(forwardSocketEvent);
        }

    }

}
