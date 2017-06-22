package com.zccl.ruiqianqi.brain.semantic.flytek;

import com.google.gson.annotations.SerializedName;

import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_GENERIC;

/**
 * Created by ruiqianqi on 2017/5/26 0026.
 */

public class GenericBean extends BaseInfo {
    public GenericBean(){
        mServiceType = FUNC_GENERIC;
    }

    // 语义
    @SerializedName("semantic")
    public ExpressionBean.Semantic semantic;

    // 定义语义
    public class Semantic {
        // 有对应的值
        @SerializedName("slots")
        public ExpressionBean.Slots slots;

    }

    // 定义服务器的返回
    public class Slots {
        // 要执行的通用动作
        @SerializedName("action")
        public String action;

    }
}
