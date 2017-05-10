// IMyUserCallback.aidl
package com.zccl.ruiqianqi.mind.service.aidl.server;
interface IMyUserCallback {
    void OnSuccess(String msg);
    void OnFailure(String errmsg);
}
