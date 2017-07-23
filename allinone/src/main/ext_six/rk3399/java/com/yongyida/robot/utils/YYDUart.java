package com.yongyida.robot.utils;

import java.io.FileDescriptor;

/**
 * Created by ruiqianqi on 2017/7/8 0008.
 *
 * 六麦：波特率 115200；数据位 8；奇偶校验位 无；停止位 1；流控 无。
 */

public class YYDUart {
    static {
        System.loadLibrary("uart");
    }
    /**
     * 初始化交互接口, 程序启动之后，只运行一次的玩意【目的只是构造上下层沟通的桥梁】
     * @param callback
     * @param methodName
     */
    public static native void initUart(YYDUartCallback callback, String methodName);

    /**
     * 打开串口
     * @param devFd
     * @param baudrate 波特率  115200
     * @param dataBit  数据位   8
     * @param jiOuSum  奇偶校验 无【0无校验，1奇校验，2偶校验】
     * @param stopBit  停止位   1
     * @param flowCtrl  流控    无【0不使用数据流控制，1硬件，2软件】
     */
    public static native FileDescriptor openUart(String devFd, int baudrate, int dataBit, int jiOuSum, int stopBit, int flowCtrl);

    /**
     * 关闭串口
     * @param fd
     */
    public static native void closeUart(FileDescriptor fd);
}
