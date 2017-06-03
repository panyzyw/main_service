package com.zccl.ruiqianqi.domain.tasks.remotetask;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_CANCEL;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_CANCEL_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_INVITE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_INVITE_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_REPLY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_REPLY_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_MEETING_REPORT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_MEETING_REPORT_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_CANCEL;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_CANCEL_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_INVITE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_INVITE_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_REPLY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_REPLY_ACK;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 *
 * 这个指令由服务器下发给主服务，再由主服务转发给【视频APK】
 */

public class ForwardTask extends BaseTask {

    private static String TAG = ForwardTask.class.getSimpleName();

    @Override
    public void run() {
        try {
            JSONObject jsonObj = new JSONObject(result);
            String command = jsonObj.optString("command", null);

            if(!StringUtils.isEmpty(command)){
                jsonObj = new JSONObject(command);
                String cmd = jsonObj.optString("cmd", null);

                if(B_AGORA_VIDEO_INVITE.equals(cmd)){
                }else if(B_AGORA_VIDEO_INVITE_ACK.equals(cmd)){
                }else if(B_AGORA_VIDEO_REPLY.equals(cmd)){
                }else if(B_AGORA_VIDEO_REPLY_ACK.equals(cmd)){
                }else if(B_AGORA_VIDEO_CANCEL.equals(cmd)){
                }else if(B_AGORA_VIDEO_CANCEL_ACK.equals(cmd)){
                }else if(B_WEB_RTC_VIDEO_INVITE.equals(cmd)){
                }else if(B_WEB_RTC_VIDEO_INVITE_ACK.equals(cmd)){
                }else if(B_WEB_RTC_VIDEO_REPLY.equals(cmd)){
                }else if(B_WEB_RTC_VIDEO_REPLY_ACK.equals(cmd)){
                }else if(B_WEB_RTC_VIDEO_CANCEL.equals(cmd)){
                }else if(B_WEB_RTC_VIDEO_CANCEL_ACK.equals(cmd)){
                }else if(B_MEETING_REPORT.equals(cmd)){
                }else if(B_MEETING_REPORT_ACK.equals(cmd)){
                }

            }

            // 转发给【视频APK】
            MindBusEvent.ForwardLocalEvent forwardLocalEvent = new MindBusEvent.ForwardLocalEvent();
            forwardLocalEvent.setText(command);
            EventBus.getDefault().post(forwardLocalEvent);

        } catch (JSONException e) {
            LogUtils.e(TAG, "", e);
        }
    }
}
