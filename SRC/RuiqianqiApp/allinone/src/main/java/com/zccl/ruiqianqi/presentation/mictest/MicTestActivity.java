package com.zccl.ruiqianqi.presentation.mictest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.mic.MIC;
import com.zccl.ruiqianqi.mind.voice.allinone.R;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MicTestActivity extends Activity {
    private Button recordTest,resetBtn;
    private Toast mToast;
    private MediaPlayer mediaPlayer;
    private SavePcmAudio mSavePcmAudio;
    private static final String RECORD_TEST_FILE = "msc/mic/recordTest.pcm";
    private static final int MIC0=1,MIC1=7,MIC2=2,MIC3=8,MIC4=3,MIC6=4,MIC7=10;
    private ImageView forwardMic,midMic,leftMic,rightMic,backMic,speak1Mic,speak2Mic;
    private TextView testState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mic_test);
        init_view();
        recordTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTip("开始录音测试");
                mSavePcmAudio.setIsSave(true,"mic");
                mediaPlayer.start();
                testState.setTextColor(Color.parseColor("#FFEA0C2D"));
                testState.setText("测试进行中...请勿操作！！！");
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mSavePcmAudio.setIsSave(false,"mic");
                        recordTest(getApplicationContext());
                    }
                });
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
            }
        });
    }
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MIC0:
                    forwardMic.setBackground(getResources().getDrawable(R.drawable.light_green));
                    break;
                case MIC1:
                    rightMic.setBackground(getResources().getDrawable(R.drawable.light_green));
                    break;
                case MIC2:
                    backMic.setBackground(getResources().getDrawable(R.drawable.light_green));
                    break;
                case MIC3:
                    leftMic.setBackground(getResources().getDrawable(R.drawable.light_green));
                    break;
                case MIC4:
                    midMic.setBackground(getResources().getDrawable(R.drawable.light_green));
                    break;
                case MIC6:
                    speak1Mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                    break;
                case MIC7:
                    speak2Mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                    break;
            }
        }
    };
    private Handler mHandlerTest=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            testState.setText("测试结束！！！");
            testState.setTextColor(Color.parseColor("#FF07F12A"));
        }
    };
    private void recordTest(Context context)
    {
        LOGD( "startRecTest() is called" );
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                /************************************录音测试代码***************************************************/
                try {
                    File file = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + RECORD_TEST_FILE);
                    FileInputStream fin = new FileInputStream(file);
                    int length = fin.available();
                    if(length <= 0)
                    {
                        LOGD("test file is null, please check /mnt/sdcard/" + RECORD_TEST_FILE);
                    }else {
                        byte [] bbuffer = new byte[length];
                        fin.read(bbuffer);
                        int[] ibuffer = bytesToInt(bbuffer, length);
                        int ret[] = MIC.recordTestWr(ibuffer, ibuffer.length - 1280);
                        for(int i = 0; i < 13; i++)
                        {
                            if (ret[i] != 0) {
                                LOGD( "record test fail, reason is " + ret[i] );
                            } else {
                                LOGD("record test pass, reason is " + ret[i]);
                                Message message=new Message();
                                message.what=i;
                                mHandler.sendMessage(message);
                            }
                        }
                        showTip("测试结束");
                        mHandlerTest.sendEmptyMessage(0);
                    }
                    fin.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    LOGD(e.getMessage());
                }
                /****************************************录音测试代码***********************************************/
            }
        }, 3 * 1000);
    }
    private static void LOGD(String str)
    {
        Log.d("jlog", str);
    }
    private void showTip(String str)
    {
        if(!TextUtils.isEmpty(str))
        {
            mToast.setText(str);
            mToast.show();
        }
    }
    private void init_view(){
        mediaPlayer= MediaPlayer.create(MicTestActivity.this, R.raw.recordtest);
        mToast = Toast.makeText(this, "", Toast.LENGTH_LONG);
        mSavePcmAudio=new SavePcmAudio();
        recordTest=(Button)findViewById(R.id.record_test);
        forwardMic=(ImageView) findViewById(R.id.forward_mic);
        backMic=(ImageView)findViewById(R.id.back_mic);
        leftMic=(ImageView)findViewById(R.id.left_mic);
        rightMic=(ImageView)findViewById(R.id.right_mic);
        midMic=(ImageView)findViewById(R.id.mid_lamp);
        speak1Mic=(ImageView)findViewById(R.id.speak_mic_1);
        speak2Mic=(ImageView)findViewById(R.id.speak_mic_2);
        resetBtn=(Button)findViewById(R.id.reset_btn);
        testState=(TextView)findViewById(R.id.test_state_txt);
    }
    private int[] bytesToInt(byte[] src, int length) {
        int value = 0 , offset = 0, i = 0;
        int[] ret = new int[length/4 + 1];
        while(offset < length && length - offset >= 4)
        {
            value = (int) ((src[offset] & 0xFF)
                    | ((src[offset+1] & 0xFF)<<8)
                    | ((src[offset+2] & 0xFF)<<16)
                    | ((src[offset+3] & 0xFF)<<24));
            offset += 4;
            ret[i] = value;
            i ++;
        }
        return ret;
    }
    private void resetAll(){
        forwardMic.setBackground(getResources().getDrawable(R.drawable.light_white));
        leftMic.setBackground(getResources().getDrawable(R.drawable.light_white));
        rightMic.setBackground(getResources().getDrawable(R.drawable.light_white));
        midMic.setBackground(getResources().getDrawable(R.drawable.light_white));
        backMic.setBackground(getResources().getDrawable(R.drawable.light_white));
        speak1Mic.setBackground(getResources().getDrawable(R.drawable.light_white));
        speak2Mic.setBackground(getResources().getDrawable(R.drawable.light_white));
    }
    public void button001(View view) {
        Intent intentSuccess=new Intent("com.yongyida.factorytest.micTestSuccess");
        intentSuccess.putExtra("result",true);
        intentSuccess.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intentSuccess);
        finish();
    }
    public void button002(View view) {
        Intent intentSuccess=new Intent("com.yongyida.factorytest.micTestSuccess");
        intentSuccess.putExtra("result",false);
        intentSuccess.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intentSuccess);
        finish();
    }
}
