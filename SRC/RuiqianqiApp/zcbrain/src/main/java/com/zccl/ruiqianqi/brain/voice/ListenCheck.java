package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;

import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_EXIT;
import static com.zccl.ruiqianqi.brain.voice.BaseHandler.SCENE_TRANS;
import static com.zccl.ruiqianqi.config.MyConfig.INTENT_ACTION_LISTEN;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_ACCOUNT_EXCEPTION;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_CONNECT_EXCEPTION;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_FAILURE;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_ING;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_SUCCESS;

/**
 * Created by ruiqianqi on 2017/3/27 0027.
 */

public class ListenCheck {

    // 类标志
    private String TAG = ListenCheck.class.getSimpleName();
    // 全局上下文
    private Context mContext;
    // 音频处理类
    private RobotVoice mRobotVoice;
    // 唤醒问候语
    private String[] helloWords;
    // 用不用表情
    private boolean isUseExpression;

    public ListenCheck(Context context, RobotVoice robotVoice){
        this.mContext = context;
        this.mRobotVoice = robotVoice;
        helloWords = mContext.getResources().getStringArray(R.array.hello_words);
    }

    /**
     * 检查机器人当前状态
     * @param isTouchVoice true触摸和唤醒，false循环监听
     */
    private boolean checkStatus(boolean isTouchVoice){
        StatePresenter sp = StatePresenter.getInstance();

        // 翻译中【语音唤醒退出】
        if(SCENE_TRANS.equals(sp.getScene())){

            // 退出翻译
            MindBusEvent.TransEvent transEvent = new MindBusEvent.TransEvent();
            transEvent.setType(TRANS_EXIT);
            EventBus.getDefault().post(transEvent);

            // 切换回中文
            mRobotVoice.switchLanguage("zh");
        }

        // 机器人在控制中
        if(sp.isInControl()){
            LogUtils.e(TAG, mContext.getString(R.string.is_in_control));
            // 被控制时，只有触摸和语音唤醒才播放
            // 循环监听不播放
            if(isTouchVoice) {
                soundTips(mContext.getString(R.string.is_in_control));
            }
            return false;
        }

        // 机器人帐号异常
        if(STATE_ACCOUNT_EXCEPTION.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.error_account_id));
            soundTips(mContext.getString(R.string.error_account_id));
            return false;
        }

        // 网络连接有问题
        else if(!sp.isNetConnected()){
            LogUtils.e(TAG, mContext.getString(R.string.have_no_net));
            soundTips(mContext.getString(R.string.have_no_net));
            return false;
        }

        // 连接服务器失败
        else if(STATE_CONNECT_EXCEPTION.equals(sp.getRobotState())){
            // 这儿不用重连，连接失败会一直尝试连接
            LogUtils.e(TAG, mContext.getString(R.string.connect_server_failure));
            soundTips(mContext.getString(R.string.connect_server_failure));
            return false;
        }
        // 机器人登录失败
        else if(STATE_LOGIN_FAILURE.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.login_server_failure));
            soundTips(mContext.getString(R.string.login_server_failure));
            return false;
        }
        // 连接成功，正在登录
        else if(STATE_LOGIN_ING.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.login_is_going));
            soundTips(mContext.getString(R.string.login_is_going));
            return false;
        }
        // 机器人登录不成功
        else if(!STATE_LOGIN_SUCCESS.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.login_server_no_success) + " : " + sp.getRobotState());
            soundTips(mContext.getString(R.string.login_server_no_success));
            return false;
        }

        // 机器人正在电话中
        if(sp.isCalling()){
            LogUtils.e(TAG, mContext.getString(R.string.is_making_a_call));
            soundTips(mContext.getString(R.string.is_making_a_call));
            return false;
        }

        // 机器人正在工厂模式中
        if(sp.isFactory()){
            LogUtils.e(TAG, mContext.getString(R.string.is_in_factory_mode));
            soundTips(mContext.getString(R.string.is_in_factory_mode));
            return false;
        }

        // 机器人正在视频中
        if(sp.isVideoing()){
            LogUtils.e(TAG, mContext.getString(R.string.is_in_video));
            soundTips(mContext.getString(R.string.is_in_video));
            return false;
        }

        return true;
    }

    /**
     * 语音提示
     * @param words
     */
    private void soundTips(String words){
        if(!mRobotVoice.isSpeaking()){
            ReportPresenter.report(words);
        }
    }

    /**
     * 需要播放问候语后再开启监听
     * @param isTouchVoice true触摸和唤醒，false循环监听
     * @param welcome true说唤醒语，false不说唤醒语
     */
    protected void startCheckAndListen(boolean isTouchVoice, boolean welcome){

        boolean isGo = checkStatus(isTouchVoice);
        if(isGo) {
            if(welcome) {
                String word = helloWords[CheckUtils.getRandom(helloWords.length)];
                mRobotVoice.stopTTS();
                mRobotVoice.startTTS(word, "", new AbstractVoice.SynthesizerCallback() {
                    @Override
                    public void OnBegin() {

                    }

                    @Override
                    public void OnPause() {

                    }

                    @Override
                    public void OnResume() {

                    }

                    @Override
                    public void OnComplete(Throwable throwable, String tag) {
                        mRobotVoice.startUnderstand();
                        startListenFace();
                    }
                });
            }else {
                mRobotVoice.startUnderstand();
                startListenFace();
            }
        }else {
            LogUtils.e(TAG, mContext.getString(R.string.no_match_listen));
            mRobotVoice.prettyEnd();
        }
    }

    /**********************************【读写成员变量】********************************************/
    /**
     * 要不要显示表情
     * @param useExpression
     */
    public void setUseExpression(boolean useExpression) {
        isUseExpression = useExpression;
    }

    /**********************************【表情相关方法】********************************************/
    /**
     * 开启监听表情
     */
    protected void startListenFace(){
        if(!isUseExpression)
            return;
        Bundle args = new Bundle();
        //args.putString(KEY_LISTEN_STATUS, VALUE_LISTEN_START);
        args.putString("result", "enter");
        MyAppUtils.sendBroadcast(mContext, INTENT_ACTION_LISTEN, args);

        sendLedBroad(true);
    }

    /**
     * 广播音量
     * @param volume
     */
    protected void sendVolume(int volume){
        if(!isUseExpression)
            return;
        Bundle args = new Bundle();
        //args.putString(KEY_LISTEN_STATUS, VALUE_LISTEN_START);
        args.putString("result", volume + "");
        MyAppUtils.sendBroadcast(mContext, INTENT_ACTION_LISTEN, args);
    }

    /**
     * 结束监听表情
     */
    protected void endListenFace(){
        if(!isUseExpression)
            return;
        Bundle args = new Bundle();
        //args.putString(KEY_LISTEN_STATUS, VALUE_LISTEN_END);
        args.putString("result", "out");
        MyAppUtils.sendBroadcast(mContext, INTENT_ACTION_LISTEN, args);

        sendLedBroad(false);
    }

    /**
     * 开始与关闭，监听时候的呼吸灯状态
     * 监听是绿灯
     * @param onOff
     */
    private void sendLedBroad(boolean onOff) {
        Intent intentMonitorOpen = new Intent("com.yongyida.robot.change.BREATH_LED");
        intentMonitorOpen.putExtra("on_off", onOff);
        intentMonitorOpen.putExtra("place", 3);
        // BEAN_LED_DEFAULT = 0;
        // BEAN_LED_RED = 1;
        // BEAN_LED_GREEN = 2;
        // BEAN_LED_BLUE = 3;
        intentMonitorOpen.putExtra("colour", 2);
        intentMonitorOpen.putExtra("frequency", 3);
        intentMonitorOpen.putExtra("Permanent", "monitor");
        intentMonitorOpen.putExtra("priority", 6);
        mContext.sendBroadcast(intentMonitorOpen);
    }
}
