package com.zccl.ruiqianqi.brain.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;

import static android.view.WindowManagerPolicy.ACTION_HDMI_PLUGGED;
import static android.view.WindowManagerPolicy.EXTRA_HDMI_PLUGGED_STATE;
import static com.zccl.ruiqianqi.config.LocalProtocol.ACTION_LOG_COLLECT;
import static com.zccl.ruiqianqi.config.LocalProtocol.ACTION_MAIN_RECV;
import static com.zccl.ruiqianqi.config.LocalProtocol.ACTION_MEDIA_SEND;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_COLLECT_FROM;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_COLLECT_RESULT;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MAIN_RECV_FROM;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MAIN_RECV_FUNCTION;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MAIN_RECV_RESULT;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MEDIA_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_ROBOT_MEDIA_CONTROL;
import static com.zccl.ruiqianqi.domain.model.dataup.ShutdownBack.ACTION_SHUTDOWN;

/**
 * Created by ruiqianqi on 2017/6/2 0002.
 */

public class OtherReceiver extends BroadcastReceiver {

    // 类标志
    private static String TAG = OtherReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent){
            return;
        }
        LogUtils.e(TAG, "action = " + intent.getAction());

        // 往手机端发送多媒体命令，这个其实是从其他应用接收
        if (ACTION_MEDIA_SEND.equals(intent.getAction())) {
            String media_result = intent.getStringExtra(KEY_MEDIA_RESULT);
            if(StringUtils.isEmpty(media_result))
                return;
            MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
            forwardSocketEvent.setCmd(B_ROBOT_MEDIA_CONTROL);
            forwardSocketEvent.setText(media_result);
            EventBus.getDefault().post(forwardSocketEvent);
        }

        // 插拔HDMI线
        else if(ACTION_HDMI_PLUGGED.equals(intent.getAction())){
            boolean state = intent.getBooleanExtra(EXTRA_HDMI_PLUGGED_STATE, false);
            MainBusEvent.HdmiEvent hdmiEvent = new MainBusEvent.HdmiEvent();
            hdmiEvent.setState(state);
            EventBus.getDefault().post(hdmiEvent);
        }

        // 用户操作日志收集
        else if(ACTION_LOG_COLLECT.equals(intent.getAction())){
            String collect_from = intent.getStringExtra(KEY_COLLECT_FROM);
            String collect_result = intent.getStringExtra(KEY_COLLECT_RESULT);

            LogUtils.e(TAG, "collect_from = " + collect_from);
            LogUtils.e(TAG, "collect_result = " + collect_result);

            if(StringUtils.isEmpty(collect_from) || StringUtils.isEmpty(collect_result))
                return;

            AppUtils.logCollectUp2Server(collect_from, collect_result, null);
        }

        // 其他应用发给主服务的数据及回调
        else if(ACTION_MAIN_RECV.equals(intent.getAction())){
            String main_recv_from = intent.getStringExtra(KEY_MAIN_RECV_FROM);
            String main_recv_function = intent.getStringExtra(KEY_MAIN_RECV_FUNCTION);
            LogUtils.e(TAG, "main_recv_from = " + main_recv_from);
            LogUtils.e(TAG, "main_recv_function = " + main_recv_function);
            if(StringUtils.isEmpty(main_recv_from) || StringUtils.isEmpty(main_recv_function))
                return;

            // 关机的回调
            if("shutdown".equals(main_recv_function)){
                int main_recv_result = intent.getIntExtra(KEY_MAIN_RECV_RESULT, 0);
                LogUtils.e(TAG, "shutdown_result = " + main_recv_result);
                // 定时关机【包含查询】
                if(main_recv_result > 0){
                    AppUtils.shutdownUp2Server(ACTION_SHUTDOWN, main_recv_result);
                }
                // 取消定时关机
                else if(-1 == main_recv_result){
                    AppUtils.shutdownUp2Server(ACTION_SHUTDOWN, -1);
                }
            }

        }
    }

}
