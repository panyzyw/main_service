package com.yongyida.robot.voice.frame.iflytek;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.alsa.AlsaRecorder;
import com.iflytek.alsa.AlsaRecorder.PcmListener;
import com.iflytek.cae.CAEEngine;
import com.iflytek.cae.CAEError;
import com.iflytek.cae.CAEListener;
import com.iflytek.cae.jni.CAEJni;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.roboot.hdmictl.HdmiCtl;
import com.yongyida.robot.aidl.PlayerClient;
import com.yongyida.robot.voice.bean.WakeUpInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.frame.iflytek.VoiceWakeUp;
import com.yongyida.robot.voice.mic.SavePcmAudio;
import com.yongyida.robot.voice.robot.VoiceLocalization;
import com.yongyida.robot.voice.utils.JsonParserUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.voice.translate.TranslationAidlService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * 语音唤醒.
 *
 * @author Administrator
 */
public class VoiceWakeUp extends CommVoiceParse {
    private CAEEngine mCaeEngine;
    private AlsaRecorder mRecorder;
    private String mResPath = null;
    private VoiceUnderstand mVoiceUnderstand = null;
    private static VoiceWakeUp wake;
    private boolean flagReset=false;           //引擎销毁重置标志
    private VoiceLocalization voiceLocalization;
    private SavePcmAudio mSavePcmAudio;
    private boolean singleMic=false;
    private int thresholdValue = 15;
    private SharePreferenceUtils mSp;
    public static boolean isTranlation = false;
    PcmListener mPcmListener = new PcmListener() {
        byte[] sendData;
        @Override
        public void onPcmData(byte[] data, int dataLen) {
            // 建议不要在读音频线程中做耗时的同步操作，否则会导致音频数据读出不及时造成AudioRecord中的缓存溢出。
            // 所以以下的两个writeAudio都是采用的异步
            if (null != mCaeEngine) {
                // 将从阵列读取的96K采样的音频写入CAE引擎
                mCaeEngine.writeAudio(data, dataLen);
                mSavePcmAudio.writeAudio(data,dataLen);
                if(singleMic){                                   //触摸唤醒走单mic模式，此模式无降噪，只能近距离识别
                    if(mVoiceUnderstand.isListening()){
                        sendData=new byte[dataLen/24];
                        dataLen=mCaeEngine.extract16K(data,dataLen,1,sendData);
                        mVoiceUnderstand.writeData(sendData, dataLen);
                    }
                }
                if (mCaeEngine.isWakeup() == false) {
                    mCaeEngine.setRealBeam(0);
                }
            }
        }
    };

    CAEListener mCaeListener = new CAEListener() {
        @Override
        public void onWakeup(String jsonResult) {
            WakeUpInfo wakeUp = JsonParserUtils.parseResult(jsonResult, WakeUpInfo.class);
            if (wakeUp == null) {
                Log.d("jlog","wakeUp null");
                return;
            }
            Log.d("jlog",
                    "angle：" + wakeUp.getAngle() + " " +
                            "pwoer:" + wakeUp.getPower() / 1000000000 + " " +
                            "score:" + wakeUp.getScore() + " " +
                            "beam:" + wakeUp.getBeam() + " " +
                            "thresholdValue:" + thresholdValue
            );
			//ShowToast.getInstance(context).show("S"+wakeUp.getScore(), " A"+wakeUp.getAngle() + " N" + wakeUp.getBeam());
             if (wakeUp.getScore() > thresholdValue) {
				singleMic=false;
                //唤醒亮屏
                PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
                wl.acquire();
                wl.release();

                Map<String, String> map = new HashMap<String, String>();
                map.put(GeneralData.ACTION, IntentData.INTENT_TOUCH_SENSOR);
                map.put("android.intent.extra.Touch", "t_head");
                SendBroadcastUtils.sendBroad(context, map);
                if(voiceLocalization.startRotate(wakeUp.getAngle())){    //声源定位
                    mCaeEngine.setRealBeam(0);
                }
            }
        }

        @Override
        public void onError(CAEError error) {
            // TODO Auto-generated method stub
            Log.e("jlog", error.toString()+" "+error.getErrorCode());
            if(error.getErrorCode()==10110){
                if(!flagReset){
                    flagReset=true;
                    if(HdmiCtl.Reset5Mic()==1){
                        Log.d("jlog","10110错误，引擎重置");
                        mCaeEngine.reset();
                        mCaeEngine.destroy();
                        mCaeEngine=null;
                        mCaeEngine = CAEEngine.createInstance(mResPath);
                        mCaeEngine.setCAEListener(mCaeListener);
                    }else{
                        Log.d("jlog","5mic驱动重置失败");
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flagReset=false;  //3秒后重置
                    }
                },3000);
            }
        }

