package com.zccl.ruiqianqi.presentation.presenter;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.domain.tasks.localtask.BaseLocalTask;
import com.zccl.ruiqianqi.domain.tasks.localtask.LocalAddFriendTask;
import com.zccl.ruiqianqi.domain.tasks.localtask.LocalDelFriendTask;
import com.zccl.ruiqianqi.domain.tasks.localtask.LocalForwardTask;
import com.zccl.ruiqianqi.domain.tasks.localtask.LocalLoginTask;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.socket.localsocket.LocalServer;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.zccl.ruiqianqi.config.LocalProtocol.ADD_FRIEND_GET;
import static com.zccl.ruiqianqi.config.LocalProtocol.LOGIN_GET;
import static com.zccl.ruiqianqi.config.LocalProtocol.REMOVE_FRIEND_GET;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_CANCEL;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_CANCEL_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_INVITE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_INVITE_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_REPLY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_AGORA_VIDEO_REPLY_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_MEETING_REPORT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_MEETING_REPORT_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_TUTK_GET_ID;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_CANCEL;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_CANCEL_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_INVITE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_INVITE_ACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_REPLY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_WEB_RTC_VIDEO_REPLY_ACK;

/**
 * Created by ruiqianqi on 2017/3/16 0016.
 */

public class LocalPresenter extends BasePresenter implements LocalServer.LocalSocketCallback {

    // 本地服务器
    private LocalServer mLocalServer;
    // 本地任务集合
    private Map<String, Class<? extends BaseLocalTask>> mLocalTaskMap;

    public LocalPresenter(){

        mLocalTaskMap = new HashMap<>();
        mLocalTaskMap.put(LOGIN_GET, LocalLoginTask.class);
        mLocalTaskMap.put(ADD_FRIEND_GET, LocalAddFriendTask.class);
        mLocalTaskMap.put(REMOVE_FRIEND_GET, LocalDelFriendTask.class);

        //【声网】
        mLocalTaskMap.put(B_AGORA_VIDEO_INVITE, LocalForwardTask.class);
        mLocalTaskMap.put(B_AGORA_VIDEO_INVITE_ACK, LocalForwardTask.class);
        mLocalTaskMap.put(B_AGORA_VIDEO_REPLY, LocalForwardTask.class);
        mLocalTaskMap.put(B_AGORA_VIDEO_REPLY_ACK, LocalForwardTask.class);
        mLocalTaskMap.put(B_AGORA_VIDEO_CANCEL, LocalForwardTask.class);
        mLocalTaskMap.put(B_AGORA_VIDEO_CANCEL_ACK, LocalForwardTask.class);

        //【WebRTC】
        mLocalTaskMap.put(B_WEB_RTC_VIDEO_INVITE, LocalForwardTask.class);
        mLocalTaskMap.put(B_WEB_RTC_VIDEO_INVITE_ACK, LocalForwardTask.class);
        mLocalTaskMap.put(B_WEB_RTC_VIDEO_REPLY, LocalForwardTask.class);
        mLocalTaskMap.put(B_WEB_RTC_VIDEO_REPLY_ACK, LocalForwardTask.class);
        mLocalTaskMap.put(B_WEB_RTC_VIDEO_CANCEL, LocalForwardTask.class);
        mLocalTaskMap.put(B_WEB_RTC_VIDEO_CANCEL_ACK, LocalForwardTask.class);

        //【视频会议时间流量上报】
        mLocalTaskMap.put(B_MEETING_REPORT, LocalForwardTask.class);
        mLocalTaskMap.put(B_MEETING_REPORT_ACK, LocalForwardTask.class);

        //【TUTK】
        mLocalTaskMap.put(B_TUTK_GET_ID, LocalForwardTask.class);

        // 开启本地服务器
        mLocalServer = new LocalServer();
        mLocalServer.setVideoClientCallback(this);
;
    }

    /**************************************【对外提供的方法】**************************************/
    /**
     * 初始化
     */
    public void initSome(){
        // 注册事件总线
        EventBus.getDefault().register(this);
        // 开启本地服务器监听
        MyRxUtils.doNewThreadRun(mLocalServer);
    }

    /**
     * 释放资源
     */
    public void release(){
        // 注销事件总线
        EventBus.getDefault().unregister(this);
        // 关闭本地服务器监听
        mLocalServer.close();
    }

    /**********************************【事件总线的处理】******************************************/
    /**
     * 对接收到的数据进行本地服务器的转发
     * @param forwardLocalEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 10)
    public void OnForwardLocalEvent(MindBusEvent.ForwardLocalEvent forwardLocalEvent){
        String data = forwardLocalEvent.getText();
        if(StringUtils.isEmpty(data))
            return;
        mLocalServer.sendData(LocalServer.VIDEO_CLIENT, data);
    }

    /********************************【本地客户端的数据回调】**************************************/
    /**
     * 来自本地客户端的数据
     * @param json
     * @param data
     */
    @Override
    public void OnResult(final String json, final byte[] data) {
        scheduleTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObj = new JSONObject(json);
                    String cmd = jsonObj.optString("cmd", null);
                    if(!StringUtils.isEmpty(cmd)){
                        Class<? extends BaseLocalTask> taskClass = mLocalTaskMap.get(cmd);
                        if(null != taskClass){
                            BaseLocalTask baseLocalTask = taskClass.newInstance();
                            baseLocalTask.setResult(json);
                            baseLocalTask.run();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
