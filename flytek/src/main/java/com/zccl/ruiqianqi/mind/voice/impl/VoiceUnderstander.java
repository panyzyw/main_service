package com.zccl.ruiqianqi.mind.voice.impl;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zccl.ruiqianqi.mind.voice.R;

/**
 * Created by ruiqianqi on 2016/7/20 0020.
 */
public final class VoiceUnderstander extends BaseVoice implements SpeechUnderstanderListener {

    // 类日志标志
    private static String TAG = VoiceUnderstander.class.getSimpleName();
    // 单例引用
    private static VoiceUnderstander instance;

    // 音频保存路径
    private String SAVE_AUDIO_PATH = MyConfigure.SDCARD + "msc" + File.separator;
    // 语义理解次数
    private int underCount = 0;

    // 语义理解对象（语音到语义）
    private SpeechUnderstander mSpeechUnderstander;
    /**
     * 语义理解回调接口，多接口
     */
    private Map<String, AbstractVoice.UnderstandCallback> understandCallbackMap;

    /**
     * 构造子
     * @param context
     */
    protected VoiceUnderstander(Context context) {
        super(context);
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    LogUtils.e(TAG, "语义理解初始化失败, 错误码：" + code);
                } else {
                    init();
                }
            }
        });

        // 语义理解回调接口
        understandCallbackMap = new ConcurrentHashMap<>();

    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static VoiceUnderstander getInstance(Context context) {
        if (instance == null) {
            synchronized (VoiceUnderstander.class) {
                VoiceUnderstander temp = instance;
                if (temp == null) {
                    temp = new VoiceUnderstander(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    private void init() {
        // 设置语音参数
        setUnderParams();
    }

    /**************************************【自身参数的设置】**************************************/
    /**
     * 设置语法识别回调接口【集合】，二级回调接口
     * @param key
     * @param understandCallback
     */
    public void addUnderstandCallback(String key, AbstractVoice.UnderstandCallback understandCallback) {
        understandCallbackMap.put(key, understandCallback);
    }

    /**
     * 删除对应回调接口
     * @param key
     */
    public void removeUnderstandCallback(String key) {
        if (understandCallbackMap.containsKey(key)) {
            understandCallbackMap.remove(key);
        }
    }

    /**************************************【各种参数的设置】**************************************/
    /**
     * 参数设置【语义理解没有资源路径】
     *
     * @return
     */
    private void setUnderParams() {
        if (null == mSpeechUnderstander) {
            return;
        }
        // 清空所有设置
        mSpeechUnderstander.setParameter(SpeechConstant.PARAMS, null);

        // 设置识别语言
        setLanguage(Configuration.Language);

        // 设置返回结果格式
        mSpeechUnderstander.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "10000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入，自动停止录音
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "500");
        // 设置标点符号，默认：1（有标点）
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "0");
        // 采样率
        mSpeechUnderstander.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 录音存放路径
        setSaveAudio(false);

        // 设置远场模式
        //mSpeechUnderstander.setParameter("ent", "smsfar16k");

        // 解除麦克风独占
        //mSpeechUnderstander.setParameter("domain", "fariat");
        //mSpeechUnderstander.setParameter("aue", "speex-wb;10");

        boolean isAiUi = Boolean.parseBoolean(MyConfigure.getValue("use_aiui"));
        if(isAiUi) {
            // 3.0是AIUI
            mSpeechUnderstander.setParameter(SpeechConstant.NLP_VERSION, "3.0");
        }else {
            mSpeechUnderstander.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        }
    }

    /**
     * 设置识别语言
     *
     * @param language
     */
    public void setLanguage(String language) {
        if (StringUtils.isEmpty(language))
            return;

        if (language.equals("en")) {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");

            // 这个域设置成其他值的话，就不能识别英文了
            // 服务器为不同的应用领域，定制了不同的听写匹配引擎，使用对应的领域能获取更 高的匹配率
            // { "iat", "video", "poi", "music" }
            mSpeechUnderstander.setParameter(SpeechConstant.DOMAIN, "iat");

        }
        else if (language.equals("zh")) {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

            // 解除麦克风独占，为什么要设置这个域呢
            mSpeechUnderstander.setParameter(SpeechConstant.DOMAIN, "fariat");

            mSpeechUnderstander.setParameter("aue", "speex-wb;10");

        }
        // 设置【普通话 mandarin 粤语 cantonese 河南话 henanese】
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, "mandarin");
    }

    /**
     * 设置使用录音
     */
    private void setRecorderParams() {
        // 使用录音
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_SOURCE, null);
    }

    /**
     * 设置使用外部音频源
     */
    private void setWriteAudioParams() {
        // 使用外部音频
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
    }

    /**
     * 是不是保存音频数据
     * @param saveAudio
     */
    protected void setSaveAudio(boolean saveAudio){
        if(saveAudio){
            // 录音存放路径
            mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, SAVE_AUDIO_PATH + "under" + (underCount++) + ".wav");
        }else {
            underCount = 0;
            mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, null);
        }
    }

    /************************************【提供给外部使用的方法】**********************************/
    /**
     * 这个是开始监听
     */
    public void start() {
        if (null == mSpeechUnderstander) {
            return;
        }
        stop();

        //setUnderParams();

        if (DATA_SOURCE_TYPE.TYPE_RAW_DATA == mDataSourceType) {
            setWriteAudioParams();
        } else {
            setRecorderParams();
        }

        int ret = mSpeechUnderstander.startUnderstanding(this);
        if (ret != 0) {
            LogUtils.e(TAG, "语义理解失败, 错误码:" + ret);
        } else {
            LogUtils.e(TAG, "开始理解......");
        }

        // 如果是五麦的，回调开始说话
        if(DATA_SOURCE_TYPE.TYPE_RAW_DATA == mDataSourceType){
            for (String key : understandCallbackMap.keySet()) {
                if (understandCallbackMap.get(key) != null) {
                    understandCallbackMap.get(key).onBeginOfSpeech();
                }
            }
        }
    }

    /**
     * 停止录音，然后上传
     */
    public void stop() {
        if (null == mSpeechUnderstander) {
            return;
        }
        if (mSpeechUnderstander.isUnderstanding()) {
            // 停止录音，然后上传
            mSpeechUnderstander.stopUnderstanding();
        }
    }

    /**
     * 取消本次录音会话功能
     */
    public void cancel() {
        if (null == mSpeechUnderstander) {
            return;
        }
        // 取消本次录音会话功能
        mSpeechUnderstander.cancel();
    }

    /**
     * 加载原始录音，使用的时候要先调用监听方法
     *
     * 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，
     * 云端都支持），位长16bit，单声道的wav或者pcm
     *
     * 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
     * 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别
     *
     * @param dataS
     */
    public void writeAudio(byte[] dataS) {
        if (null == mSpeechUnderstander) {
            return;
        }
        if (DATA_SOURCE_TYPE.TYPE_RAW_DATA == mDataSourceType) {
            if (mSpeechUnderstander.isUnderstanding()) {
                mSpeechUnderstander.writeAudio(dataS, 0, dataS.length);
            }
        }
    }

    /**
     * 单独加载音频资源，集成了监听与写数据
     *
     * @param buffer
     */
    /*
    public void loadAudio(byte[] buffer) {
        if (null == mSpeechUnderstander) {
            return;
        }
        stop();
        setWriteAudioParams();

        int ret = mSpeechUnderstander.startUnderstanding(this);
        if (ret != 0) {
            LogUtils.e(TAG, "语义理解失败, 错误码:" + ret);
        } else {
            if (mSpeechUnderstander.isUnderstanding()) {
                mSpeechUnderstander.writeAudio(buffer, 0, buffer.length);
            }
        }
    }
    */

    /**
     * 是不是正在监听
     * @return
     */
    public boolean isListening() {
        boolean ret = false;
        if (null != mSpeechUnderstander) {
            ret = mSpeechUnderstander.isUnderstanding();
        }
        return ret;
    }

    /***********************************【语义理解回调及二次回调】*********************************/
    @Override
    public void onBeginOfSpeech() {
        for (String key : understandCallbackMap.keySet()) {
            if (understandCallbackMap.get(key) != null) {
                understandCallbackMap.get(key).onBeginOfSpeech();
            }
        }
    }

    @Override
    public void onVolumeChanged(int volume, byte[] bytes) {
        for (String key : understandCallbackMap.keySet()) {
            if (understandCallbackMap.get(key) != null) {
                understandCallbackMap.get(key).onVolumeChanged(volume);
            }
        }
    }

    @Override
    public void onEndOfSpeech() {
        for (String key : understandCallbackMap.keySet()) {
            if (understandCallbackMap.get(key) != null) {
                understandCallbackMap.get(key).onEndOfSpeech();
            }
        }
    }

    @Override
    public void onResult(UnderstanderResult understanderResult) {
        if (null != understanderResult) {
            for (String key : understandCallbackMap.keySet()) {
                if (understandCallbackMap.get(key) != null) {
                    understandCallbackMap.get(key).onResult(understanderResult.getResultString());
                }
            }
            return;
        }
        for (String key : understandCallbackMap.keySet()) {
            if (understandCallbackMap.get(key) != null) {
                understandCallbackMap.get(key).onError(new Throwable(mContext.getString(R.string.error_result_is_null)));
            }
        }
    }

    @Override
    public void onError(SpeechError speechError) {
        for (String key : understandCallbackMap.keySet()) {
            if (understandCallbackMap.get(key) != null) {
                understandCallbackMap.get(key).onError(speechError);
            }
        }
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle bundle) {

    }

}
