package com.zccl.ruiqianqi.brain.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yongyida.robot.entity.Notice;
import com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presentation.presenter.BindUserPresenter;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.RemindPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.util.ArrayList;

public class MainReceiver extends SystemReceiver {

    // 类标志
    private static String TAG = MainReceiver.class.getSimpleName();

    // 通过手机查询提醒，等待结果的广播【接收】
    public static final String QUERY_RESULT = "com.yongyida.robot.notification.QUERY_RESULT";
    // 携带数据的KEY
    public static final String QUERY_RESULT_KEY = "noticeList";

    // 查询绑定用户
    public static final String BINDER_USER_QUERY = "com.yydrobot.qrcode.QUERY";
    // 删除绑定用户
    public static final String BINDER_USER_DELETE = "com.yydrobot.qrcode.DELETE";

    // 机器人当前应用场景
    public static final String ROBOT_SCENE = "com.yongyida.robot.SCENE";
    // 当前场景【String】
    public static final String KEY_SCENE_NAME = "key_scene_current";
    // 当前场景状态【boolean】
    public static final String KEY_SCENE_STATUS = "key_scene_status";

    // 科大讯飞声源定位与唤醒状态
    public static final String ACTION_FLYTEK_VOICE = "com.yongyida.robot.VOICE";

    public MainReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //super.onReceive(context, intent);

        if(null == intent){
            return;
        }
        LogUtils.e(TAG, "action = " + intent.getAction());

        // 系统开机广播
        if(ACTION_BOOT.equals(intent.getAction())){

        }
        // 5秒钟一次launcher广播
        else if (ACTION_LAUNCH.equals(intent.getAction())) {

        }
        // 通过手机查询提醒，等待结果的广播
        else if (QUERY_RESULT.equals(intent.getAction())) {
            StatePresenter sp = StatePresenter.getInstance();

            // 电话中返回
            // 非控制状态返回
            if(sp.isCalling() || !sp.isInControl())
                return;

            RemindPresenter remindPresenter = new RemindPresenter();
            try {
                ArrayList<Notice> list = intent.getParcelableArrayListExtra(QUERY_RESULT_KEY);
                remindPresenter.getRemindResult(list);
            } catch (Throwable e) {
                remindPresenter.getRemindResult(null);
            }
        }

        // 查询绑定用户
        else if (BINDER_USER_QUERY.equals(intent.getAction())) {
            BindUserPresenter bup = new BindUserPresenter();
            bup.queryBindUser();
        }

        // 删除绑定用户
        else if (BINDER_USER_DELETE.equals(intent.getAction())) {
            String id = intent.getStringExtra("id");
            BindUserPresenter bup = new BindUserPresenter();
            bup.deleteBindUser(id);
        }

        // 机器人当前应用场景
        else if(ROBOT_SCENE.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if(null == bundle)
                return;
            String scene = bundle.getString(KEY_SCENE_NAME);
            boolean status = bundle.getBoolean(KEY_SCENE_STATUS);
            StatePresenter.getInstance().handleScene(scene, status);
        }

        // 科大讯飞声源定位与唤醒状态
        else if(ACTION_FLYTEK_VOICE.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if(null == bundle)
                return;
            voiceLocalization(context, bundle);
        }

    }

    /***********************************【私有实现方法】*******************************************/
    /**
     * 声源定位与唤醒阀值相关设置与查询
     * @param bundle
     */
    private void voiceLocalization(Context context, Bundle bundle){
        String data = bundle.getString("data");
        PersistPresenter persist = PersistPresenter.getInstance();
        // 【设置】关闭声源定位
        if("rotate_off".equals(data)){
            persist.setLocalization(false);
            LogUtils.e(TAG, "rotate_off");
        }
        // 【设置】打开声源定位
        else if("rotate_on".equals(data)){
            persist.setLocalization(true);
            LogUtils.e(TAG, "rotate_on");
        }
        // 【查询】声源定位状态
        else if("getStatus".equals(data)){
            sendLocalizationStatus(context);
            LogUtils.e(TAG, "queryLocalization");
        }
        // 【查询】唤醒阀值
        else if("getThresholdValue".equals(data)){
            sendThresholdValue(context);
            LogUtils.e(TAG, "queryThreshold");
        }

        // 设置唤醒阀值
        int thresholdValue = bundle.getInt("thresholdValue", 0);
        if(thresholdValue != 0){
            persist.setThreshold(thresholdValue);
        }

    }

    /**
     * 发送声源定位状态给系统设置
     * @param context
     */
    private void sendLocalizationStatus(Context context){
        PersistPresenter persist = PersistPresenter.getInstance();
        Bundle bundle = new Bundle();
        if(persist.isLocalization()){
            bundle.putString("result", "rotate_on");
        }else {
            bundle.putString("result", "rotate_off");
        }
        MyAppUtils.sendBroadcast(context, ACTION_FLYTEK_VOICE, bundle);
    }

    /**
     * 发送唤醒阀值给系统设置
     * @param context
     */
    private void sendThresholdValue(Context context){
        PersistPresenter persist = PersistPresenter.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString("result", persist.getThreshold() + "");
        MyAppUtils.sendBroadcast(context, ACTION_FLYTEK_VOICE, bundle);
    }

}
