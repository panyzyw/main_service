package com.yongyida.robot.voice.bean;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.yongyida.robot.voice.data.RobotStateData;

public class MainServiceInfo {
	/* 服务是否销毁 */
	private boolean isServiceDestroy = true;
	
	/* 接收提醒 广播 */
	private Map<String, String> remindMap;
	
	/* 查询用于发送数据 */
	private boolean isRemind;
	
	/*登录闪断*/
	private String loginFlash = RobotStateData.STATE_LOGIN_VOICE_PLAY;
	
	/*网络连接状况*/
	private String netConnect = RobotStateData.STATE_NET_UNCONNECT;
	
	private Context mContext;
	
	private boolean isFirstRecord;

	private boolean isTouchHead;
	
	private boolean isFactory;
	
	private boolean showText;

	private boolean isCall;
	
	private String sendRemand = RobotStateData.STATE_SEND_DATA;

	private boolean isVideo;
	
	private boolean isTouch;

	private boolean isPlayComplete;
	
	private String isOpenVideo;
	
	private boolean mIsMainServiceDestroy = true;
	
	//防止误发消息
	private int complete;
	
	private int beforComplete;

	//private boolean isTouch;
	private static MainServiceInfo mainSerciceInfo;
	
	public MainServiceInfo() {
		complete = 0;
		beforComplete = 0;
		isServiceDestroy = false;
		isRemind = false;
		isFirstRecord = false;
		isTouchHead = false;
		isFactory = false;
		showText = true;
		isVideo = false;
		isTouch = false;
		isPlayComplete = false;
		isOpenVideo = "close";
		isCall = false;
		remindMap = new HashMap<String, String>();
		
	}


	
	public Context getContext() {
		return mContext;
	}


	public void setContext(Context mContext) {
		this.mContext = mContext;
	}


	public static void cleanInfoState(){
		if(mainSerciceInfo != null){
			mainSerciceInfo = null;
		}
	}
	
	
	public String getSendRemand() {
		return sendRemand;
	}

	public void setSendRemand(String sendRemand) {
		this.sendRemand = sendRemand;
	}

	public String getNetConnect() {
		return netConnect;
	}


	public void setNetConnect(String netConnect) {
		this.netConnect = netConnect;
	}

	public String getLoginFlash() {
		return loginFlash;
	}

	public void setLoginFlash(String loginFlash) {
		this.loginFlash = loginFlash;
	}


	public boolean getMainServiceDestroy() {
		return mIsMainServiceDestroy;
	}



	public void setMainServiceDestroy(boolean mIsMainServiceDestroy) {
		this.mIsMainServiceDestroy = mIsMainServiceDestroy;
	}



	public int getComplete() {
		return complete;
	}



	public void setComplete(int complete) {
		this.complete = complete;
	}



	public int getBeforComplete() {
		return beforComplete;
	}



	public void setBeforComplete(int beforComplete) {
		this.beforComplete = beforComplete;
	}



	public boolean getServiceDestroy() {
		return isServiceDestroy;
	}

	public void setServiceDestroy(boolean isServiceDestroy) {
		this.isServiceDestroy = isServiceDestroy;
	}

	public Map<String, String> getRemindMap() {
		return remindMap;
	}

	public void setRemindMap(Map<String, String> remindMap) {
		this.remindMap = remindMap;
	}

	public boolean getRemind() {
		return isRemind;
	}

	public void setRemind(boolean isRemind) {
		this.isRemind = isRemind;
	}

	public boolean getFirstRecord() {
		return isFirstRecord;
	}

	public void setFirstRecord(boolean isFirstRecord) {
		this.isFirstRecord = isFirstRecord;
	}

	public boolean getTouchHead() {
		return isTouchHead;
	}

	public void setTouchHead(boolean isTouchHead) {
		this.isTouchHead = isTouchHead;
	}

	public boolean getFactory() {
		return isFactory;
	}

	public void setFactory(boolean isFactory) {
		this.isFactory = isFactory;
	}

	public boolean getShowText() {
		return showText;
	}

	public void setShowText(boolean showText) {
		this.showText = showText;
	}

	public boolean getCall() {
		return isCall;
	}

	public void setCall(boolean isCall) {
		this.isCall = isCall;
	}

	public boolean getVideo() {
		return isVideo;
	}

	public void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}

	public boolean getTouch() {
		return isTouch;
	}

	public void setTouch(boolean isTouch) {
		this.isTouch = isTouch;
	}

	public boolean getPlayComplete() {
		return isPlayComplete;
	}

	public void setPlayComplete(boolean isPlayComplete) {
		this.isPlayComplete = isPlayComplete;
	}

	public String getOpenVideo() {
		return isOpenVideo;
	}

	public void setOpenVideo(String isOpenVideo) {
		this.isOpenVideo = isOpenVideo;
	}
	
	
	public static MainServiceInfo getInstance(){
		
		if(mainSerciceInfo == null){
			mainSerciceInfo = new MainServiceInfo();
		}
		return mainSerciceInfo;
	}
	
	
	
}
