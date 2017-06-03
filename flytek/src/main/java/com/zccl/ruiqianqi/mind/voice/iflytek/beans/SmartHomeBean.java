package com.zccl.ruiqianqi.mind.voice.iflytek.beans;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ruiqianqi on 2017/1/6 0006.
 */

public class SmartHomeBean extends BaseInfo {
    // 语义
    @SerializedName("semantic")
    public Semantic semantic;

    // 定义语义
    public class Semantic {
        // 没有对应的值

        // 有对应的值
        @SerializedName("slots")
        public Slots slots;
    }

    // 定义信息
    public class Slots {
        // 动作
        @SerializedName("action")
        public String action;

        // 设备
        @SerializedName("device")
        public String device;
    }
}
