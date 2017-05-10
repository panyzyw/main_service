package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.roboot.hdmictl.HdmiCtl;
import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.frame.iflytek.VoiceWakeUp;
import com.yongyida.robot.voice.frame.newflytek.MyAppUtils;
import com.yongyida.robot.voice.utils.FileUtil;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

/**
 * 接收传感器消息.
 *
 * @author Administrator
 */
public class TouchSensor extends BaseMessage {

    private static final String TAG = "TouchSensorTAG";
    private SharedPreferences preferences;

    private static boolean head = false;

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            head = false;
        }
    };

    @Override
    public void execute() {
        if (!head) {
            head = true;
            if (context == null) return;

            //用于得到翻译开关是否开启
            preferences = context.getSharedPreferences("BooleanValue"
                    , Context.MODE_PRIVATE);

			/*停止子模块*/
            if (VoiceUnderstand.getInstance(context).stopSunFunc != null) {
                VoiceUnderstand.getInstance(context).stopSunFunc.stop();
                VoiceUnderstand.getInstance(context).stopSunFunc = null;
            }

            executeTouch();
        }

        executor.schedule(runnable, 1, TimeUnit.SECONDS);

    }

    public void executeTouch() {
        try {
            String touch = intent.getExtras().getString("android.intent.extra.Touch");
			String wake =intent.getExtras().getString("android.intent.extra.Wakeup");
            LogUtils.showLogInfo(GeneralData.SUCCESS, touch);
            Log.d("jlog", "touch:" + touch);
            if (touch == null) {
                return;
            }
            touch = touch.trim();


            if (touch.equals("t_pir")) {

                Log.d("jlog", "t_pir");
                return;
            }

            if (mainServiceInfo.getFactory()) {

                Log.d("jlog", "工厂模式");
                return;
            }
            if (mainServiceInfo.getCall()) {

                Log.d("jlog", "Call");
                return;
            }
			if (mainServiceInfo.getVideo()) {
                Log.d("jlog", "video视频会议中");
                return;
            }

            if (touch.equals("yyd4") || touch.equals("yyd3")) {

                stopUnderstandAndMedia();
                Log.d("jlog", touch);
            } else if (touch.equals("yyd2")) {

                Map<String, String> map;
                map = new HashMap<String, String>();
                map.put(GeneralData.ACTION, "com.yydrobot.SHOULDER");
                SendBroadcastUtils.sendBroad(context, map);
                Log.w("jlog", "cn_en");
            }

            if (touch.equals("t_head") || touch.equals("yyd3") || touch.equals("yyd4")) {

                Log.d("jlog", "发STOP广播");

                Map<String, String> map2;
                map2 = new HashMap<String, String>();
                map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
                map2.put(GeneralData.RESULT, GeneralData.TOUCHHEAD);
                map2.put(GeneralData.FROM, touch);
                SendBroadcastUtils.sendBroad(context, map2);
                //Log.d("jlog", "heac");

                //光机开启时，摸肚子关闭光机
                if (touch.equals("t_head")) {
                    if (HdmiCtl.HdmiDppPowerStatus()==1) {
                    HdmiCtl.HdmiDppPowerOFF();
					Map<String, String> hdmi = new HashMap<String, String>();
                    hdmi.put(GeneralData.ACTION, "com.yongyida.robot.ACTION_HDMI_CLOSE");
                    SendBroadcastUtils.sendBroad(context, hdmi);
					}
                }
				//触摸唤醒，设置拾音方向为正前方(0mic)
				if(wake!=null){
					if(wake.equals("t_head_wake")){
						VoiceWakeUp wakeUp= VoiceWakeUp.getInstance(context);
						//wakeUp.setSingleMic(true);
					}
				}
                

            }

            if (robot.getOnline().equals(RobotStateData.STATE_ACCOUNT)) {
                //accout_ecxcpton.wav
                if (!mPlayer.isPlaying()) {
                    mPlayer.playMusic(context, VoiceData.ACCOUNT_EXCEPTION);
                }
                return;
            }

            // 离线状态
            if (!MyAppUtils.isNetConnected(context)) {
                if (robot.getContrallState().equals(RobotStateData.STATE_UNCONTRALL)) {
                    Class<? extends BaseCmd> msg = messageMap.get(touch);
                    if (msg != null) {
                        BaseCmd cmd = msg.newInstance();
                        cmd.execute();
                    }
                    return;
                }
                return;
            }

            if (robot.getOnline().equals(RobotStateData.STATE_LOGIN_DEFAULT)) {

                if (mainServiceInfo.getNetConnect().equals(RobotStateData.STATE_NET_UNCONNECT)) {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.clearOnCompleteListener();
                        mPlayer.playMusic(context, VoiceData.ERROR_NETWORK_1);
                    }
                    return;
                } else if (mainServiceInfo.getNetConnect().equals(RobotStateData.STATE_NET_CONNECT)) {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.playMusic(context, VoiceData.NETWORK_EXCEPTION);
                    }
                    return;
                }

            }

            if (robot.getOnline().equals(RobotStateData.STATE_LOGIN_FAIL)) {

                if (!mPlayer.isPlaying()) {
                    mPlayer.playMusic(context, "login_error.wav");
                }
                return;
            }

            if (robot.getContrallState().equals(RobotStateData.STATE_UNCONTRALL)) {

                Log.d("jlog", "执行任务");

                Class<? extends BaseCmd> msg = messageMap.get(touch);
                FileUtil.putToFile(context, touch, "head.txt");
                if (msg != null) {
                    BaseCmd cmd = msg.newInstance();
                    cmd.execute();

                }
            } else {

                if (!mPlayer.isPlaying()) {
                    mPlayer.playMusic(context, VoiceData.CONTROLL);
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

	private void stopUnderstandAndMedia() {
        if (factory != null) {
            factory.setFactory(VoiceUnderstand.getInstance(context));
            factory.parseStop();
        }
        if (mPlayer.isPlaying()) {
            mPlayer.stopMusic();
        }
		Map<String, String> voiceUnderstandStatus;
        voiceUnderstandStatus = new HashMap<String, String>();
        voiceUnderstandStatus.put(GeneralData.ACTION, IntentData.INTENT_VOICE_UNDERSTAND_STATUS);
        voiceUnderstandStatus.put(GeneralData.STATUS, "end");
        SendBroadcastUtils.sendBroad(context, voiceUnderstandStatus);
    }
}
