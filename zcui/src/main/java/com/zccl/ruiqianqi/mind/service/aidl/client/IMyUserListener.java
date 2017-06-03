package com.zccl.ruiqianqi.mind.service.aidl.client;

/**
 * Created by ruiqianqi on 2016/7/21 0021.
 */
public interface IMyUserListener {
    void OnSuccess(String msg);
    void OnFailure(String errmsg);
}
