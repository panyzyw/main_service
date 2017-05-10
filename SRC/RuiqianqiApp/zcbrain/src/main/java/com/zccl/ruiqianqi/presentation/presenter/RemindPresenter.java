package com.zccl.ruiqianqi.presentation.presenter;

import android.content.Intent;

import com.google.gson.Gson;
import com.yongyida.robot.entity.Notice;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.domain.model.pushback.RemindBack;
import com.zccl.ruiqianqi.domain.model.pushdown.PushRemind;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.zccl.ruiqianqi.config.RemoteProtocol.B_REMIND_RESULT;

/**
 * Created by ruiqianqi on 2017/3/22 0022.
 */

public class RemindPresenter extends BasePresenter {

    // 类标志
    private static String TAG = RemindPresenter.class.getSimpleName();

    // 增
    public static final String REMIND_ADD = "android.intent.action.NOTIFICATION_ADD";
    // 删
    public static final String REMIND_DEL = "android.intent.action.NOTIFICATION_DEL";
    // 改
    public static final String REMIND_UPDATE = "android.intent.action.NOTIFICATION_UPDATE";
    // 查
    public static final String REMIND_QUERY = "android.intent.action.NOTIFICATION_QUERY";

    // 等待查询提醒的结果，唯一，静态
    private static ScheduledFuture<?> queryRemind;

    /**
     * 发送给提醒应用的广播
     * @param action
     * @param commandRemind
     */
    private void sendRemindBroadcast(String action, PushRemind.CommandRemind commandRemind) {
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra("id", commandRemind.getId());
            intent.putExtra("time", commandRemind.getTime());
            intent.putExtra("title", commandRemind.getTitle());
            intent.putExtra("content", commandRemind.getContent());
            intent.putExtra("seq", commandRemind.getSeq());
            mContext.sendBroadcast(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过手机增加提醒
     * @param commandRemind
     */
    public void addRemind(PushRemind.CommandRemind commandRemind){
        sendRemindBroadcast(REMIND_ADD, commandRemind);
    }

    /**
     * 通过手机删除提醒
     * @param commandRemind
     */
    public void delRemind(PushRemind.CommandRemind commandRemind){
        sendRemindBroadcast(REMIND_DEL, commandRemind);
    }

    /**
     * 通过手机更改提醒
     * @param commandRemind
     */
    public void updateRemind(PushRemind.CommandRemind commandRemind){
        sendRemindBroadcast(REMIND_UPDATE, commandRemind);
    }

    /**
     * 【这个在NETTY客户端线程调用】
     * 通过手机查询提醒
     * 等待广播{@link com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver#QUERY_RESULT}
     * @param commandRemind
     */
    public void queryRemind(PushRemind.CommandRemind commandRemind){
        StatePresenter sp = StatePresenter.getInstance();
        if(sp.isWaitingRemindResult())
            return;
        // 发送查询广播
        sendRemindBroadcast(REMIND_QUERY, commandRemind);
        // 设置等待结果标志
        sp.setWaitingRemindResult(true);
        // 设置超时任务
        queryRemind = scheduleTask.execute(new Runnable() {
            @Override
            public void run() {
                sendResultToServer(null);
            }
        }, 2000, TimeUnit.MILLISECONDS);
    }

    /**
     * 【这个在广播主线程调用】
     * 来自提醒应用的数据
     * @param noticeArrayList
     */
    public void getRemindResult(ArrayList<Notice> noticeArrayList){
        if(null != queryRemind){
            long remain = queryRemind.getDelay(TimeUnit.MILLISECONDS);
            // 超时任务，没开始、没取消的话
            if(remain > 0 && !queryRemind.isCancelled()){
                queryRemind.cancel(true);
                sendResultToServer(noticeArrayList);
            }
            queryRemind = null;
        }
    }

    /**
     * 发送结果到服务器
     * @param noticeArrayList
     */
    private void sendResultToServer(ArrayList<Notice> noticeArrayList){

        StatePresenter sp = StatePresenter.getInstance();
        // 有结果了，设置状态为非等待状态
        sp.setWaitingRemindResult(false);

        // 转发给【服务器】
        MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
        forwardSocketEvent.setCmd(B_REMIND_RESULT);

        if(null != noticeArrayList){
            Gson gson = new Gson();
            RemindBack.RemindResult remindResult = new RemindBack.RemindResult();
            remindResult.setData(gson.toJson(noticeArrayList));
            forwardSocketEvent.setText(gson.toJson(remindResult));
        }else {
            forwardSocketEvent.setText("get data is null");
        }

        EventBus.getDefault().post(forwardSocketEvent);
    }

}
