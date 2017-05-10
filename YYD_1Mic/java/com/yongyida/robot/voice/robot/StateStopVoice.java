package com.yongyida.robot.voice.robot;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;

/**
 * Created by Administrator on 2016/12/28 0028.
 * by dean
 */

public class StateStopVoice extends BaseMessage {
    @Override
    public void execute() {
        if (factory != null) {
            factory.setFactory(VoiceUnderstand.getInstance(context));
            factory.parseStop();
        }
    }
}
