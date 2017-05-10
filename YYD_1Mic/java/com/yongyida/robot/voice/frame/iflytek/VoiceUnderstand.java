package com.yongyida.robot.voice.frame.iflytek;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.yongyida.robot.voice.activity.MonitorExpressionActivity;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.UrlData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.newflytek.MyAppUtils;
import com.yongyida.robot.voice.frame.newflytek.MySpeechUnderstander;
import com.yongyida.robot.voice.robot.TouchHead;
import com.yongyida.robot.voice.utils.MediaPlayUtils.CompleteListener;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 语义理解.
 *
 * @author Administrator
 */
public class VoiceUnderstand extends CommVoiceParse {
	
    private static VoiceUnderstand understand;
    private static int mErrorNum = 0;

    private boolean isRecording = false;

    private RecordingTimer recordingTimer;
    private volatile boolean isNormalTimeOut = true;
    private volatile int timer = 0;
    //private String temp_json;
    public static String temp_json;

    private RecordController recordController;
    private Intent intentRecord;

    public static void setFirst() {
        mErrorNum = 0;
    }
    /**
     * rc = 4的时候，解析处理
     * */
/*
    @Override
    public void parse() {
    	 Log.d("voiceUnderstand", "json:" + json);
         if (json == null) {
             return;
         }
         LogUtils.showLogInfo(GeneralData.SUCCESS, json);
         BaseInfo type = JsonParserUtils.parseResult(json, BaseInfo.class);
         if (type == null) {
        	 return;
         }      
         if (SharePreferenceUtils.getInstance(context).getInt("showtext", ShowVoiceText.SHOW_TEXT) == ShowVoiceText.SHOW_TEXT) {
        	 ShowToast.getInstance(context).show(type.getText());
         }
         switch (type.getSuccess()) {

             case GeneralData.RESULT_THERO: 
            	 Log.d("voiceUnderstand", "first:sucess,rc!=4");
            	 Map<String, String> map = new HashMap<String, String>();
            	 map.put(GeneralData.ACTION, IntentData.INTENT_PARSE_RESULT);
            	 map.put(GeneralData.RESULT, json);
            	 SendBroadcastUtils.sendBroad(context, map);        	 
                 parseSuccess();
                 break;

             case GeneralData.RESULT_FOUR:
                 parseFail();
                 break;
         }
    }
   *//**
     * 重写父类的失败方法，把rc=4的数据放到voiceText中进行文本解析
     * *//*
    @Override
    protected void parseFail() {
    	Log.d("voiceUnderstand", "first:fail,rc=4");
    	JSONObject jsonobject;
		try {
			temp_json = json;
			jsonobject = new JSONObject(json);
			//VoiceText.getInstance(context).setWords(jsonobject.getString("text"))
			VoiceText voiceText = VoiceText.getInstance(context);
			voiceText.praseAgain(jsonobject.getString("text"));
		//	voiceText.setWords(jsonobject.getString("text"));
		//	voiceText.start();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }*/

    /**
     * 在rc = 4时，薄言语义时，去掉讯飞解析失败时的语音
     */
    @Override
    protected void parseFail() {
        // TODO Auto-generated method stub
        //super.parseFail();
    }

