package com.zccl.ruiqianqi.brain.system;

interface ITtsCallback {
    void OnBegin();
    void OnPause();
    void OnResume();
    void OnComplete(String error, String tag);
}
