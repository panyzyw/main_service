// ISDKCallback.aidl
package com.zccl.ruiqianqi.brain.system;

// Declare any non-default types here with import statements

interface ISDKCallback {
    void onAudio(String audio, int audioLen);
    void onReceive(int cmd, String msg);
}
