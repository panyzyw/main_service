package com.zccl.ruiqianqi.brain.semantic.flytek;

import com.google.gson.annotations.SerializedName;

import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_DISPLAY;

/**
 * Created by ruiqianqi on 2017/3/27 0027.
 */

public class DisplayBean extends BaseInfo {

    // 显示录音文本
    public static final String DISPLAY = "display_text";
    // 不显示录音文本
    public static final String NO_DISPLAY = "blank_text";

    public DisplayBean(){
        mServiceType = FUNC_DISPLAY;
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


        // 指明了显不显示录音文本
        @SerializedName("display")
        public String display;

    }
}