        String str = null;
        Map<String, String> map = new HashMap<>();

        @Override
        public void onAudio(byte[] audio, int audioLen, int param1, int param2) {
            if (mVoiceUnderstand == null) {
                mVoiceUnderstand = VoiceUnderstand.getInstance(context);
                Log.d("jlog", "mVoiceUnderstand NULL");
            } else if(!singleMic) {
                mVoiceUnderstand.writeData(audio, audioLen);
            }
            try {
                str = new String(audio, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            // 处于唤醒态的话，就不写数据了
            if(mVoiceUnderstand != null && !mVoiceUnderstand.isListening()){
                PlayerClient.getInstance(context).sendAudioData(str);
            }
            
            if(VoiceWakeUp.isTranlation){
               
                try {
                    
                    if(TranslationAidlService.getInstance(context).mstub!=null){
                    	TranslationAidlService.getInstance(context).mstub.sendVoiceData(str,"" + audioLen);                    	
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
/*            map = new HashMap<>();
            map.put(GeneralData.ACTION, "com.yydrobot.AUDIO");
            map.put("data", str);
            map.put("len", String.valueOf(audioLen));
            SendBroadcastUtils.sendBroad(context, map);*/
			}
        }
    };

    private VoiceWakeUp(Context context) {
        this.context = context;
        mResPath = ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.assets, "ivw/" + "xiaoyong.jet");
        mRecorder = AlsaRecorder.createInstance(0);
        voiceLocalization=VoiceLocalization.getInstance();   //初始化声源定位
		mSp=SharePreferenceUtils.getInstance(context);
        mSavePcmAudio=new SavePcmAudio();
		Log.d("jlog", "VoiceWakeUp start");
    }
    @Override
    public void start() {
        if (context == null) {
            return;
        }

        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                if (null != mCaeEngine) {
                    mCaeEngine.destroy();
                    mCaeEngine = null;
                }
                if (null == mCaeEngine) {
                    mCaeEngine = CAEEngine.createInstance(mResPath);
                    mCaeEngine.setCAEListener(mCaeListener);
                    mRecorder.startRecording(mPcmListener);
                    CAEJni.DebugLog(false);
                    //AlsaJni.showJniLog(false);
                }
            }
        }, 1000);
        mVoiceUnderstand = VoiceUnderstand.getInstance(context);
    }

    @Override
    public void stop() {
        System.gc();
    }
    public static VoiceWakeUp getInstance(Context context) {
        if (wake == null) {
            synchronized (VoiceWakeUp.class) {
                if (wake == null) {
                    wake = new VoiceWakeUp(context);
                }
            }
        }
        return wake;
    }
    public void setSingleMic(boolean singleMic) {
        this.singleMic=singleMic;      //触摸唤醒则为单mic模式
    }

    public void setThresholdValue(int thresholdValue){
        mSp.putInt("thresholdValue",thresholdValue);
        this.thresholdValue = thresholdValue;
    }
    public int getThresholdValue(){
        return mSp.getInt("thresholdValue",15);
    }
}
