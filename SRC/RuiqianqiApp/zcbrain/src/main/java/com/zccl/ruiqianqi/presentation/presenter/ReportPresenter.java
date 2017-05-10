package com.zccl.ruiqianqi.presentation.presenter;

import com.zccl.ruiqianqi.beans.ReportBean;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.StringUtils;

import static com.zccl.ruiqianqi.beans.ReportBean.CODE_TTS;

/**
 * Created by ruiqianqi on 2017/3/3 0003.
 */

public class ReportPresenter extends BasePresenter {

    /**
     * 语音播报
     * @param msg
     */
    public static void report(String msg){
        if(StringUtils.isEmpty(msg))
            return;
        // 语音处理类
        AbstractVoice voice = MindPresenter.getInstance().getVoiceDevice();
        voice.startTTS(msg, null, null);
    }

    /**
     * 其他报告
     * @param reportBean
     */
    public static void report(ReportBean reportBean){
        if(null == reportBean)
            return;
        // 语音处理类
        AbstractVoice voice = MindPresenter.getInstance().getVoiceDevice();
        if(CODE_TTS == reportBean.getCode()){
            voice.startTTS(reportBean.getMsg(), null, null);
        }
    }
}
