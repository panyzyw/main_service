package com.yongyida.robot.voice.bean;

import com.google.gson.annotations.SerializedName;

public class BaseInfo {
	@SerializedName("operation")
	private String mOperation;

	@SerializedName("service")
	private String mServiceType;
	
	@SerializedName("rc")
	private int mSuccess;
	
	@SerializedName("text")
	private String mText;

	
	
	
	public String getText() {
		return mText;
	}

	public void setText(String mText) {
		this.mText = mText;
	}

	public int getSuccess() {
		return mSuccess;
	}

	public void setSuccess(int mSuccess) {
		this.mSuccess = mSuccess;
	}

	public String getOperation() {
		return mOperation;
	}

	public void setOperation(String mOperation) {
		this.mOperation = mOperation;
	}

	public String getServiceType() {
		return mServiceType;
	}

	public void setServiceType(String mServiceType) {
		this.mServiceType = mServiceType;
	}
}
