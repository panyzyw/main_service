package com.zccl.ruiqianqi.brain.semantic.flytek;

import com.google.gson.annotations.SerializedName;


/**
 * Created by ruiqianqi on 2017/3/27 0027.
 */

public class MuteBean extends BaseInfo {

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
