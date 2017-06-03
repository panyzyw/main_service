package com.yongyida.robot.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Notice implements Parcelable {
    public static final Creator<Notice> CREATOR = new Creator<Notice>() {

        @Override
        public Notice createFromParcel(Parcel source) {
            return new Notice(source);
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };
    String id;
    String title;
    String content;
    String time;
    String seq;

    public Notice(String id, String title, String content, String time, String seq) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.time = time;
        this.seq = seq;
    }

    public Notice(String title, String content, String time, String seq) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.seq = seq;
    }

    public Notice(Parcel source) {
        id = source.readString();
        title = source.readString();
        content = source.readString();
        time = source.readString();
        seq = source.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return "Notice [title=" + title + ", content=" + content + ", time=" + time + ", seq=" + seq + "]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(time);
        dest.writeString(seq);
    }
} 
