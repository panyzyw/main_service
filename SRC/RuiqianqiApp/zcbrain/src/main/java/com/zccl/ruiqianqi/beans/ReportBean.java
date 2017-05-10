package com.zccl.ruiqianqi.beans;

/**
 * Created by ruiqianqi on 2017/3/21 0021.
 */

public class ReportBean {
    // 发音
    public static final int CODE_TTS = 1;

    // 功能
    private int code;
    // 参数
    private String msg;
    // 类型
    private String type;

    public static ReportBean obtain(int code, String msg){
        ReportBean reportBean = new ReportBean();
        reportBean.setCode(code);
        reportBean.setMsg(msg);
        return reportBean;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
