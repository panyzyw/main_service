package com.zccl.ruiqianqi.brain.semantic.flytek;

/**
 * Created by ruiqianqi on 2017/7/23 0023.
 */

public class ChatBean extends BaseInfo {
    public ChatBean(){
        mSuccess = 0;
        mOperation = "ANSWER";
        mServiceType = "chat";
    }
    public Answer answer;
    public class Answer{
        public String type = "T";
        public String text;
    }
}
