package com.zccl.ruiqianqi.mind.voice.impl;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.yongyida.robot.utils.YYDUart;
import com.zccl.ruiqianqi.mind.voice.allinone.R;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ruiqianqi on 2017/7/15 0015.
 */

public class SensorComm {

    // 类标志
    private static String TAG = SensorComm.class.getSimpleName();

    // 触摸底层串口
    private final String COMM = "/dev/ttyS2";
    /** 生命周期最长的上下文 */
    protected Context mContext;
    // 唤醒的数据串口，描述符
    protected FileDescriptor mSensorFd;
    // 唤醒的数据串口，输入流
    private FileInputStream mSensorFis;
    // 唤醒的数据串口，输出流
    private FileOutputStream mSensorFos;
    // 消息发送的HANDLER
    private Handler mSensorHandler;
    // 是否正在读取传感信息
    private boolean isReadSensor;

    protected SensorComm(Context context, Handler sensorHandler){
        this.mContext = context;
        this.mSensorHandler = sensorHandler;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        startRecvSensor(COMM);
    }

    /**
     * 开始接收底层的传感信息
     */
    public void startRecvSensor(final String comm){
        LogUtils.e(TAG, "startRecvSensor " + comm);

        MyRxUtils.doNewThreadRun(new Runnable() {
            @Override
            public void run() {
                mSensorFd = YYDUart.openUart(comm, 115200, 8, 0, 1, 0);
                if(null != mSensorFd) {
                    LogUtils.e(TAG, "open " + comm + " success");
                    mSensorFos = new FileOutputStream(mSensorFd);
                    mSensorFis = new FileInputStream(mSensorFd);
                    isReadSensor = true;
                    while (isReadSensor) {
                        byte[] data = new byte[64];
                        try {
                            int len = mSensorFis.read(data);
                            LogUtils.e(TAG, comm + " ReadSensorLen = " + len);
                            if (len > 0) {
                                LogUtils.e(TAG, comm + " data[0] = " + data[0]);
                                LogUtils.e(TAG, comm + " data[1] = " + data[1]);
                                LogUtils.e(TAG, comm + " data[2] = " + data[2]);
                                LogUtils.e(TAG, comm + " data[4] = " + data[4]);
                                if(0xAA == data[0] && 0xBB == data[1] && 0x04 == data[2]){
                                    // 前额
                                    if(0x01 == data[4]){
                                        sendSensor(mContext.getString(R.string.qian_e));
                                    }
                                    // 后脑勺
                                    else if(0x02 == data[4]){
                                        sendSensor(mContext.getString(R.string.hou_nao_shao));
                                    }
                                    // 左肩
                                    else if(0x04 == data[4]){
                                        sendSensor(mContext.getString(R.string.zuo_jian));
                                    }
                                    // 左手臂
                                    else if(0x08 == data[4]){
                                        sendSensor(mContext.getString(R.string.zuo_shou_bi));
                                    }
                                    // 右肩
                                    else if(0x10 == data[4]){
                                        sendSensor(mContext.getString(R.string.you_jian));
                                    }
                                    // 右手臂
                                    else if(0x20 == data[4]){
                                        sendSensor(mContext.getString(R.string.you_shou_bi));
                                    }

                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }else {
                    LogUtils.e(TAG, "open " + comm + " failed");
                }
            }
        });
    }

    /**
     * 发送数据给串口
     * @param data
     */
    protected void sendMsgToTTY(byte[] data){
        try {
            mSensorFos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止接收底层的传感信息
     */
    public void stopRecvSensor(){
        LogUtils.e(TAG, "stopRecvSensor");

        isReadSensor = false;
        if(null != mSensorFis){
            try {
                mSensorFis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSensorFis = null;
        }

        if(null != mSensorFos){
            try {
                mSensorFos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mSensorFos = null;
        }

        if(null != mSensorFd) {
            YYDUart.closeUart(mSensorFd);
            mSensorFd = null;
        }
    }

    /**
     * 发送传感信息
     * @param value
     */
    private void sendSensor(String value){
        Bundle args = new Bundle();
        args.putString("android.intent.extra.Touch", value);
        MyAppUtils.sendBroadcast(mContext, "TouchSensor", args);

        mSensorHandler.obtainMessage(2, value).sendToTarget();
    }
}
