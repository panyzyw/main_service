package com.zccl.ruiqianqi.mind.voice.impl;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.yongyida.robot.utils.YYDUart;
import com.yongyida.robot.utils.YYDUartCallback;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.WakeInfo;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public class VoiceWakeUp implements Handler.Callback{

    /** 类日志标志 */
    private static String TAG = VoiceWakeUp.class.getSimpleName();
    /** 单例引用 */
    private static VoiceWakeUp instance;
    /** 录音设备 */
    public static final int RECORD_MODE = 1;

    // 发送数据到串口
    public static final int SEND_DATA_TO_TTY = 1;
    /** 唤醒回调接口 */
    private AbstractVoice.WakeupCallback mWakeupCallback;
    // 需要重启吗
    private volatile boolean needReboot = false;

    /** 生命周期最长的上下文 */
    protected Context mContext;
    // 唤醒的数据串口，描述符
    protected FileDescriptor mWakeUpFd;
    // 唤醒的数据串口，输入流
    private FileInputStream mWakeFis;
    // 是否正在读取唤醒信息
    private boolean isReadWakeUp;
    // 捕获唤醒角度
    private Pattern pattern;

    // 发送传感信息的Handler
    private Handler mSensorHandler;
    // 从串口读取传感信息
    private SensorComm mSensorComm;

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static VoiceWakeUp getInstance(Context context) {
        if(instance == null) {
            synchronized(VoiceWakeUp.class) {
                VoiceWakeUp temp = instance;
                if(temp == null) {
                    temp = new VoiceWakeUp(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    private VoiceWakeUp(Context context) {
        this.mContext = context.getApplicationContext();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        pattern = Pattern.compile(".*WAKE UP!angle:(\\d+)\\D*", Pattern.CASE_INSENSITIVE);
        mSensorHandler = new Handler(this);
        mSensorComm = new SensorComm(mContext, mSensorHandler);
    }

    /**
     * 开始底层录音，循环录音
     */
    public void startWakeUp(){
        LogUtils.e(TAG, "startWakeUp");

        // 先释放之前的
        stopWakeUp();

        MyRxUtils.doNewThreadRun(new Runnable() {
            @Override
            public void run() {
                YYDUart.initUart(new YYDUartCallback(mContext, mSensorHandler) , "getUartData");
                mWakeUpFd = YYDUart.openUart("/dev/ttyS4", 115200, 8, 0, 1, 0);
                mWakeFis = new FileInputStream(mWakeUpFd);
                isReadWakeUp = true;
                while (isReadWakeUp){
                    byte[] data = new byte[256];
                    try {
                        int len = mWakeFis.read(data);
                        if(len > 0){
                            String wakeUpInfo = new String(data, 0, len);
                            LogUtils.e(TAG, wakeUpInfo + "");
                            Matcher matcher = pattern.matcher(wakeUpInfo);
                            // 捕获组
                            if(matcher.find()){
                                // 整个字符串
                                //matcher.group(0);
                                String angle = matcher.group(1);

                                LogUtils.e(TAG, "angle = " + angle);
                                Message msg = mSensorHandler.obtainMessage(1, "angle = " + angle);
                                msg.sendToTarget();

                                WakeInfo wakeInfo = new WakeInfo();
                                try{
                                    Integer angleInt = Integer.parseInt(angle);
                                    wakeInfo.setAngle(angleInt);
                                    wakeInfo.setScore(50);
                                    if(null != mWakeupCallback){
                                        mWakeupCallback.wakeSuccess(wakeInfo);
                                    }
                                }catch (NumberFormatException e){

                                }

                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    /**
     * 停止底层录音
     */
    public void stopWakeUp(){
        LogUtils.e(TAG, "stopWakeUp");

        isReadWakeUp = false;
        if(null != mWakeFis){
            try {
                mWakeFis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mWakeFis =null;
        }

        if(null != mWakeUpFd) {
            YYDUart.closeUart(mWakeUpFd);
            mWakeUpFd = null;
        }

        mSensorComm.stopRecvSensor();
    }

    /**
     * 设置拾音麦口【五麦】
     *   2
     * 3   1
     *   0
     * @param beam
     */
    public void setRealBeam(int beam){

    }

    /**
     * 进行某项功能
     * @param cmd
     * @param obj
     */
    public void sendCommand(int cmd, Object obj){
        if(SEND_DATA_TO_TTY == cmd){
            byte[] data = (byte[]) obj;
            mSensorComm.sendMsgToTTY(data);
        }
    }

    /*************************************【六麦回调】*********************************************/
    @Override
    public boolean handleMessage(Message msg) {
        if(1 == msg.what){
            //MYUIUtils.showToast(mContext, msg.obj + "");
        }
        else if(2 == msg.what){
            //MYUIUtils.showToast(mContext, msg.obj + "");
        }
        return false;
    }

    /**
     * 重启唤醒
     * @param context
     * 返回值：true  表示需要检测
     *         false 表示不需要检测
     */
    public static boolean reboot(Context context){
        // 唤醒监听开启
        VoiceWakeUp.getInstance(context).startWakeUp();
        return true;
    }

    /**************************************自身参数设置********************************************/
    /**
     * 设置唤醒回调接口
     * @param wakeupCallback
     */
    public void setWakeupCallback(AbstractVoice.WakeupCallback wakeupCallback) {
        this.mWakeupCallback = wakeupCallback;
    }

    /**
     * 得到看门狗的值
     * @return
     */
    public boolean isReboot() {
        return needReboot;
    }

    /**
     * 设置看门狗的值
     * @param needReboot
     */
    public void setReboot(boolean needReboot) {
        this.needReboot = needReboot;
    }
}
