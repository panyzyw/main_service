package com.yongyida.robot.voice.robot;

import android.content.Intent;

import com.yongyida.robot.voice.base.BaseCmd;
import com.yongyida.robot.voice.mic.MicTestActivity;
import com.yongyida.robot.voice.mic.SavePcmAudio;

/**
 * Created by sunyibin on 2016/8/6 0006.
 */
public class WritePcmData extends BaseCmd {
    private SavePcmAudio mSavePcmAudio;
    @Override
    public void execute() {
        String data = intent.getStringExtra("data");
        mSavePcmAudio=new SavePcmAudio();
        if(data.equals("startSavePcm")){
            mSavePcmAudio.setIsSave(true);
        }else if(data.equals("stopSavePcm")){
            mSavePcmAudio.setIsSave(false);
        }else if(data.equals("startMicTest")){
            Intent intent = new Intent(context, MicTestActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
