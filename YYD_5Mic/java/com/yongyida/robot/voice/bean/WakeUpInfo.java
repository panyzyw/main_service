package com.yongyida.robot.voice.bean;

import com.google.gson.annotations.SerializedName;

public class WakeUpInfo {
//onWakeup{"angle":326,"channel":1,"power":2729960800256,"CMScore":6,"beam":3}
	@SerializedName("angle")
	private int mAngle;
	
	@SerializedName("channel")
	private int mChannel;
		
	@SerializedName("power")
	private long mPower;
	
	@SerializedName("CMScore")
	private int mScore;
	
	@SerializedName("beam")
	private int mBeam;
	

	
	

	public int getAngle() {
		return mAngle;
	}

	public void setAngle(int angle) {
		this.mAngle = angle;
	}
	
	public int getChannel() {
		return mChannel;
	}

	public void setChannel(int channel) {
		this.mChannel= channel;
	}
	
	public long getPower() {
		return mPower;
	}

	public void setPower(long power) {
		this.mPower= power;
	}

	public int getScore() {
		return mScore;
	}

	public void setScore(int mScore) {
		this.mScore = mScore;
	}

	public int getBeam() {
		return mBeam;
	}

	public void setBeam(int beam) {
		this.mBeam = beam;
	}

	
}
