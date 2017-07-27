package com.zccl.ruiqianqi.mind.voice.impl.beans;

import com.google.gson.annotations.SerializedName;

public class FiveWakeUpInfo {

	//{"angle":322, "channel":2, "power":313967968256, "CMScore":12, "beam":3}
	// 角度
	@SerializedName("angle")
	private int mAngle;

	// 声道
	@SerializedName("channel")
	private int mChannel;

	// 能量值 
	@SerializedName("power")
	private long mPower;

	// 得分
	@SerializedName("CMScore")
	private int mScore;

	// 拾音波束区域
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
