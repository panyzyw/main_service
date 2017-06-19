package com.zccl.ruiqianqi.brain.handler;

import android.content.Context;

import com.zccl.ruiqianqi.brain.voice.RobotVoice;

/**
 * Created by ruiqianqi on 2017/6/5 0005.
 */

public class OtherHandler extends BaseHandler {
    public OtherHandler(Context context, RobotVoice robotVoice) {
        super(context, robotVoice);
    }

    @Override
    public boolean handlerScene(String json, int type) {
        return false;
    }

    @Override
    public boolean handleSemantic(String json, String funcType) {
        return false;
    }

    @Override
    public boolean handleAsr(String asr, int type) {
        return false;
    }

    @Override
    public boolean handlerFunc(String func) {
        return false;
    }
}
