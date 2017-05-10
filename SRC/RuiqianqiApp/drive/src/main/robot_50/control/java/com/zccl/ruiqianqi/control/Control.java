package com.zccl.ruiqianqi.control;

import com.example.y50bpro.yydctl;

/**
 * Created by ruiqianqi on 2017/4/11 0011.
 */

public class Control {

    /**
     * 检测五麦状态
     * @return 1:success  0:fail
     */
    public static int Reset5Mic(){
        return yydctl.Reset5Mic();
    }

    /**
     *
     * @param val 1:on 0:off
     * @return
     */
    public static int LedDanceCtl(int val){
        return yydctl.LedDanceCtl(val);
    }

    public static int HdmiPowerOFF(){
        return 0;
    }

    public static int HdmiPowerON(){
        return 0;
    }

    public static int HdmiSwitchInternal(int dist){
        return 0;
    }

    public static int HdmiSwitchExternal(int dist){
        return 0;
    }

    public static int HdmiDppPowerON(){
        return 0;
    }

    public static int HdmiDppPowerOFF(){
        return 0;
    }

    public static int HdmiDppPowerRate(int dist){
        return 0;
    }

    public static int HdmiDppConfig(int l_throw, int m_throw, int l_DMD, int m_DMD, int l_PP, int m_PP){
        return 0;
    }

    public static int StmPinCtl(int pin,int val){
        return 0;
    }

    public static int SystemBootUp(int val){
        return 0;
    }

    /**
     *
     * @return 0：光机关 	 1：光机开
     */
    public static int HdmiDppPowerStatus(){
        return 0;
    }

    public static int ProjectionStatus(){
        return 0;
    }

    /**
     *
     * @return 1:ac in    0:ac out
     */
    public static int CheckAcCharger(){
        return 0;
    }

    /**
     * sw:1 向上层报真实数据
     * sw:0 向上层全报0
     * @param sw
     * @return 返回值大于0 表示操作成功
     */
    public static int GsensorDataSwitch(int sw){
        return 0;
    }

    /**
     *
     * @param gData x:gData[0]  y:gdata[1]  z:gdata[2]
     * @return
     */
    public static int GetGsensorData(int[] gData){
        return 0;
    }

    /**
     * 画面正转或反转
     * @param val 0x7 or 0x6
     * @return
     */
    public static int SetPitrueRotation(int val){
        return 0;
    }

    public static int Set5MicSleep(int val){
        return 0;
    }

    public static int ResetGsensor(int times){
        return 0;
    }

    /**
     *
     * @param val 0:close  1:open
     * @return
     */
    public static int SetCorrectOnOFF(int val){
        return 0;
    }

}
