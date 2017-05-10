package com.yongyida.robot.voice.thread;

import com.yongyida.robot.voice.activity.MonitorExpressionActivity;
import com.yongyida.robot.voice.data.ImageData;

/**
 * Created by Administrator on 2016/9/20 0013.
 */

public class MonitorBackgroundThread extends Thread {

    private MonitorExpressionActivity backgroundView;
    private boolean backgroundFlag = true;
    private int size = ImageData.Y20_MONITOR_BACKGROUND.length;
    private int currentIndex = 0;

    public MonitorBackgroundThread(MonitorExpressionActivity view) {
        backgroundView = view;
    }

    public void setBackgroundView(boolean flag) {
        backgroundFlag = flag;
    }

    @Override
    public void run() {

        while (backgroundFlag) {
            if (currentIndex < size) {
                backgroundView.startBackgroundAnimation(currentIndex);
                ++currentIndex;
            } else {
                currentIndex = 0;
            }
            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
