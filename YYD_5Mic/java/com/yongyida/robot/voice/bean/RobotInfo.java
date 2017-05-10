package com.yongyida.robot.voice.bean;

import com.yongyida.robot.voice.data.RobotStateData;

public class RobotInfo {
	
	private String mName;
	
	private String mId;
	
	private String mRid;
	
	private String contraller;
	
	private String serial;
	
	private String mOnline;
	
	private String mContrall;
	
	private String mBattery;
	
	private String mRegister;
	
	private static RobotInfo robot;
	
	
	
	private RobotInfo(){
		this.mName = RobotStateData.STATE_DEFAULT_NAME;
		this.mId = RobotStateData.STATE_DEFAULT_ID;
		this.mRid = RobotStateData.STATE_DEFAULT_RID;
		this.contraller = "contraller_default";
		this.serial = "serial_default";
		this.mOnline = RobotStateData.STATE_LOGIN_DEFAULT;
		this.mContrall = RobotStateData.STATE_UNCONTRALL;
		this.mBattery = "null";
		this.mRegister = RobotStateData.STATE_UNREGISTER;
	}

	public static void cleanInfoState(){
		if(robot != null){
			robot = null;
		}
	}
	
	public String getmRegister() {
		return mRegister;
	}

	public void setmRegister(String mRegister) {
		this.mRegister = mRegister;
	}
	
	public String getBattery() {
		return mBattery;
	}

	public void setBattery(String battery) {
		this.mBattery = battery;
	}
	
	
	public String getName() {
		return mName;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public String getId() {
		return mId;
	}

	public void setId(String id) {
		this.mId = id;
	}

	public String getRid() {
		return mRid;
	}

	public void setRid(String rId) {
		this.mRid = rId;
	}

	public String getContraller() {
		return contraller;
	}

	public void setContraller(String contraller) {
		this.contraller = contraller;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getOnline() {
		return mOnline;
	}

	public void setOnline(String online) {
		this.mOnline = online;
	}

	public String getContrallState() {
		return mContrall;
	}

	public void setContrallState(String contrall) {
		this.mContrall = contrall;
	}
	
	public static RobotInfo getInstance(){
		if(robot == null){
			robot = new RobotInfo();
		}
		return robot;
	}
}
