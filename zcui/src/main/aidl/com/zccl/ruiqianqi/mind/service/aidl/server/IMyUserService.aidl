// IMyUserService.aidl
package com.zccl.ruiqianqi.mind.service.aidl.server;

import com.zccl.ruiqianqi.mind.service.aidl.server.MyUserBean;
import com.zccl.ruiqianqi.mind.service.aidl.server.IMyUserCallback;

interface IMyUserService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);

    MyUserBean query(String name);

    String insert(String name, IMyUserCallback callBack);

    List<MyUserBean> getMyUserBeans();
}
