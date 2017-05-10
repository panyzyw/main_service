package com.zccl.ruiqianqi.mind.voice.iflytek;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by ruiqianqi on 2016/7/21 0021.
 */
public abstract class BaseVoice {

    /** 生命周期最长的上下文 */
    protected Context mContext;

    /** 语音数据来源 */
    public enum DATA_SOURCE_TYPE{
        TYPE_RECORD,
        TYPE_RAW_DATA,
    }
    /** 语音数据来源 */
    protected DATA_SOURCE_TYPE mDataSourceType = DATA_SOURCE_TYPE.TYPE_RECORD;
    /** 主线程Handler */
    protected Handler handler;

    protected BaseVoice(Context context){
        this.mContext = context.getApplicationContext();
        initBase();
    }

    /**
     * 基类初始化
     */
    private void initBase(){
        handler = new Handler(Looper.getMainLooper());
        if(VoiceWakeUp.RECORD_MODE==1){
            mDataSourceType = DATA_SOURCE_TYPE.TYPE_RECORD;
        }else {
            mDataSourceType = DATA_SOURCE_TYPE.TYPE_RAW_DATA;
        }
    }

}
