package com.yongyida.robot.voice.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.yongyida.robot.idhelper.RobotIDHelper;
import com.yongyida.robot.motor.util.MotorControl;
import com.yongyida.robot.voice.bean.RobotInfo;
import com.yongyida.robot.voice.dao.DatabaseOpera;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.frame.iflytek.VoiceWakeUp;
import com.yongyida.robot.voice.robot.CmdRobot;
import com.yongyida.robot.voice.robot.ShowVoiceText;
import com.yongyida.robot.voice.robot.SwitchVersion;
import com.yongyida.robot.voice.robot.VoiceLocalization;
import com.yongyida.robot.voice.subservice.SubFunctionRegister;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.RobotIDUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;

public class MyApp extends Application {
    private static Context mContext;
    private SharePreferenceUtils sp;
    private VoiceWakeUp wk;
    private VoiceLocalization vl;

    @Override
    public void onCreate() {
        super.onCreate();
        initVariable();
        initSpeech();
        /*注册命令*/
        CmdRobot.cmdRegiser();
        SubFunctionRegister.register();
        getAccount();
        binderMotorService();
    }

    private void initVariable() {
        sp = SharePreferenceUtils.getInstance(this);
        vl=VoiceLocalization.getInstance();
        int value=sp.getInt("thresholdValue",15);      //获取唤醒门限值状态
        wk= VoiceWakeUp.getInstance(this);
        wk.setThresholdValue(value);
        DatabaseOpera db = new DatabaseOpera(this);
        if (!sp.getBoolean("isInsert", false)) {
            sp.putInt("version", SwitchVersion.Y50B_FORMAL_VERSION);
            sp.putBoolean("isInsert", true);
            db.insert();
        } else {
            db.update(sp.getInt("version", SwitchVersion.Y50B_FORMAL_VERSION));
        }
        if (sp.getInt("showtext", -1) == -1) {
            //默认打开录音文本，保留原有设置状态，除非清除数据
            sp.putInt("showtext", ShowVoiceText.SHOW_TEXT);
            db.insert();
        }
        if (sp.getBoolean("rotateState", true)) {
            //获取声源定位状态
            vl.setRotateUp(true);
        } else {
            vl.setRotateUp(false);
        }
        mContext = getApplicationContext();
    }

    private void binderMotorService() {
        Intent intent = new Intent();
        intent.setAction("com.yongyida.robot.MotorService");
        intent.setPackage("com.yongyida.robot.motorcontrol");
        mContext.bindService(intent, MotorControl.motorService, Context.BIND_AUTO_CREATE);
    }

    private void getAccount() {
//        String gsm = RobotIDUtils.getRobotID(this);
        String gsm = RobotIDHelper.Builder.THIS.createIDHelper().getRobotSN();
        if (gsm.isEmpty()) {
            LogUtils.showLogError("success", "获取账号异常");
            RobotInfo.getInstance().setOnline(RobotStateData.STATE_ACCOUNT);
        } else {
            LogUtils.showLogDebug("success", "gsm.serial ; gsm : " + gsm + " ; id : " + getId(gsm)
                    + " ; sid : " + getSId(gsm));
            if (sp != null) {
                sp.putString("id", getId(gsm));
                sp.putString("serial", getSId(gsm));
            }
        }
//		try {
//			ContentResolver resolver;
//			Cursor cursor;
//			Uri uri = Uri.parse("content://com.yongyida.robot.idprovider//id");
//			resolver = getContentResolver();
//			cursor = resolver.query(uri, null, null, null, null);
//			if (cursor.moveToFirst()) {
//				String id = cursor.getString(cursor.getColumnIndex("id"));// id
//				String serial = cursor.getString(cursor.getColumnIndex("sid"));// 序列号
//
//				id = id.trim();
//				serial = serial.trim();
//
//				LogUtils.showLogError("success", "id : " + id + " ; sid : " + serial);
//
//				if(id.equals("") || serial.equals("")){
//					RobotInfo.getInstance().setOnline(RobotStateData.STATE_ACCOUNT);
//				}else{
//					if(sp != null){
//						sp.putString("id", id);
//						sp.putString("serial", serial);
//					}
//				}
//
//				cursor.close();
//
//			}
//		} catch (Throwable e) {
//			RobotInfo.getInstance().setOnline(RobotStateData.STATE_ACCOUNT);
//			e.printStackTrace();
//		}
    }

    private String getId(String gsm) {
        try {
            final String sdKey = gsm.length() > 32 ? gsm.substring(0, 32).trim() : gsm.trim();
            String ids[] = sdKey.split("-");
            return ids[0];
        } catch (Exception e) {
            RobotInfo.getInstance().setOnline(RobotStateData.STATE_ACCOUNT);
        }
        return "";
    }

    private String getSId(String gsm) {
        try {
            final String sdKey = gsm.length() > 32 ? gsm.substring(0, 32).trim() : gsm.trim();
            String ids[] = sdKey.split("-");
            return ids[1];
        } catch (Exception ignored) {
            RobotInfo.getInstance().setOnline(RobotStateData.STATE_ACCOUNT);
        }
        return "";
    }

    /**
     * 初始化语记.
     */
    private void initSpeech() {
        //注意KEY必须与SDK对应，否则异常
        if (RobotStateData.mRotorVoiceKey == RobotStateData.VOICE_KEY_XIAOYONG) {
            //小勇小勇
            //SpeechUtility.createUtility(MyApp.this, SpeechConstant.APPID + "=56065ce8");

            StringBuffer param = new StringBuffer();
            param.append(SpeechConstant.APPID+"=56065ce8");
            param.append(",");
            param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
            SpeechUtility.createUtility(this, param.toString());
			
        } else if (RobotStateData.mRotorVoiceKey == RobotStateData.VOICE_KEY_XIAOER) {
            //小二小二
            SpeechUtility.createUtility(MyApp.this, SpeechConstant.APPID + "=56aadb6d");
        }
    }

    /**
     * 上下文.
     */
    public static Context getContext() {
        return mContext;
    }
}