    private SpeechUnderstanderListener listener = new SpeechUnderstanderListener() {

        @Override
        public void onVolumeChanged(int arg0, byte[] arg1) {
			if (recordController != null) {
				recordController.volumeChange(arg0);
            }
        }

        @Override
        public void onResult(UnderstanderResult result) {

            if(result!=null && result.getResultString()!=null && result.getResultString().equals("")){

            }else {
				sendStatusBroad("end");
				sendLedBroad(false);
			}
			
			if (recordController != null) {
				recordController.quitRecord();
            }
            stopTimerCycle();

            if (result != null && mainInfo.getBeforComplete() == mainInfo.getComplete()) {
                TouchHead.i = 0;
                json = result.getResultString();
                parse();
                Log.d("jlog", "json:" + json);
            }
            mErrorNum = 1;
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

        }

        @Override
        public void onError(SpeechError error) {

            if (timer < 5 && isNormalTimeOut) {

                Log.d("jlog", "前端点异常超时，重启会话");
                if (speechUnderstander.isUnderstanding()) {
                    speechUnderstander.cancel();
                }
                setParam(GeneralData.LANGUAGE_CHINESE, GeneralData.LANGUAGE_GENERAL,
                        GeneralData.LANGUAGE_BOS_TIME, GeneralData.LANGUAGE_EOS_TIME,
                        GeneralData.LANGUAGE_HAVE_PUNCTUATION,
                        GeneralData.LANGUAGE_WAV_FORMAT, UrlData.LANGUAGE_PATH);
                isRecording = true;
                speechUnderstander.startUnderstanding(listener);
            } else {
				
				sendStatusBroad("end");
				sendLedBroad(false);
				if (recordController != null) {
					recordController.quitRecord();
				}
                stopTimerCycle();

                Log.d("jlog", "error:" + error.getErrorCode());
//            LogUtils.showLogInfo(GeneralData.SUCCESS, "onError ： " + error.getErrorCode());
                if (mainInfo.getFirstRecord()) {
                    errorCode = error;
                    errorCode();
                }
                ++mErrorNum;

                Log.d("jlog", "mErrorNum : " + String.valueOf(mErrorNum));
                if (mErrorNum == 1) {
                    if (player != null) {

                        player.setOnCompleteListener(new CompleteListener() {

                            @Override
                            public void complete() {
                                // TODO Auto-generated method stub
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        Log.d("jlog", "ERROR RECYCLE");
                                        Map<String, String> map2;
                                        map2 = new HashMap<String, String>();
                                        map2.put(GeneralData.ACTION, IntentData.INTENT_RECYCLE);
                                        map2.put(GeneralData.FROM, "VoiceUnderstand");
                                        SendBroadcastUtils.sendBroad(context, map2);
                                    }
                                }, 10);
                            }
                        });
                        int index = random.nextInt(VoiceData.noResponse.length);
                        player.playMusic(context, VoiceData.noResponse[index]);
                    }
                } else {
                    Log.d("jlog", "voice end");
					if (player != null) {
						player.clearOnCompleteListener();
					}
                }
            }
        }

        @Override
        public void onEndOfSpeech() {
            Log.d("jlog", "onEndOfSpeech");
            isRecording = false;
            System.gc();
//            LogUtils.showLogInfo(GeneralData.SUCCESS, "onEndOfSpeech");
//            if (mainInfo.getFirstRecord() && player != null) {
//                player.playMusic(context, "wait_9.mp3");
//                player.setOnCompleteListener(new CompleteListener() {
//
//                    @Override
//                    public void complete() {
//                        // TODO Auto-generated method stub
//                        Log.d("jlog", "player Complete");
//                    }
//                });
//            }

        }

        @Override
        public void onBeginOfSpeech() {
            Log.d("jlog", "onBeginOfSpeech");
        }
    };

    private void stopTimerCycle() {
        isNormalTimeOut = false;
        timer = 0;
    }

    private void sendStatusBroad(String status) {
        Map<String, String> voiceUnderstandStatus;
        voiceUnderstandStatus = new HashMap<String, String>();
        voiceUnderstandStatus.put(GeneralData.ACTION, IntentData.INTENT_VOICE_UNDERSTAND_STATUS);
        voiceUnderstandStatus.put(GeneralData.STATUS, status);
        SendBroadcastUtils.sendBroad(context, voiceUnderstandStatus);
    }

    // 语义理解对象
    private SpeechUnderstander speechUnderstander;

    private VoiceUnderstand(Context context) {
        this.context = context;
        setSpeechUnderstander();
    }
	
    public boolean isListening(){
        boolean ret =false;
        if( null != this.speechUnderstander ){
            ret = this.speechUnderstander.isUnderstanding();
        }
        return ret;
    }

    @Override
    public void start() {

        Log.d("jlog", "Understander start");
        if (speechUnderstander == null) {
            return;
        }

        setSpeechUnderstander();

        //开始录音活动
        if (!MonitorExpressionActivity.isExpressRun) {
            Intent intentRecord = new Intent(context, MonitorExpressionActivity.class);
            intentRecord.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentRecord);
        }

        if (speechUnderstander.isUnderstanding()) {
            speechUnderstander.cancel();
        }
		
		if (player != null) {
            player.clearOnCompleteListener();
        }

        setParam(GeneralData.LANGUAGE_CHINESE, GeneralData.LANGUAGE_GENERAL,
                GeneralData.LANGUAGE_BOS_TIME, GeneralData.LANGUAGE_EOS_TIME,
                GeneralData.LANGUAGE_HAVE_PUNCTUATION,
                GeneralData.LANGUAGE_WAV_FORMAT, UrlData.LANGUAGE_PATH);
        isRecording = true;
        isNormalTimeOut = true;
        timer = 0;
		sendStatusBroad("start");
		sendLedBroad(true);
        speechUnderstander.startUnderstanding(listener);
        startTimerThread();
    }

    private void startTimerThread() {
        if (recordingTimer != null) {
            if (recordingTimer.isAlive()) {
                Log.d("jlog", "计时线程执行中");
            } else {
                recordingTimer = new RecordingTimer();
                recordingTimer.start();
            }
        } else {
            recordingTimer = new RecordingTimer();
            recordingTimer.start();
        }
    }

    @Override
    public void stop() {

        if (speechUnderstander == null) {
            return;
        }
        if (recordController != null) {
            recordController.quitRecord();
        }
        if (speechUnderstander.isUnderstanding()) {
            stopTimerCycle();
			sendStatusBroad("end");
			sendLedBroad(false);
            speechUnderstander.cancel();
            Log.d("jlog", "stop");
        }
        System.gc();
    }


    public static VoiceUnderstand getInstance(Context context) {

        if (understand == null) {
            understand = new VoiceUnderstand(context);
        }

        return understand;
    }

    /**
     * 切换在线版本和离线版本
     */
    public void setSpeechUnderstander(){
        if(MyAppUtils.isNetConnected(context)){
            speechUnderstander = SpeechUnderstander.createUnderstander(context,
                    new InitListener() {
                        @Override
                        public void onInit(int arg0) {

                        }
                    });
        }else {
            speechUnderstander = MySpeechUnderstander.createUnderstander(context,
                    new InitListener() {
                        @Override
                        public void onInit(int arg0) {

                        }
                    });
        }
    }

    /**
     * 参数设置.
     *
     * @param langunage   语言
     * @param type        语言区域
     * @param bosTime     静音超时时间
     * @param eosTime     静音检测时间
     * @param punctuation 标点符号，默认：1（有标点）
     * @param audioFormat 音频格式
     * @param path        音频保存路径
     */
    private void setParam(String langunage, String type, String bosTime,
                          String eosTime, String punctuation, String audioFormat, String path) {

        //speechUnderstander.setParameter(SpeechConstant.AUDIO_SOURCE, AudioSource.VOICE_COMMUNICATION+"");
        speechUnderstander.setParameter(SpeechConstant.LANGUAGE, langunage);
        speechUnderstander.setParameter(SpeechConstant.ACCENT, type);
        speechUnderstander.setParameter(SpeechConstant.VAD_BOS, bosTime);
        speechUnderstander.setParameter(SpeechConstant.VAD_EOS, eosTime);

        speechUnderstander.setParameter(SpeechConstant.ASR_PTT, punctuation);

        speechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT,
                audioFormat);
        speechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, path);
        //使用录音
        speechUnderstander.setParameter(SpeechConstant.AUDIO_SOURCE, null);
        //设置远场模式
        speechUnderstander.setParameter("ent", "smsfar16k");
    }

    public void writeData(byte[] data, int len) {
        //Log.d("jlog", "Understanding... " + speechUnderstander.isUnderstanding());
        if (speechUnderstander.isUnderstanding() && isRecording) {
            speechUnderstander.writeAudio(data, 0, len);
//            Log.d("jlog", "Understanding:" + len);
            //speechUnderstander.getParameter(SpeechConstant.AUDIO_SOURCE);
        }
    }

    private class RecordingTimer extends Thread {
        @Override
        public void run() {
            Log.d("jlog", "计时线程开始执行");
            while (isNormalTimeOut) {
                try {
                    sleep(1000);
                    ++timer;
                    if (timer >= 15) {
                        Log.d("jlog", "录音超时");
                        stopTimerCycle();
                        speechUnderstander.stopUnderstanding();
                        if (speechUnderstander.isUnderstanding()) {
                            speechUnderstander.cancel();
                        }
						sendStatusBroad("end");
						sendLedBroad(false);
						if (recordController != null) {
							recordController.quitRecord();
						}
                        player.clearOnCompleteListener();

                        /*
                        if (MainServiceInfo.getInstance().getNetConnect().equals(RobotStateData.STATE_NET_UNCONNECT)) {
                        }else {
                        }
                        */
                        if(MyAppUtils.isNetConnected(context)) {
                            player.playMusic(context, "error_network_2.mp3");
                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	private void sendLedBroad(boolean onOff) {
        Intent intentMonitorOpen = new Intent("com.yongyida.robot.change.BREATH_LED");
        intentMonitorOpen.putExtra("on_off", onOff);
        intentMonitorOpen.putExtra("place", 3);
        intentMonitorOpen.putExtra("colour", 2);
        intentMonitorOpen.putExtra("frequency", 3);
        intentMonitorOpen.putExtra("Permanent", "monitor");
        intentMonitorOpen.putExtra("priority", 6);
        context.sendBroadcast(intentMonitorOpen);
    }

    public void setRecordController(RecordController controller) {
        this.recordController = controller;
    }

    public void cleanRecordController() {
        this.recordController = null;
    }

    public interface RecordController {
        void startBackgroundAnimation();

        void stopBackgroundAnimation();

        void startMicrophoneAnimation();

        void stopMicrophoneAnimation();

        void volumeChange(int volume);

        void quitRecord();
    }
}
