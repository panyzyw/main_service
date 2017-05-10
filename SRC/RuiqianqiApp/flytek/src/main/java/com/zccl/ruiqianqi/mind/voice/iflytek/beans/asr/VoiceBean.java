package com.zccl.ruiqianqi.mind.voice.iflytek.beans.asr;

import com.zccl.ruiqianqi.mind.voice.iflytek.beans.asr.Ws;

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

    private String serialSlot;
    private String serialId;
    private String serialWord;

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
}
