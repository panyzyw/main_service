package com.zccl.ruiqianqi.mind.service.aidl.server;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ruiqianqi on 2016/7/21 0021.
 */
public class MyUserBean implements Parcelable {
    private int id;
    private String msg;

    protected MyUserBean() {
    }

    protected MyUserBean(Parcel in) {
        id = in.readInt();
        msg = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static final Creator<MyUserBean> CREATOR = new Creator<MyUserBean>() {
        @Override
        public MyUserBean createFromParcel(Parcel in) {
            return new MyUserBean(in);
        }

        @Override
        public MyUserBean[] newArray(int size) {
            return new MyUserBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(msg);
    }
}
