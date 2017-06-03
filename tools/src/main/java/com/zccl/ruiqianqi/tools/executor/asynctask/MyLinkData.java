package com.zccl.ruiqianqi.tools.executor.asynctask;

import android.os.Parcel;
import android.os.Parcelable;

import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

public class MyLinkData implements Parcelable {

	/********************** 进程间数据传递 **********************/
	/** 二级指令 */
	private int cmd = -1;
	/** 用来保存运行状态的 */
	private String state = MyConfigure.ERROR;
	/** 携带的参数 */
	private String args = null;
	/** 唯一的KEY值 */
	private String key = null;
	/** 一个tag */
	private String tag = null;

	/********************** 本进程数据传递 **********************/
	/** 本进程传递时——用AsyncTask作任务时的结果处理  */
	private MyAsyncTask.OnAsyncResultCallback resultCallback = null;

	/** 本进程传递时——携带的其他接口或对象 */
	private Object carryObject = null;
	/** 本进程传递时——返回的数据对象 */
	private Object fromLocaLApp = MyConfigure.OKEY;

	/********************** 其他进程数据传递 **********************/
	/** 跨进程传递时——返回的数据字符串 */
	private String fromOtherApp = MyConfigure.OKEY;

	public MyLinkData() {
	}

	public MyLinkData(Parcel source) {
		cmd = source.readInt();
		args = source.readString();
		key = source.readString();
		tag = source.readString();
		state = source.readString();
		fromOtherApp = source.readString();
	}

	/**
	 * 二级指令（可序列化）
	 * @return
	 */
	public int getCmd() {
		return cmd;
	}

	/**
	 * 二级指令（可序列化）
	 * @param cmd
	 */
	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	/**
	 * 字符串参数（可序列化）
	 * @return
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * 字符串参数（可序列化）
	 * @param args
	 */
	public void setArgs(String args) {
		this.args = args;
	}

	/**
	 * 唯一键值（可序列化）
	 * @return
	 */
	public String getKey() {
		return key;
	}

	/**
	 * 生成唯一键值
	 */
	public void generateKey(String key) {
		if(StringUtils.isEmpty(key)) {
			this.key = CheckUtils.getRandomString();
		}else{
			this.key = key;
		}
	}

	/**
	 * 携带的TAG（可序列化）
	 * @return
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * 携带的TAG（可序列化）
	 * @param tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 运行结果（可序列化）
	 * @return
	 */
	public String getState() {
		return state;
	}

	/**
	 * 运行结果（可序列化）
	 * @param state
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * 跨进程传递时——用作携带String类型的对象（可序列化）
	 * @return
	 */
	public String getOtherData() {
		return fromOtherApp;
	}

	/**
	 * 跨进程传递时——设置其他进程携带的数据（可序列化）
	 * @param otherdata
	 */
	public void setOtherData(String otherdata) {
		this.fromOtherApp = otherdata;
	}

	/**
	 * 反序列化
	 */
	public static final Creator<MyLinkData> CREATOR = new Creator<MyLinkData>() {

		@Override
		public MyLinkData createFromParcel(Parcel source) {
			return new MyLinkData(source);
		}

		@Override
		public MyLinkData[] newArray(int size) {
			return new MyLinkData[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * 序列化
	 * @param dest
	 * @param flags
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(cmd);
		dest.writeString(args);
		dest.writeString(key);
		dest.writeString(tag);
		dest.writeString(state);
		dest.writeString(fromOtherApp);
	}


	/**
	 * 本进程传递时——携带的其他接口或对象（默认非序列化）
	 * @return
	 */
	public Object getCarryObject() {
		return carryObject;
	}

	/**
	 * 本进程传递时——携带的其他接口或对象（默认非序列化）
	 * @param carryObject
	 */
	public void setCarryObject(Object carryObject) {
		this.carryObject = carryObject;
	}
	/**
	 * 本进程传递时——返回的数据对象（默认非序列化）
	 * @return
	 */
	public Object getLocalData() {
		return fromLocaLApp;
	}

	/**
	 * 本进程传递时——返回的数据对象（默认非序列化）
	 * 
	 * @param localdata
	 */
	public void setLocalData(Object localdata) {
		this.fromLocaLApp = localdata;
	}

	/**
	 * 线程池任务返回接口
	 * @return
	 */
	public MyAsyncTask.OnAsyncResultCallback getResultCallback() {
		return resultCallback;
	}

	/**
	 * 线程池任务返回接口
	 * @param resultCallback
	 */
	public void setResultCallback(MyAsyncTask.OnAsyncResultCallback resultCallback) {
		this.resultCallback = resultCallback;
	}
}
