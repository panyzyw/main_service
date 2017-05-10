package com.yongyida.robot.voice.robot;

import android.util.Log;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;

/**
 * 循环录音.
 *
 * @author Administrator
 */
public class StateRecycle extends BaseMessage {

    @Override
    public void execute() {

        String from = intent.getExtras().getString(GeneralData.FROM);
        if (from != null) {
            Log.d("jlog", "from = " + from);
            if (!robot.getContrallState().equals(RobotStateData.STATE_CONTRALL)) {
                mainServiceInfo.setFirstRecord(false);

                Log.d("jlog", "Recycle...");
                if (factory != null) {
                    factory.setFactory(VoiceUnderstand.getInstance(context));
                    factory.parseStart(null);
                }
            }
        } else {
            Log.d("jlog", "from = " + "null");
        }
    }
}