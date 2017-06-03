package com.zccl.ruiqianqi.mind.voice.iflytek.beans;

import com.google.gson.annotations.SerializedName;

public class OneWakeUpInfo {

	@SerializedName("id")
	private int mId;
	
	@SerializedName("score")
	private int mScore;
	
	@SerializedName("sst")
	private String mType;
	
	@SerializedName("bos")
	private String mBos;
	
	@SerializedName("eos")
	private String mEos;

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getScore() {
		return mScore;
	}

	public void setScore(int mScore) {
		this.mScore = mScore;
	}

	public String getType() {
		return mType;
	}

	public void setType(String mType) {
		this.mType = mType;
	}

	public String getBos() {
		return mBos;
	}

	public void setBos(String mBos) {
		this.mBos = mBos;
	}

	public String getEos() {
		return mEos;
	}

	public void setEos(String mEos) {
		this.mEos = mEos;
	}
	
	
}
