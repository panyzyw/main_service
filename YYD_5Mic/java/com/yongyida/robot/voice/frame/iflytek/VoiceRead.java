package com.yongyida.robot.voice.frame.iflytek;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.yongyida.robot.voice.data.UrlData;

/**
 * 将文字转换成语音.
 *
 * @author Administrator
 */
public class VoiceRead extends CommVoiceParse {
    private static VoiceRead mReadSpeech;

    //获取对象
    private SpeechSynthesizer mSpeechSynthesizer;

    public VoiceRead(Context context) {
        this(context, SpeechConstant.TYPE_CLOUD, "aisxa", "50", "50", "100", "3", "true", "wav", UrlData.READ_PATH);

    }

    public VoiceRead(Context context, String engineType, String voicer, String speed, String tone, String volume, String type, String interpt, String format, String path) {
        this.mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context, new InitListener() {

            @Override
            public void onInit(int arg0) {

            }
        });


        setParam(engineType, voicer, speed, tone, volume, type, interpt, format, path);

    }


    @Override
    public void start() {

        if (mSpeechSynthesizer == null) {
            return;
        }
        if (words == null) {
            return;
        }

        if (mSpeechSynthesizer.isSpeaking()) {
            mSpeechSynthesizer.stopSpeaking();
        }

        mSpeechSynthesizer.startSpeaking(words, new SynthesizerListener() {

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int arg0, int arg1, int arg2) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakBegin() {


            }

            @Override
            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

            }

            @Override
            public void onCompleted(SpeechError arg0) {

            }

            @Override
            public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {

            }
        });

    }

    @Override
    public void stop() {
        if (mSpeechSynthesizer == null) {
            return;
        }
        if (mSpeechSynthesizer.isSpeaking()) {
            mSpeechSynthesizer.stopSpeaking();
        }
    }

    /**
     * 参数设置.
     *
     * @param engineType
     * @param voicer
     * @param speed
     * @param tone
     * @param volume
     * @param type
     * @param interpt
     * @param format
     * @param path
     */
    private void setParam(String engineType, String voicer, String speed, String tone, String volume, String type, String interpt, String format, String path) {
        mSpeechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        //mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_SOURCE, AudioSource.VOICE_COMMUNICATION+"");
        if (engineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voicer);
        } else {
            mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "");
        }
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, speed);
        mSpeechSynthesizer.setParameter(SpeechConstant.PITCH, tone);
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, volume);
        mSpeechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, type);

        mSpeechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, interpt);

        mSpeechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, format);
        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, path);

    }


    public static VoiceRead getInstence(Context context) {
        if (mReadSpeech == null) {
            synchronized (VoiceRead.class) {
                if (mReadSpeech == null) {
                    mReadSpeech = new VoiceRead(context);
                }
            }

        }

        return mReadSpeech;
    }


}
