package com.zccl.ruiqianqi.tools.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zc on 2016/4/5.
 * 供系统自带的下载类【DownloadManager】使用
 */
public class MyDownItem implements Parcelable {
    // 下载ID
    private long id;
    // 下载路径
    private String url;
    // 保存路径
    private String path;

    public MyDownItem() {
    }

    protected MyDownItem(Parcel in) {
        id = in.readLong();
        url = in.readString();
        path = in.readString();
    }

    public static final Creator<MyDownItem> CREATOR = new Creator<MyDownItem>() {
        @Override
        public MyDownItem createFromParcel(Parcel in) {
            return new MyDownItem(in);
        }

        @Override
        public MyDownItem[] newArray(int size) {
            return new MyDownItem[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(url);
        dest.writeString(path);
    }
}
