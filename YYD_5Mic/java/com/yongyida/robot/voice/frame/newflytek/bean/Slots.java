package com.yongyida.robot.voice.frame.newflytek.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ruiqianqi on 2016/8/25 0025.
 */
public class Slots {
    //动作用的
    private String direct;

    //唱歌用的
    @SerializedName("default")
    private String mDefault;

    //打电话用的
    private String name;
    private String code;

    //空调操作用的
    private String action;
    private String device;

    //打开相册
    private String photo;

    //音量
    private String volume;

    public String getDirect() {
        return direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }

    public String getmDefault() {
        return mDefault;
    }

    public void setmDefault(String mDefault) {
        this.mDefault = mDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }
}
