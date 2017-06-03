package com.zccl.ruiqianqi.tools.beans;

/**
 * Created by ruiqianqi on 2016/8/15 0015.
 */
public class RxBaseResult<T> {

    /** 失败 */
    public static final int FAILURE = 0;
    /** 成功 */
    public static final int SUCCESS = 1;

    /**
     * 返回状态：
     * 0 失败
     * 1 成功
     */
    private int code;
    /** 返回信息 */
    private String msg;
    /** 包装的对象 */
    private T data;

    public boolean success() {
        return code==SUCCESS;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResult{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

}
