package com.zccl.ruiqianqi.presentation.mictest;

import android.os.Environment;
import android.util.Log;

/**
 * Created by 软件组 on 2016/9/10.
 */
public class SavePcmAudio {
    private static boolean saveAudio=false;
    private static PcmFileUtil pcmFileUtil;
    private  String mWakeAudioDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/audio/";
    private  String mWakeEchoDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/echo/";
    private  String mWakeMicDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/mic/";
    public boolean getSave(){
        return saveAudio;
    }
    public void setIsSave(boolean save){
        this.saveAudio=save;
        if(save){
            pcmFileUtil=new PcmFileUtil(mWakeAudioDir);
            pcmFileUtil.createPcmFile();
        }
    }
    public void setIsSave(boolean save,String dir){
        if(dir.equals("echo")){
            this.saveAudio=save;
            if(save){
                pcmFileUtil=new PcmFileUtil(mWakeEchoDir);
                pcmFileUtil.createPcmFile("echoTest");
            }
        }else if(dir.equals("mic")){
            this.saveAudio=save;
            if(save){
                pcmFileUtil=new PcmFileUtil(mWakeMicDir);
                pcmFileUtil.createPcmFile("recordTest");
            }
        }

    }
    public void writeAudio(byte[] data,int len){
        if(pcmFileUtil!=null){
            if(saveAudio){
                Log.d("slog","isSave");
                pcmFileUtil.write(data);
            }else {
                pcmFileUtil.closeWriteFile();
            }
        }

    }
}
