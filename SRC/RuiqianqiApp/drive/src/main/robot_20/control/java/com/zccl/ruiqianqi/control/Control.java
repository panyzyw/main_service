package com.zccl.ruiqianqi.control;

import com.roboot.hdmictl.HdmiCtl;

/**
 * Created by ruiqianqi on 2017/4/11 0011.
 */

public class Control {

    /**
     * 检测五麦状态
     * @return 1:success  0:fail
     */
    public static int Reset5Mic(){
        return HdmiCtl.Reset5Mic();
    }

    /**
     *
     * @param val 1:on 0:off
     * @return
     */
    public static int LedDanceCtl(int val){
        return 0;
    }

    public static int HdmiPowerOFF(){
        return HdmiCtl.HdmiPowerOFF();
    }

    public static int HdmiPowerON(){
        return HdmiCtl.HdmiPowerON();
    }

    public static int HdmiSwitchInternal(int dist){
        return HdmiCtl.HdmiSwitchInternal(dist);
    }

    public static int HdmiSwitchExternal(int dist){
        return HdmiCtl.HdmiSwitchExternal(dist);
    }

    public static int HdmiDppPowerON(){
        return HdmiCtl.HdmiDppPowerON();
    }

    public static int HdmiDppPowerOFF(){
        return HdmiCtl.HdmiDppPowerOFF();
    }

    public static int HdmiDppPowerRate(int dist){
        return HdmiCtl.HdmiDppPowerRate(dist);
    }

    public static int HdmiDppConfig(int l_throw, int m_throw, int l_DMD, int m_DMD, int l_PP, int m_PP){
        return HdmiCtl.HdmiDppConfig(l_throw, m_throw, l_DMD, m_DMD, l_PP, m_PP);
    }

    public static int StmPinCtl(int pin,int val){
        return HdmiCtl.StmPinCtl(pin, val);
    }

    public static int SystemBootUp(int val){
        return HdmiCtl.SystemBootUp(val);
    }

    /**
     *
     * @return 0：光机关 	 1：光机开
     */
    public static int HdmiDppPowerStatus(){
        return HdmiCtl.HdmiDppPowerStatus();
    }

    public static int ProjectionStatus(){
        return HdmiCtl.ProjectionStatus();
    }

    /**
     *
     * @return 1:ac in    0:ac out
     */
    public static int CheckAcCharger(){
        return HdmiCtl.CheckAcCharger();
    }

    /**
     * sw:1 向上层报真实数据
     * sw:0 向上层全报0
     * @param sw
     * @return 返回值大于0 表示操作成功
     */
    public static int GsensorDataSwitch(int sw){
        return HdmiCtl.GsensorDataSwitch(sw);
    }

    /**
     *
     * @param gData x:gData[0]  y:gdata[1]  z:gdata[2]
     * @return
     */
    public static int GetGsensorData(int[] gData){
        return HdmiCtl.GetGsensorData(gData);
    }

    /**
     * 画面正转或反转
     * @param val 0x7 or 0x6
     * @return
     */
    public static int SetPitrueRotation(int val){
        return HdmiCtl.SetPitrueRotation(val);
    }

    public static int Set5MicSleep(int val){
        return HdmiCtl.Set5MicSleep(val);
    }

    public static int ResetGsensor(int times){
        return HdmiCtl.ResetGsensor(times);
    }

    /**
     *
     * @param val 0:close  1:open
     * @return
     */
    public static int SetCorrectOnOFF(int val){
        return HdmiCtl.SetCorrectOnOFF(val);
    }

}
