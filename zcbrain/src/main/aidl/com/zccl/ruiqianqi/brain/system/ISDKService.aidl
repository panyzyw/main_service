// ISDKService.aidl
package com.zccl.ruiqianqi.brain.system;

import com.zccl.ruiqianqi.brain.system.ISDKCallback;
// Declare any non-default types here with import statements

interface ISDKService {
    // 异步通知
    void setCallback(ISDKCallback callback);
}
