package com.zccl.ruiqianqi.mind.voice.iflytek.beans;

import com.google.gson.annotations.SerializedName;

import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_DISPLAY;
import static com.zccl.ruiqianqi.mind.voice.iflytek.function.FuncType.FUNC_SWITCH;

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
