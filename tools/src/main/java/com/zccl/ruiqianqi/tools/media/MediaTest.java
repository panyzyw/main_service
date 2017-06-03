package com.zccl.ruiqianqi.tools.media;

import android.content.Context;

import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by ruiqianqi on 2017/5/13 0013.
 */

public class MediaTest {
    private Context mContext;
    private File mediaLog;
    private FileOutputStream fos;
    private StringBuffer sb;

    protected MediaTest(Context context){
        this.mContext = context;
        this.sb = new StringBuffer();
    }

    /**
     * 创建文件
     */
    protected void create(){
        mediaLog = new File(MyConfigure.ZCCL_SDCARD + System.currentTimeMillis() + ".cvs");
    }

    /**
     * 写文件
     * @param content
     */
    protected void write(String content){
        try {
            fos = new FileOutputStream(mediaLog, true);
            fos.write(content.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭文件流
     */
    protected void close(){
        if(null != fos){
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
