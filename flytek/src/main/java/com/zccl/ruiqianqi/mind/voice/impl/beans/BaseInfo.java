package com.zccl.ruiqianqi.mind.voice.impl.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @Expose @Expose标签的2个属性.
 *	deserialize (boolean) 反序列化 默认 true
 *	serialize  (boolean)  序列化  默认 true
 * @SerializedName 标签定义属性序列化后的名字
 */
public class BaseInfo {

	// 动作类型，有时没有返回，因为不规则的语句不好解析
	@SerializedName("operation")
	@Expose
	protected String mOperation;

	// 请求的服务类型
	@SerializedName("service")
	@Expose
	protected String mServiceType;

	// 0表示成功，4表示失败
	@SerializedName("rc")
	@Expose
	protected int mSuccess = 4;

	// 请求的上行语句
	@SerializedName("text")
	@Expose
	protected String mText;

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
