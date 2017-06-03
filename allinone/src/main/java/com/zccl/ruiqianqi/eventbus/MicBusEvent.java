package com.zccl.ruiqianqi.eventbus;

/**
 * Created by ruiqianqi on 2017/5/20 0020.
 */

public class MicBusEvent {

    /**
     * 打开和关闭五麦事件
     */
    public static class Operator5MicEvent{

        // 0：关闭
        public static final int CLOSE_5_MIC = 0;
        // 1：打开
        public static final int OPEN_5_MIC = 1;

        public int status = CLOSE_5_MIC;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
