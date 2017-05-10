package com.yongyida.robot.voice.frame.iflytek;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.iflytek.cloud.SpeechError;
import com.yongyida.robot.voice.R;
import com.yongyida.robot.voice.bean.BaseInfo;
import com.yongyida.robot.voice.bean.BroadcastInfo;
import com.yongyida.robot.voice.bean.MainServiceInfo;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.ParseFactory;
import com.yongyida.robot.voice.frame.SpeechInterface;
import com.yongyida.robot.voice.robot.ShowVoiceText;
import com.yongyida.robot.voice.subservice.SubFunction;
import com.yongyida.robot.voice.utils.FileUtil;
import com.yongyida.robot.voice.utils.JsonParserUtils;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.MediaPlayUtils;
import com.yongyida.robot.voice.utils.MediaPlayUtils.CompleteListener;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.robot.voice.utils.ShowToast;

/**
 * 解析语义理解的结果.
 *
 * @author Administrator
 */
public class CommVoiceParse implements SpeechInterface, CompleteListener {

    protected String words;
    protected String json;
    protected SpeechError errorCode;
    protected MainServiceInfo mainInfo = MainServiceInfo.getInstance();
    protected RobotInfo robot = RobotInfo.getInstance();
    protected Context context = MainServiceInfo.getInstance().getContext();
    private BroadcastInfo brcInfo = BroadcastInfo.getInstance();
    protected MediaPlayUtils player = MediaPlayUtils.getInstance();
    protected Random random = new Random();
    protected static boolean recycle = false;
    private ParseFactory factory = new ParseFactory();
    /* 保存对子功能对象 */
    public SubFunction stopSunFunc = null;
    private String action;
    private static int mError = 0;

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void parse() {

        Log.d("jlog", "json:" + json);
        if (json == null) {
            return;
        }
        LogUtils.showLogInfo(GeneralData.SUCCESS, json);

        Map<String, String> map = new HashMap<String, String>();
        map.put(GeneralData.ACTION, IntentData.INTENT_PARSE_RESULT);
        map.put(GeneralData.RESULT, json);
        map.put("rid", RobotInfo.getInstance().getRid());
        SendBroadcastUtils.sendBroad(context, map);
        
        BaseInfo type = JsonParserUtils.parseResult(json, BaseInfo.class);
        if (type == null) {
            return;
        }

        if (SharePreferenceUtils.getInstance(context).getInt("showtext", ShowVoiceText.SHOW_TEXT) == ShowVoiceText.SHOW_TEXT) {
            ShowToast.getInstance(context).show(type.getText());
        }
        switch (type.getSuccess()) {

            case GeneralData.RESULT_THERO:
                parseSuccess();
                break;

            case GeneralData.RESULT_FOUR:
                parseFail();
                break;
        }

    }

    /**
     * 解析成功的结果.
     */
    protected synchronized void parseSuccess() {

        if (json == null) {
            return;
        }
        if (context == null) {
            return;
        }
        BaseInfo type = JsonParserUtils.parseResult(json, BaseInfo.class);
        if (type == null) {
            return;
        }

        SubFunction fun = SubFunction.getFunctions(type.getServiceType());
        if (fun != null) {
            stopSunFunc = fun;
            fun.setContext(context);
            fun.setJson(json);
            fun.run();
            return;
        }

        action = brcInfo.getIntentBroadcast(type.getServiceType());
        String video = "close";
        if (action != null) {
            if (isVideoing()) {
                video = "open";
                LogUtils.showLogInfo("success", "open");
            } else {
                video = "close";
                LogUtils.showLogInfo("success", "close");
            }

            Map<String, String> map1;
            map1 = new HashMap<String, String>();
            map1.put(GeneralData.ACTION, action);
            map1.put(GeneralData.RESULT, json);
            map1.put("video", video);
            SendBroadcastUtils.sendBroad(context, map1);

            FileUtil.putToFile(context, action + "/  " + video, "action.log");
            LogUtils.showLogInfo(GeneralData.SUCCESS, action);

        } else {
            if (factory == null) {
                return;
            }
            factory.setFactory(VoiceRead.getInstence(context));
            factory.parseStart(context.getString(R.string.result_no_find));
        }

    }

    /**
     * 讯飞资源未找到.
     */
    protected void parseFail() {

        //int index = random.nextInt(VoiceData.noResponse.length);
        recycle = false;
        player.clearOnCompleteListener();
        player.playMusic(context, "result_no_find.wav");
    }

    @Override
    public void complete() {

        if (recycle) {
            recycle = false;
            mError++;
            if (mError < 3) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Log.d("jlog", "no send RECYCLE");
                        LogUtils.showLogInfo("success", "INTENT_RECYCLE complete");
                        Map<String, String> map = new HashMap<String, String>();
                        map.put(GeneralData.ACTION, IntentData.INTENT_RECYCLE);
						map.put(GeneralData.FROM, "CommVoiceParse");
                        SendBroadcastUtils.sendBroad(context, map);

                    }
                }, 10);
            } else {
                Log.d("jlog", "end recycle:" + mError);
            }
        } else {
            mError = 0;
        }

    }

    /**
     * 判断是否进入视频状态
     *
     * @return
     */
    public boolean isVideoing() {
        Cursor cursor = null;
        String value = null;
        try {

            if (context == null) {
                return false;
            }
            Uri uri = Uri.parse("content://com.yongyida.robot.video.provider/config");
            cursor = context.getContentResolver().query(uri, null,
                    "name = ?", new String[]{"videoing"}, null);

            if (cursor.moveToFirst())
                value = cursor.getString(cursor.getColumnIndex("value"));
        } catch (Exception e) {
            Log.e("success", "AbsVoiceParse queryVideoing error: " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return (value != null && value.equals("true"));
    }


    public void setWords(String words) {
        this.words = words;
    }

    /**
     * 错误代码处理
     *
     * @param
     */
    public void errorCode() {

        if (errorCode == null) {
            return;
        }
        if (player == null)
            return;
        if (errorCode.getErrorCode() == GeneralData.NOVOICE) {

            Log.d("jlog", "错误码：10118 没有数据");
//            int index = random.nextInt(VoiceData.noResponse.length);
//            player.playMusic(context, VoiceData.noResponse[index]);
        } else {
            player.playMusic(context, "error_network_2.mp3");
        }
    }
}
