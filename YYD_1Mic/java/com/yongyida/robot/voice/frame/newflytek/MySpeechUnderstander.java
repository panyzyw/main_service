package com.yongyida.robot.voice.frame.newflytek;

import android.content.Context;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;

/**
 * Created by ruiqianqi on 2016/8/25 0025.
 */
public class MySpeechUnderstander extends SpeechUnderstander{

    /** 语法处理 */
    private VoiceRecognizer voiceRecognizer;
    /** 语法匹配 */
    private SpeechRecognizer speechRecognizer;
    /** 对象单例 */
    private static MySpeechUnderstander instance = null;

    private MySpeechUnderstander(Context context, InitListener initListener){
        super(context, initListener);
        speechRecognizer = SpeechRecognizer.createRecognizer(context, initListener);
        voiceRecognizer = new VoiceRecognizer(context, speechRecognizer);
    }

    public static synchronized MySpeechUnderstander createUnderstander(Context context, InitListener initListener) {
        if(instance == null) {
            instance = new MySpeechUnderstander(context, initListener);
        }
        return instance;
    }

    @Override
    public int startUnderstanding(final SpeechUnderstanderListener speechUnderstanderListener) {
        voiceRecognizer.setUnderstanderListener(speechUnderstanderListener);
        return voiceRecognizer.start();
    }

    @Override
    public void stopUnderstanding() {
        speechRecognizer.stopListening();
    }

    @Override
    public int writeAudio(byte[] bytes, int i, int i1) {
        return speechRecognizer.writeAudio(bytes, i, i1);
    }

    @Override
    public boolean setParameter(String s, String s1) {
        return speechRecognizer.setParameter(s, s1);
    }

    @Override
    public String getParameter(String s) {
        return speechRecognizer.getParameter(s);
    }

    @Override
    public boolean isUnderstanding() {
        return speechRecognizer.isListening();
    }

    @Override
    public void cancel() {
        speechRecognizer.cancel();
    }

    @Override
    public boolean destroy() {
        return speechRecognizer.destroy();
    }

}
