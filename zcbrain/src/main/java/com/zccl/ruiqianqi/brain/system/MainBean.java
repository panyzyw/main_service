package com.zccl.ruiqianqi.brain.system;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ruiqianqi on 2017/6/12 0012.
 */

public class MainBean implements Parcelable {
    private int cmd;
    private String msg;

    public MainBean() {
    }

    public MainBean(Parcel in) {
        cmd = in.readInt();
        msg = in.readString();
    }

    public static final Creator<MainBean> CREATOR = new Creator<MainBean>() {
        @Override
        public MainBean createFromParcel(Parcel in) {
            return new MainBean(in);
        }

        @Override
        public MainBean[] newArray(int size) {
            return new MainBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cmd);
        dest.writeString(msg);
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
