package com.zccl.ruiqianqi.tools.media;

import android.content.Context;
import android.media.MediaRecorder;

import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public class MyMediaRecorder {
    /** 类标识 */
    private static String TAG = MyMediaRecorder.class.getSimpleName();

    /** 录音类 */
    private MediaRecorder mediaRecorder;

    /** 生命周期最长的上下文 */
    private Context mContext;
    // 文件保存路径
    private String mFilePath;

    public MyMediaRecorder(Context context){
        this.mContext = context.getApplicationContext();
        mediaRecorder = new MediaRecorder();
    }

    /**
     * 初始化
     */
    private void init(){
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        setRecordListener();
    }

    /**
     * 设置录音回调接口
     */
    private void setRecordListener(){
        if(null != mediaRecorder){
            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    LogUtils.e(TAG, "onInfo = " + what + "-" + extra);
                }
            });

            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    LogUtils.e(TAG, "onError = " + what + "-" + extra);
                }
            });
        }
    }

    /**
     * 设置输出的文件路径
     * @param filePath 文件路径
     */
    public void setOutputFile(String filePath) {
        this.mFilePath = filePath;
        mediaRecorder.setOutputFile(filePath);
    }

    /**
     * 录音准备
     */
    public void prepare(){
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录音
     */
    public void startRecord(){
        mediaRecorder.start();
    }

    /**
     * 结束录音
     */
    public void stopRecord(){
        mediaRecorder.stop();
    }

    /**
     * 释放资源
     */
    public void release(){
        stopRecord();

        mediaRecorder.release();
        if(!StringUtils.isEmpty(mFilePath)) {
            new File(mFilePath).delete();
        }
    }

    /**
     * 重置
     */
    public void reset(){
        mediaRecorder.reset();
        init();
    }

}
