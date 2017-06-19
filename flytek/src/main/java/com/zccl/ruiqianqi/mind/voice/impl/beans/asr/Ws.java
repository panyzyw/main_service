package com.zccl.ruiqianqi.mind.voice.impl.beans.asr;

import java.util.List;

/**
 * Created by ruiqianqi on 2016/8/10 0010.
 */
public class Ws {
    private int bg;
    private String slot;
    private List<Cw> cw;

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public List<Cw> getCw() {
        return cw;
    }

    public void setCw(List<Cw> cw) {
        this.cw = cw;
    }
}
