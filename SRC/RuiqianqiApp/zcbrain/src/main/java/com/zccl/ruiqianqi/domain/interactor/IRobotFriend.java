package com.zccl.ruiqianqi.domain.interactor;

/**
 * Created by ruiqianqi on 2017/3/14 0014.
 */

public interface IRobotFriend {
    // 添加的对象是机器人
    int TYPE_ROBOT = 0;
    // 添加的对象是手机
    int TYPE_PHONE = 1;

    // 同步的
    String addFriend(String id, String serial, String friendId, int type);
    // 同步的
    String removeFriend(String id, String serial, String friendId, int type);
    // 同步的
    String updateFriend(String id, String serial, String friendId, String alisa, int type);
    // 同步的
    String queryFriend(String Rid_Phone, int type);
}
