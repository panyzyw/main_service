package com.roboot.hdmictl;

public class HdmiCtl {
	static {
		System.loadLibrary("hdmictl");
	}
	public static native int HdmiPowerOFF(); 
	public static native int HdmiPowerON(); 
	public static native int HdmiSwitchInternal(int dist);
	public static native int HdmiSwitchExternal(int dist);
	public static native int HdmiDppPowerON();
	public static native int HdmiDppPowerOFF();
	public static native int HdmiDppPowerRate(int dist);
	public static native int HdmiDppConfig(int l_throw, int m_throw, int l_DMD, int m_DMD, int l_PP, int m_PP);
	public static native int StmPinCtl(int pin,int val);
	public static native int SystemBootUp(int val);
	public static native int HdmiDppPowerStatus();// 返回 0：光机关 	 1：光机开
	public static native int ProjectionStatus();
	public static native int CheckAcCharger();//1:ac in    0:ac out
	public static native int Reset5Mic();     //1:success  0:fail
	public static native int GsensorDataSwitch(int sw);  // sw:1 向上层报真实数据 sw:0向上层全报0。  返回值大于0 表示操作成功
	public static native int GetGsensorData(int[] gData);//x:gData[0]  y:gdata[1]  z:gdata[2]
	public static native int SetPitrueRotation(int val); //val= 0x7 or 0x6  画面正转或反转
	public static native int Set5MicSleep(int val);
	public static native int ResetGsensor(int times);
	public static native int SetCorrectOnOFF(int val);// val=0 :close;  1:open

}
