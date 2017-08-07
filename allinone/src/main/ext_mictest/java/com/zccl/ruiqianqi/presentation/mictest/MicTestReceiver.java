package com.zccl.ruiqianqi.presentation.mictest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ruiqianqi on 2017/6/21 0021.
 */

public class MicTestReceiver extends BroadcastReceiver {

    // 五麦测试
    public static final String FIVE_MIC_TEST = "com.yydrobot.MICTEST";
    // 五麦测试携带数据的KEY
    public static final String MIC_DATA_KEY = "data";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 五麦测试
        if(FIVE_MIC_TEST.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            if(null == bundle)
                return;
            micTest(context, bundle.getString(MIC_DATA_KEY));
        }
    }

    /**
     * 麦克测试
     */
    private void micTest(Context context, String data) {
        if("startMicTest".equals(data)){
            Intent intent = new Intent(context, MicTestActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
