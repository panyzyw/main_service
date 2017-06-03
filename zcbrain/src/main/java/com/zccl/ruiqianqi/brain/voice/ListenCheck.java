package com.zccl.ruiqianqi.brain.voice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;
import com.zccl.ruiqianqi.utils.LedUtils;

import org.greenrobot.eventbus.EventBus;

import static com.zccl.ruiqianqi.brain.eventbus.MindBusEvent.TransEvent.TRANS_EXIT;
import static com.zccl.ruiqianqi.brain.voice.BaseHandler.SCENE_MUSIC;
import static com.zccl.ruiqianqi.brain.voice.BaseHandler.SCENE_TRANS;
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
    // 用不用显示悬浮表情
    private boolean isUseVoiceFloat;

    public ListenCheck(Context context, RobotVoice robotVoice){
        this.mContext = context;
        this.mRobotVoice = robotVoice;
        helloWords = mContext.getResources().getStringArray(R.array.hello_words);
    }

    /**
     * 要不要显示大表情
     * @return
     */
    public boolean isShowExpression(){
        StatePresenter sp = StatePresenter.getInstance();
        String scene = sp.getScene();

        // 自产音乐播放器是否在运行
        if(SCENE_MUSIC.equals(scene)){
            return false;
        }

        // android:sharedUserId="android.uid.system"
        String topApp = SystemUtils.getCurrentAppPkgName(mContext);
        // 讯飞音乐播放器是否在运行
        if("com.lz.smart.music".equals(topApp)){
            return false;
        }
        // 自产音乐播放器是否在运行
        else if("com.yongyida.robot.player".equals(topApp)){
            return false;
        }

        return true;
    }

    /**
     * 检查机器人当前状态
     * @param isTouchOrVoice true触摸和唤醒，false循环监听
     */
    private int checkStatus(boolean isTouchOrVoice){
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
            if(isTouchOrVoice) {
                soundTips(mContext.getString(R.string.is_in_control));
            }
            return -1;
        }

        // 机器人帐号异常
        if(STATE_ACCOUNT_EXCEPTION.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.error_account_id));
            soundTips(mContext.getString(R.string.error_account_id));
            return -2;
        }

        // 机器人正在电话中
        if(sp.isCalling()){
            LogUtils.e(TAG, mContext.getString(R.string.is_making_a_call));
            soundTips(mContext.getString(R.string.is_making_a_call));
            return -8;
        }

        // 机器人正在工厂模式中
        if(sp.isFactory()){
            LogUtils.e(TAG, mContext.getString(R.string.is_in_factory_mode));
            soundTips(mContext.getString(R.string.is_in_factory_mode));
            return -9;
        }

        // 机器人正在视频中
        if(sp.isVideoing()){
            LogUtils.e(TAG, mContext.getString(R.string.is_in_video));
            soundTips(mContext.getString(R.string.is_in_video));
            return -10;
        }


        // 网络连接有问题
        if(!sp.isNetConnected()){
            LogUtils.e(TAG, mContext.getString(R.string.have_no_net));
            //soundTips(mContext.getString(R.string.have_no_net));
            return -3;
        }

        // 连接服务器失败
        if(STATE_CONNECT_EXCEPTION.equals(sp.getRobotState())){
            // 这儿不用重连，连接失败会一直尝试连接
            LogUtils.e(TAG, mContext.getString(R.string.connect_server_failure));
            soundTips(mContext.getString(R.string.connect_server_failure));
            return -4;
        }
        // 机器人登录失败
        if(STATE_LOGIN_FAILURE.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.login_server_failure));
            soundTips(mContext.getString(R.string.login_server_failure));
            return -5;
        }
        // 连接成功，正在登录
        if(STATE_LOGIN_ING.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.login_is_going));
            soundTips(mContext.getString(R.string.login_is_going));
            return -6;
        }
        // 机器人登录不成功
        if(!STATE_LOGIN_SUCCESS.equals(sp.getRobotState())){
            LogUtils.e(TAG, mContext.getString(R.string.login_server_no_success) + " : " + sp.getRobotState());
            soundTips(mContext.getString(R.string.login_server_no_success));
            return -7;
        }

        return 0;
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
     * @param isTouchOrVoice true触摸和唤醒，false循环监听
     * @param welcome         true说唤醒语，false不说唤醒语
     * @param isUseExpression     是不是显示大表情
     */
    protected void startCheckAndListen(boolean isTouchOrVoice, boolean welcome, boolean isUseExpression){

        final int isGo = checkStatus(isTouchOrVoice);
        if(0 == isGo || -3 == isGo) {
            if(welcome) {
                String word = helloWords[CheckUtils.getRandom(helloWords.length)];
                mRobotVoice.startTTS(word, new Runnable() {
                    @Override
                    public void run() {
                        if(0 == isGo) {
                            mRobotVoice.startUnderstand();
                        }
                        // 网络断开了，开启离线
                        else {
                            mRobotVoice.firstAsr();
                        }
                        startListenFace();
                    }
                });
            }else {
                if(0 == isGo) {
                    mRobotVoice.startUnderstand();
                }
                // 网络断开了，开启离线
                else {
                    mRobotVoice.firstAsr();
                }
                startListenFace();
            }

            LogUtils.e(TAG, "isUseExpression = " + isUseExpression);
            // 触摸，露出笑脸
            if(isUseExpression){

                // 开启大表情
                /*
                Intent intent = new Intent(mContext, ExpressionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
                */

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.yongyida.robot.lockscreen",
                        "com.yongyida.robot.lockscreen.presentation.view.ExpressionActivity");
                // 待机表情，眨眼
                intent.putExtra("action", "init");
                intent.setComponent(componentName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        }else {
            LogUtils.e(TAG, mContext.getString(R.string.no_match_listen));
            mRobotVoice.prettyEnd();
        }
    }

    /**********************************【读写成员变量】********************************************/
    /**
     * 获取要不要显示悬浮表情
     * @return
     */
    public boolean isUseVoiceFloat() {
        return isUseVoiceFloat;
    }

    /**
     * 设置要不要显示悬浮表情
     * @param useVoiceFloat
     */
    public void setUseVoiceFloat(boolean useVoiceFloat) {
        isUseVoiceFloat = useVoiceFloat;
    }

    /**********************************【表情相关方法】********************************************/
    /**
     * 开启监听表情
     */
    protected void startListenFace(){

        // 监听时候的呼吸灯
        LedUtils.startMonitorLed(mContext);

        if(!isUseVoiceFloat)
            return;

        MindBusEvent.VoiceFloatEvent voiceFloatEvent = new MindBusEvent.VoiceFloatEvent();
        voiceFloatEvent.setType(MindBusEvent.VoiceFloatEvent.START);
        EventBus.getDefault().post(voiceFloatEvent);
    }

    /**
     * 广播音量【0~30】
     * @param volume
     */
    protected void sendVolume(int volume){
        if(!isUseVoiceFloat)
            return;

        MindBusEvent.VoiceFloatEvent voiceFloatEvent = new MindBusEvent.VoiceFloatEvent();
        voiceFloatEvent.setType(MindBusEvent.VoiceFloatEvent.GOING);
        voiceFloatEvent.setVolume(volume);
        EventBus.getDefault().post(voiceFloatEvent);
    }

    /**
     * 结束监听表情
     */
    protected void endListenFace(){

        // 结束监听
        LedUtils.endMonitorLed(mContext);

        if(!isUseVoiceFloat)
            return;

        MindBusEvent.VoiceFloatEvent voiceFloatEvent = new MindBusEvent.VoiceFloatEvent();
        voiceFloatEvent.setType(MindBusEvent.VoiceFloatEvent.END);
        EventBus.getDefault().post(voiceFloatEvent);
    }

}