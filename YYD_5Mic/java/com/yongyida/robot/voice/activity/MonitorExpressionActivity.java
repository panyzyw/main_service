package com.yongyida.robot.voice.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yongyida.robot.voice.R;
import com.yongyida.robot.voice.data.ImageData;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.thread.MicrophoneChangeThread;
import com.yongyida.robot.voice.thread.MonitorBackgroundThread;

public class MonitorExpressionActivity extends Activity implements VoiceUnderstand.RecordController {

    private static final String TAG = "MonitorExpressionActTAG";
	
	public static boolean isExpressRun = false;

    ImageView imageViewMonitorBackground;
    ImageView imageViewVolumeExpression;
    Bitmap monitorBackground;
    Bitmap volumeExpression;

    MonitorBackgroundThread monitorBackgroundThread;
    MicrophoneChangeThread microphoneChangeThread;

    VoiceUnderstand voiceUnderstand;

    int backgroundIndex = 0;
    volatile int microphoneChangeVolume = 0;
    int microphoneCurrentIndex = 0;
    //未检测到声音
    static final int MICROPHONE_ZERO = 0;
    BitmapFactory.Options options;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
		
		isExpressRun = true;
		
        voiceUnderstand = VoiceUnderstand.getInstance(getApplicationContext());
        voiceUnderstand.setRecordController(this);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_monitor_expression);

        options = new BitmapFactory.Options();
        options.inDensity = 160;
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;

        init();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        initBitmap();
        startBgThread();
        startMicThread();
    }

    private void startMicThread() {
        if (microphoneChangeThread != null) {
            if (microphoneChangeThread.isAlive()) {
                Log.d(TAG, "麦克风动画线程执行中");
            } else {
                microphoneChangeThread = new MicrophoneChangeThread(MonitorExpressionActivity.this);
                microphoneChangeThread.setMicrophoneView(true);
                microphoneChangeThread.start();
            }
        } else {
            microphoneChangeThread = new MicrophoneChangeThread(MonitorExpressionActivity.this);
            microphoneChangeThread.setMicrophoneView(true);
            microphoneChangeThread.start();
        }
    }

    private void startBgThread() {
        if (monitorBackgroundThread != null) {
            if (monitorBackgroundThread.isAlive()) {
                Log.d(TAG, "背景动画线程执行中");
            } else {
                monitorBackgroundThread = new MonitorBackgroundThread(MonitorExpressionActivity.this);
                monitorBackgroundThread.setBackgroundView(true);
                monitorBackgroundThread.start();
            }
        } else {
            monitorBackgroundThread = new MonitorBackgroundThread(MonitorExpressionActivity.this);
            monitorBackgroundThread.setBackgroundView(true);
            monitorBackgroundThread.start();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        monitorBackgroundThread.setBackgroundView(false);
        microphoneChangeThread.setMicrophoneView(false);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
        isExpressRun = false;
        voiceUnderstand.stop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (!monitorBackground.isRecycled()) {
            monitorBackground.recycle();
            monitorBackground = null;
            System.gc();
        }
        if (!volumeExpression.isRecycled()) {
            volumeExpression.recycle();
            volumeExpression = null;
            System.gc();
        }
    }

    private void init() {
        imageViewMonitorBackground = (ImageView) findViewById(R.id.imageView_monitor_background);
        imageViewVolumeExpression = (ImageView) findViewById(R.id.imageView_volume_expression);
    }

    private void initBitmap() {
        microphoneCurrentIndex = 0;
        microphoneChangeVolume = 0;
        monitorBackground = BitmapFactory.decodeStream(getResources().openRawResource(
                R.raw.listenbg_00), null, options);
        volumeExpression = BitmapFactory.decodeStream(getResources().openRawResource(
                R.raw.mai_00), null, options);
        imageViewMonitorBackground.setImageBitmap(monitorBackground);
        imageViewVolumeExpression.setImageBitmap(volumeExpression);
    }

    public void startBackgroundAnimation(int currentIndex) {
        backgroundIndex = currentIndex;
        monitorBackground = BitmapFactory.decodeStream(getResources().openRawResource(
                ImageData.Y20_MONITOR_BACKGROUND[backgroundIndex]), null, options);
        runOnUiThread(backgroundAnimationRunnable);
    }

    private Runnable backgroundAnimationRunnable = new Runnable() {
        @Override
        public void run() {
            imageViewMonitorBackground.setImageBitmap(monitorBackground);
        }
    };

//    public void startMicrophoneAnimation(int currenIndex) {
//        microphoneIndex = currenIndex;
//        volumeExpression = BitmapFactory.decodeStream(getResources().openRawResource(
//                ImageData.Y20_MONITOR_MICROPHONE[microphoneIndex]));
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                imageViewVolumeExpression.setImageBitmap(volumeExpression);
//            }
//        });
//    }

    public void changeMicrophone() {
        if (microphoneChangeVolume == MICROPHONE_ZERO) {
            micBack();
        } else {
            micVolumeChange();
        }
        runOnUiThread(microphoneAnimationRunnble);
    }

    private void micVolumeChange() {
        if (microphoneChangeVolume >= microphoneCurrentIndex) {
            microphoneCurrentIndex = microphoneCurrentIndex + 2;
            if (microphoneCurrentIndex >= ImageData.Y20_MONITOR_MICROPHONE.length) {
                microphoneCurrentIndex = ImageData.Y20_MONITOR_MICROPHONE.length - 1;
            }
            volumeExpression = BitmapFactory.decodeStream(getResources().openRawResource(
                    ImageData.Y20_MONITOR_MICROPHONE[microphoneCurrentIndex]), null, options);
        } else {
            microphoneCurrentIndex = microphoneCurrentIndex - 2;
            if (microphoneCurrentIndex < MICROPHONE_ZERO) {
                microphoneCurrentIndex = MICROPHONE_ZERO;
            }
            volumeExpression = BitmapFactory.decodeStream(getResources().openRawResource(
                    ImageData.Y20_MONITOR_MICROPHONE[microphoneCurrentIndex]), null, options);
        }
    }

    private void micBack() {
        if (microphoneCurrentIndex != MICROPHONE_ZERO) {
            --microphoneCurrentIndex;
            volumeExpression = BitmapFactory.decodeStream(getResources().openRawResource(
                    ImageData.Y20_MONITOR_MICROPHONE[microphoneCurrentIndex]), null, options);
        }
    }

    private Runnable microphoneAnimationRunnble = new Runnable() {
        @Override
        public void run() {
            imageViewVolumeExpression.setImageBitmap(volumeExpression);
        }
    };

    @Override
    public void startBackgroundAnimation() {

    }

    @Override
    public void stopBackgroundAnimation() {

    }

    @Override
    public void startMicrophoneAnimation() {

    }

    @Override
    public void stopMicrophoneAnimation() {

    }

    @Override
    public void volumeChange(int volume) {
        this.microphoneChangeVolume = volume;
    }

    @Override
    public void quitRecord() {
        finish();
    }
}
