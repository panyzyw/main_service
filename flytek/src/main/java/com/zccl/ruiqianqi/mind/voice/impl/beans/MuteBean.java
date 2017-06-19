package com.zccl.ruiqianqi.mind.voice.impl.beans;

import com.google.gson.annotations.SerializedName;

import static com.zccl.ruiqianqi.mind.voice.impl.function.FuncType.FUNC_SWITCH;

/**
 * Created by ruiqianqi on 2017/3/27 0027.
 */

public class MuteBean extends BaseInfo {
    public MuteBean(){
        mServiceType = FUNC_SWITCH;
    }

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

    // 定义服务器版本
    public class Slots {

        // 安静
        @SerializedName("cmd")
        public String cmd;

    }
}
