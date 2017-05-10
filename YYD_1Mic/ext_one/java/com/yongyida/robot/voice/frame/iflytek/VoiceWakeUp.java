package com.yongyida.robot.voice.frame.iflytek;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;
import com.yongyida.robot.voice.bean.WakeUpInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.utils.JsonParserUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.robot.voice.utils.ShowToast;

import java.util.HashMap;
import java.util.Map;


/**
 * 语音唤醒.
 */
public class VoiceWakeUp extends CommVoiceParse {

	// 类标志
	private static String TAG = VoiceWakeUp.class.getSimpleName();
	// 播放逻辑单例
	private static VoiceWakeUp instance;
	// 唤醒对象
	private VoiceWakeuper mIvw = null;
	/** 唤醒资源路径 */
	private String mResPath = null;
	/** 优化唤醒资源下载路径 */
	private String mJetDownLoadPath = null;
	private SharePreferenceUtils mSp;
	private int thresholdValue = 10;
	private static boolean isRun = false;

	private VoiceWakeUp(Context context) {
		this.context = context;
		ShowToast.getInstance(context);
		mIvw = VoiceWakeuper.createWakeuper(context, new InitListener() {
			@Override
			public void onInit(int i) {

			}
		});
		// 唤醒文件路径
		mResPath = ResourceUtil.generateResourcePath(context, RESOURCE_TYPE.assets, "ivw/" + "xiaoyong" + ".jet");
		// 优化唤醒路径
		mJetDownLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/res/xiaoyong.jet";

		mSp= SharePreferenceUtils.getInstance(context);
	}

	/**
	 * 用这个用话，instance不需要用volatile修饰
	 * @return
	 */
	public static VoiceWakeUp getInstance(Context context) {
		if(instance == null) {
			synchronized(VoiceWakeUp.class) {
				VoiceWakeUp temp = instance;
				if(temp == null) {
					temp = new VoiceWakeUp(context);
					instance = temp;
				}
			}
		}
		return instance;
	}

	@Override
	public void start() {
		if(isRun == true) {
			return;
		}
		isRun = true;
		mIvw = VoiceWakeuper.getWakeuper();

		if (mIvw != null) {
			// 清空参数
			mIvw.setParameter(SpeechConstant.PARAMS, null);
			// 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
			//mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, AudioSource.VOICE_COMMUNICATION+"");
			mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:-50;1:-50");
			// 设置唤醒模式
			mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
			// 设置返回结果格式
			mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
			// 设置持续进行唤醒
			mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
			// 设置闭环优化网络模式
			mIvw.setParameter(SpeechConstant.IVW_NET_MODE, "1");
			// 设置
			mIvw.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
			//
			mIvw.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_IVW);
			// 设置唤醒资源路径
			mIvw.setParameter(SpeechConstant.IVW_RES_PATH, mResPath);

			// 开始无限监听
			mIvw.startListening(new WakeuperListener() {
				
				@Override
				public void onVolumeChanged(int volume) {

				}

				@Override
				public void onResult(WakeuperResult result) {
					if (result != null) {
						synchronized (VoiceWakeUp.class) {
							WakeUpInfo wakeUp = JsonParserUtils.parseResult(result.getResultString(), WakeUpInfo.class);
							if (wakeUp == null) { return; }
							if(wakeUp.getType() == null){return;}
							if(!wakeUp.getType().equals("wakeup")){return;}
							
							
							Log.d(TAG, "id:" + wakeUp.getId() + " wakeup:" + wakeUp.getScore() + " " + result.getResultString());
							if ( wakeUp.getScore() > thresholdValue) {

								// 唤醒亮屏
								PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
								PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
								wl.acquire();
								wl.release();

								Map<String, String> map = new HashMap<>();
								map.put(GeneralData.ACTION, IntentData.INTENT_TOUCH_SENSOR);
								map.put("android.intent.extra.Touch", "t_head");
								SendBroadcastUtils.sendBroad(context, map);
							}
						}
					}
				}

				@Override
				public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

				}

				@Override
				public void onError(SpeechError error) {

				}

				@Override
				public void onBeginOfSpeech() {
					
				}
			});
		}
	}

	@Override
	public void stop() {
		isRun = false;
		if (mIvw != null) {
			mIvw.cancel();
			mIvw.destroy();
			mIvw = null;
		}
	}

	public void setThresholdValue(int thresholdValue){
		mSp.putInt("thresholdValue",thresholdValue);
		this.thresholdValue = thresholdValue;
	}
	public int getThresholdValue(){
		return mSp.getInt("thresholdValue", thresholdValue);
	}
}
