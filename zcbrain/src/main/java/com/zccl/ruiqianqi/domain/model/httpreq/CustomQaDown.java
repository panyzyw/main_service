package com.zccl.ruiqianqi.domain.model.httpreq;

/**
 * Created by ruiqianqi on 2017/4/24 0024.
 */

public class CustomQaDown {
    private int ret;
    private String err_msg;
    private String answer;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
