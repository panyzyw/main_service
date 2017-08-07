package com.zccl.ruiqianqi.presentation.mictest;

import android.os.Environment;
import android.util.Log;

import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 软件组 on 2016/9/10.
 */
public class SavePcmAudio {

    // PCM数据存放路径
    public static final String PCM_DIR = MyConfigure.SDCARD + "/msc/mic/";
    // 文件名
    public static final String PCM_RECORD = "recordTest";
    // 文件后缀
    public static final String PCM_SUFFIX = ".pcm";
    // 文件输出流
    private static FileOutputStream mPcmFos;
    // 是不是正在保存音频
    private static boolean isSavingAudio;

    /**
     * 是不是开始保存音频
     * @param saving
     */
    public static void setSavingAudio(boolean saving){
        isSavingAudio = saving;
        if(isSavingAudio){
            createPcmFile(PCM_RECORD);
        }
    }

    /**
     * 创建PCM文件
     * @param fileName
     */
    private static void createPcmFile(String fileName) {
        File dir = new File(PCM_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (null != mPcmFos) {
            return;
        }
        String pcmPath = PCM_DIR + fileName + PCM_SUFFIX;
        if(StringUtils.isEmpty(fileName)){
            pcmPath = PCM_DIR + PCM_RECORD + PCM_SUFFIX;
        }
        File pcm = new File(pcmPath);
        if(pcm.exists()){
            pcm.delete();
        }
        try {
            if(pcm.createNewFile()) {
                mPcmFos = new FileOutputStream(pcm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 外部一直在输入
     * @param data
     * @param len
     */
    public static void writeAudio(byte[] data, int len){
        if(isSavingAudio){
            write(data, 0, len);
        }else {
            stopWrite();
        }
    }


    /**
     * 写PCM数据到文件
     * @param data
     */
    private static void write(byte[] data) {
        if (null != mPcmFos) {
            try {
                mPcmFos.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写PCM数据到文件
     * @param data
     * @param offset
     * @param len
     */
    private static void write(byte[] data, int offset, int len) {
        if (null != mPcmFos) {
            try {
                mPcmFos.write(data, offset, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止写PCM
     */
    private static void stopWrite() {
        if (null != mPcmFos) {
            try {
                mPcmFos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPcmFos = null;
        }
    }
}
