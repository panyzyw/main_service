package com.zccl.ruiqianqi.mind.voice.impl.beans.asr;

import java.util.List;

/**
 * Created by ruiqianqi on 2016/8/10 0010.
 */
public class VoiceBean {
    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private List<Ws> ws;
    private int sc = -1;

    private transient String serialSlot;
    private transient String serialId;
    private transient String serialWord;
    private transient String json;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<Ws> getWs() {
        return ws;
    }

    public void setWs(List<Ws> ws) {
        this.ws = ws;
    }

    public int getSc() {
        return sc;
    }

    public void setSc(int sc) {
        this.sc = sc;
    }

    public String getSerialSlot() {
        return serialSlot;
    }

    public void setSerialSlot(String serialSlot) {
        this.serialSlot = serialSlot;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public String getSerialWord() {
        return serialWord;
    }

    public void setSerialWord(String serialWord) {
        this.serialWord = serialWord;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
