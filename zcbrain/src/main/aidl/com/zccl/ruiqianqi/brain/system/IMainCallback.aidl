package com.zccl.ruiqianqi.brain.system;
interface IMainCallback {
    void OnSuccess(int cmd, String msg);
    void OnFailure(int cmd, String errmsg);
}
