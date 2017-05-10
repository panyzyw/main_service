package com.yongyida.robot.voice.bean;

import java.util.Map;

/**
 * 发送服务器的数据
 * 
 * @author "wy"
 *
 */
public class PhotoCmdInfo extends CmdInfo{
	
	public Map<String, String> getPhotoMap() {
		return dataMap;
	}
	public void setPhotoMap(Map<String, String> dataMap) {
		this.dataMap = dataMap;
	}
	public byte[] getPhoto_Data() {
		return byteData;
	}
	public void setPhoto_Data(byte[] byte_Data) {
		this.byteData = byte_Data;
	}
	
}
