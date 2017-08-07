package com.zccl.ruiqianqi.presentation.mictest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.mic.MIC;
import com.zccl.ruiqianqi.mind.voice.allinone.R;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MYUIUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/9/20 0020.
 */
public class MicTestActivity extends Activity {

    // 类标志
    private static String TAG = MicTestActivity.class.getSimpleName();

    // 麦克1
    private static final int MIC_1 = 1;
    // 麦克2
    private static final int MIC_2 = 7;
    // 麦克3
    private static final int MIC_3 = 2;
    // 麦克4
    private static final int MIC_4 = 8;
    // 麦克5
    private static final int MIC_5 = 3;
    // 麦克6
    private static final int MIC_6 = 9;
    // 喇叭1
    private static final int SPEAK_1 = 4;
    // 喇叭2
    private static final int SPEAK_2 = 10;

    private ImageView speak_mic_1;
    private ImageView speak_mic_2;
    private TextView test_result;

    private ImageView one_mic;
    private ImageView two_mic;
    private ImageView three_mic;
    private ImageView four_mic;
    private ImageView five_mic;
    private ImageView six_mic;

    private Button record_test;

    private MediaPlayer mMediaPlayer;

    // UI处理
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                test_result.setText("测试结束！！！");
                test_result.setTextColor(Color.parseColor("#FF07F12A"));
                record_test.setClickable(true);
            }else {
                LogUtils.e(TAG, "测试结果：" + msg.what);
                switch (msg.what){
                    case MIC_1:
                        one_mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                    case MIC_2:
                        two_mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                    case MIC_3:
                        three_mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                    case MIC_4:
                        four_mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                    case MIC_5:
                        five_mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                    case MIC_6:
                        six_mic.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                    case SPEAK_1:
                        speak_mic_1.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                    case SPEAK_2:
                        speak_mic_2.setBackground(getResources().getDrawable(R.drawable.light_green));
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mic_test);

        init();
    }

    /**
     * 初始化
     */
    private void init(){
        initData();
        initView();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        mMediaPlayer = MediaPlayer.create(MicTestActivity.this, R.raw.recordtest);
    }

    /**
     * 初始化VIEW
     */
    private void initView(){
        speak_mic_1 = (ImageView) findViewById(R.id.speak_mic_1);
        speak_mic_2 = (ImageView) findViewById(R.id.speak_mic_2);
        test_result = (TextView) findViewById(R.id.test_result);

        one_mic = (ImageView) findViewById(R.id.one_mic);
        two_mic = (ImageView) findViewById(R.id.two_mic);
        three_mic = (ImageView) findViewById(R.id.three_mic);
        four_mic = (ImageView) findViewById(R.id.four_mic);
        five_mic = (ImageView) findViewById(R.id.five_mic);
        six_mic = (ImageView) findViewById(R.id.six_mic);

        record_test = (Button) findViewById(R.id.record_test);
    }

    /**
     * 开始分析
     */
    private void recordAnalyse(){

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    File file = new File(SavePcmAudio.PCM_DIR + SavePcmAudio.PCM_RECORD + SavePcmAudio.PCM_SUFFIX);
                    FileInputStream fin = new FileInputStream(file);
                    if(null != fin) {
                        int length = fin.available();
                        if (length <= 0) {
                            LogUtils.e(TAG, "pcm file is null, please check " + file.getAbsolutePath());
                        } else {
                            byte[] byteBuf = new byte[length];
                            fin.read(byteBuf);
                            int[] intBuf = bytesToInt(byteBuf, length);
                            int ret[] = MIC.recordTestWr(intBuf, intBuf.length - 1280);
                            for (int i = 0; i < 13; i++) {
                                if (ret[i] != 0) {
                                    LogUtils.e(TAG, "record test fail, reason is " + ret[i]);
                                } else {
                                    Message message = new Message();
                                    message.what = i;
                                    mHandler.sendMessage(message);
                                }
                            }
                            //MYUIUtils.showToast(MicTestActivity.this, "测试结束");
                            mHandler.sendEmptyMessage(0);
                        }
                        fin.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3 * 1000);
    }

    /**
     * 数据转换
     * @param src
     * @param length
     * @return
     */
    private int[] bytesToInt(byte[] src, int length) {
        int value;
        int offset = 0;
        int i = 0;
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

    /**
     * 重置当前测试结果
     * @param view
     */
    public void reset(View view){
        one_mic.setBackground(getResources().getDrawable(R.drawable.light_white));
        two_mic.setBackground(getResources().getDrawable(R.drawable.light_white));
        three_mic.setBackground(getResources().getDrawable(R.drawable.light_white));
        four_mic.setBackground(getResources().getDrawable(R.drawable.light_white));
        five_mic.setBackground(getResources().getDrawable(R.drawable.light_white));
        six_mic.setBackground(getResources().getDrawable(R.drawable.light_white));
        speak_mic_1.setBackground(getResources().getDrawable(R.drawable.light_white));
        speak_mic_2.setBackground(getResources().getDrawable(R.drawable.light_white));
        test_result.setText("");
    }

    /**
     * 开始测试
     * @param view
     */
    public void startTest(View view) {
        SavePcmAudio.setSavingAudio(true);
        mMediaPlayer.start();
        test_result.setTextColor(Color.parseColor("#FFEA0C2D"));
        test_result.setText("测试进行中...请勿操作！！！");
        record_test.setClickable(false);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                SavePcmAudio.setSavingAudio(false);
                recordAnalyse();
            }
        });
    }

    /**
     * 测试成功
     * @param view
     */
    public void testSuccess(View view) {
        // 告诉工厂模式，测试结果
        Intent intentSuccess = new Intent("com.yongyida.factorytest.micTestSuccess");
        intentSuccess.putExtra("result", true);
        intentSuccess.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intentSuccess);
        finish();
    }

    /**
     * 测试失败
     * @param view
     */
    public void testFailure(View view) {
        // 告诉工厂模式，测试结果
        Intent intentSuccess = new Intent("com.yongyida.factorytest.micTestSuccess");
        intentSuccess.putExtra("result", false);
        intentSuccess.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intentSuccess);
        finish();
    }

}
