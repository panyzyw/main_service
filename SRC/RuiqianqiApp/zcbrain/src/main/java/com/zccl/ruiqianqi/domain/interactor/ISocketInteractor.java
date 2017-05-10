package com.zccl.ruiqianqi.domain.interactor;

import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.domain.model.ServerAddr;

/**
 * Created by ruiqianqi on 2017/1/16 0016.
 */

public interface ISocketInteractor {


    enum CONN_TYPE{
        // 初始化连接
        INIT_CONNECT,
        // 切换服务器
        SWITCH_CONNECT
    }
    /**
     * 查询对应版本的服务器IP信息
     * @param flagVersion   版本
     * @param connType 初始化连接、切换连接
     */
    void switchToServer(String flagVersion, CONN_TYPE connType);

    /**
     * 设置切换服务器时回调 P 的接口
     * @param switchCallback2P
     */
    void setSwitchCallback2P(SwitchCallback2P switchCallback2P);

    /**
     * 回调接口
     */
    interface SwitchCallback2P{
        void OnSwitchSuccess(String flagVersion, ServerAddr serverAddr);
        void OnSwitchFailure(Throwable error);
    }

    /**
     * 开始登录
     */
    void login();

    /**
     * 发一个心跳包
     */
    void heartBeat();

    /**
     * 向服务器更新机器人电量
     * @param battery
     */
    void flushBattery(String battery);

    /**
     * 向服务器更新机器人名字
     * @param robotName
     */
    void flushRobotName(String robotName);

    /**
     * 把收到的数据往服务器进行转发
     * @param forwardSocketEvent
     */
    void forwardToServer(MindBusEvent.ForwardSocketEvent forwardSocketEvent);

}
