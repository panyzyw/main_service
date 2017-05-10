package com.yongyida.robot.voice.thread;

import com.yongyida.robot.voice.activity.MonitorExpressionActivity;
import com.yongyida.robot.voice.data.ImageData;

/**
 * Created by Administrator on 2016/9/20 0020.
 */

public class MicrophoneChangeThread extends Thread {

    private MonitorExpressionActivity backgroundView;
    private boolean microphoneFlag = true;
//    private int size = ImageData.Y20_MONITOR_MICROPHONE.length;
//    private int currentIndex = 0;

    //测试开关，0为减，1为加
//    private int testSwitch = 1;

    public MicrophoneChangeThread(MonitorExpressionActivity view) {
        this.backgroundView = view;
//        testSwitch = 1;
    }

    public void setMicrophoneView(boolean flag) {
        this.microphoneFlag = flag;
    }

    @Override
    public void run() {
        while (microphoneFlag) {
//            switch (testSwitch) {
//                //声音减
//                case 0:
//                    if (currentIndex >= 0) {
//                        backgroundView.startMicrophoneAnimation(currentIndex);
//                        --currentIndex;
////                        currentIndex = currentIndex - 2;
//                    } else {
//                        testSwitch = 1;
//                        ++currentIndex;
////                        currentIndex = currentIndex +2;
//                    }
//                    break;
//                //声音加
//                case 1:
//                    if (currentIndex < size) {
//                        backgroundView.startMicrophoneAnimation(currentIndex);
////                        ++currentIndex;
//                        currentIndex = currentIndex + 3;
//                    } else {
//                        testSwitch = 0;
////                        --currentIndex;
//                        currentIndex = currentIndex - 3;
//                    }
//                    break;
//            }
            backgroundView.changeMicrophone();
            try {
                sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
