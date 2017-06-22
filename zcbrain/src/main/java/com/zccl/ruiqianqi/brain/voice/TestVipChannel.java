package com.zccl.ruiqianqi.brain.voice;

import android.content.Context;

import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by ruiqianqi on 2017/5/13 0013.
 */

public class TestVipChannel {
    private Context mContext;
    private AbstractVoice mVoice;
    protected final String startVip = "开启通道测试";
    protected final String endVip = "关闭通道测试";
    protected final String index = "request\tresponse\tdiff\tstatus\ttext\n";
    private FileOutputStream fos;
    private StringBuffer sb;
    private long requestTimeStamp;
    private long responseTimeStamp;
    private boolean isTestVip = false;

    protected TestVipChannel(Context context, AbstractVoice voice){
        this.mContext = context;
        this.mVoice = voice;
        this.sb = new StringBuffer();

    }

    /**
     * 创建文件
     */
    protected void create(){
        File file = new File(MyConfigure.SDCARD + System.currentTimeMillis() + ".cvs");
        try {
            fos = new FileOutputStream(file);
            write(index);
            isTestVip = true;
            mVoice.startTTS("通道测试已开启", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写文件
     * @param content
     */
    protected void write(String content){
        if(!isTestVip())
            return;
        if(null != fos){
            try {
                fos.write(content.getBytes(Charset.defaultCharset()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onEndOfSpeech(){
        if(!isTestVip())
            return;
        sb.setLength(0);
        requestTimeStamp = System.currentTimeMillis();
        sb.append(requestTimeStamp + "\t");
    }

    protected void onResult(String text){
        if(!isTestVip())
            return;
        responseTimeStamp = System.currentTimeMillis();
        sb.append(responseTimeStamp + "\t" + (responseTimeStamp - requestTimeStamp) + "\tsuccess\t" + text + "\n");
        write(sb.toString());
    }

    protected void onError(String errMsg){
        if(!isTestVip())
            return;
        responseTimeStamp = System.currentTimeMillis();
        sb.append(responseTimeStamp + "\t" + (responseTimeStamp - requestTimeStamp) + "\tfailure\t" + errMsg + "\n");
        write(sb.toString());
    }

    /**
     * 关闭文件流
     */
    protected void close(){
        if(null != fos){
            try {
                fos.close();
                isTestVip = false;
                mVoice.startTTS("通道测试已关闭", null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是不是在进行VIP通道测试
     * @return
     */
    public boolean isTestVip() {
        return isTestVip;
    }
}
