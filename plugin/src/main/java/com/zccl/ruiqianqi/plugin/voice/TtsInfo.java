package com.zccl.ruiqianqi.plugin.voice;

/**
 * Created by ruiqianqi on 2016/11/23 0023.
 */

public class TtsInfo {
    private String text;
    private AbstractVoice.SynthesizerCallback synthesizerCallback;
    private int priority;
    private String tag;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public AbstractVoice.SynthesizerCallback getSynthesizerCallback() {
        return synthesizerCallback;
    }

    public void setSynthesizerCallback(AbstractVoice.SynthesizerCallback synthesizerCallback) {
        this.synthesizerCallback = synthesizerCallback;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
