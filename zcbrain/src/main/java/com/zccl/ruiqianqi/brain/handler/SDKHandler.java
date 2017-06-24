package com.zccl.ruiqianqi.brain.handler;

import android.content.Context;
import android.os.RemoteException;

import com.zccl.ruiqianqi.brain.system.ISDKCallback;
import com.zccl.ruiqianqi.brain.voice.RobotVoice;

import java.io.UnsupportedEncodingException;

/**
 * Created by ruiqianqi on 2017/6/22 0022.
 */

public class SDKHandler {

    // 类标志
    private static String TAG = SDKHandler.class.getSimpleName();

    // 触摸头
    public static final int SENSOR_HEADER = 0;
    // 触摸左肩
    public static final int SENSOR_LEFT_ARM = 1;
    // 触摸右肩
    public static final int SENSOR_RIGHT_ARM = 2;
    // 触摸下巴
    public static final int SENSOR_CHIN = 3;
    // 同时触摸双肩
    public static final int SENSOR_LEFT_RIGHT_ARM = 4;

    // 全局上下文
    protected Context mContext;
    // 音频处理类
    protected RobotVoice mRobotVoice;
    // 采用SDK开发的方式
    private ISDKCallback SDKCallback;

    public SDKHandler(Context context, RobotVoice robotVoice){
        this.mContext = context;
        this.mRobotVoice = robotVoice;
    }

    /**
     * 得到SDK的回调接口
     * @return
     */
    public ISDKCallback getSDKCallback() {
        return SDKCallback;
    }

    /**
     * 设置SDK的回调接口
     * @param SDKCallback
     */
    public void setSDKCallback(ISDKCallback SDKCallback) {
        this.SDKCallback = SDKCallback;
    }

    /**
     * 处理SDK音频
     * @param audio
     * @param audioLen
     * @return
     */
    public boolean onAudio(byte[] audio, int audioLen){
        if(null != getSDKCallback()) {
            try {
                String str = new String(audio, "ISO-8859-1");
                getSDKCallback().onAudio(str, audioLen);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * 相关动作节点，回调SDK客户端
     * @param cmd
     * @param msg
     */
    public void onReceive(int cmd, String msg){
        if(null != getSDKCallback()){
            try {
                getSDKCallback().onReceive(cmd, msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
