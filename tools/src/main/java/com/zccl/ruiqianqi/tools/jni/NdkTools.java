package com.zccl.ruiqianqi.tools.jni;

/**
 * Created by ruiqianqi on 2016/9/18 0018.
 */
public class NdkTools {

    static {
        System.loadLibrary("function");
    }

    /**
     * 初始化交互接口, 程序启动之后，只运行一次的玩意【目的只是构造上下层沟通的桥梁】
     * @param ndkBridge
     * @param methodName
     */
    public static native void bridgeInit(NdkBridge ndkBridge, String methodName);

    /**
     * 得到底层ABI接口
     */
    public static native String getABI();

    /**
     * 打印点东西
     */
    public static native void print();

    /**
     * 检测五麦能否重置
     * @return 0:fail
     *          1:success
     */
    public static native int reset5Mic();

    public static native int HdmiPowerOFF();
    public static native int HdmiPowerON();
    public static native int HdmiSwitchInternal(int dist);
    public static native int HdmiSwitchExternal(int dist);
    public static native int HdmiDppPowerON();
    public static native int HdmiDppPowerOFF();
    public static native int HdmiDppPowerRate(int dist);
    public static native int HdmiDppConfig(int l_throw,int m_throw,int l_DMD,int m_DMD,int l_PP,int m_PP);
    public static native int StmPinCtl(int pin,int val);
    public static native int SystemBootUp(int val);

    /**
     * 返回光机状态
     * @return 0：光机关
     *          1：光机开
     */
    public static native int HdmiDppPowerStatus();

    public static native int ProjectionStatus();
    /**
     *
     * @return 0:ac out
     *          1:ac in
     */
    public static native int CheckAcCharger();

    /**
     * 设置Gsensor工作方式
     * @param sw 0 向上层全报0
     *            1 向上层报真实数据
     * @return 大于0 表示操作成功
     */
    public static native int GsensorDataSwitch(int sw);

    /**
     * 取得Gsensor数据
     * @param gData x:gData[0]
     *               y:gdata[1]
     *               z:gdata[2]
     * @return
     */
    public static native int GetGsensorData(int[] gData);
}
