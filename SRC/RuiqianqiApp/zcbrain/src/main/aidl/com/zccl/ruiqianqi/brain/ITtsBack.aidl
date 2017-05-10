package com.zccl.ruiqianqi.brain;

interface ITtsBack {
    void OnBegin();
    void OnPause();
    void OnResume();
    void OnComplete(String tag);
    void OnError(String error, String tag);
}
