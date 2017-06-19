package com.zccl.ruiqianqi.brain.system;

import com.zccl.ruiqianqi.brain.system.MainBean;
import com.zccl.ruiqianqi.brain.system.IMainCallback;
import com.zccl.ruiqianqi.brain.system.ITtsCallback;

interface IMainService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    //void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    // 异步通知
    void sendCommand(int cmd, String msg, IMainCallback callback);
    // 同步结果
    MainBean sendCommandSync(int cmd, String msg);

    // 开始发音
    void startTTS(String words, String tag, ITtsCallback callback);
    // 暂停发音
    void pauseTTS();
    // 恢复发音
    void resumeTTS();
    // 停止发音，并清空队列
    void stopTTS();
    // 是否正在发音
    boolean isSpeaking();
}
