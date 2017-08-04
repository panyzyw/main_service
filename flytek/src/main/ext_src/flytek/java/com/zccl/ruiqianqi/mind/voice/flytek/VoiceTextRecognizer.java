package com.zccl.ruiqianqi.mind.voice.flytek;

import android.content.Context;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

/**
 * Created by ruiqianqi on 2016/9/27 0027.
 */

public final class VoiceTextRecognizer extends BaseVoice implements TextUnderstanderListener {
    /**
     * 类标志
     */
    private static String TAG = VoiceTextRecognizer.class.getSimpleName();
    /**
     * 单例引用
     */
    private static VoiceTextRecognizer instance;
    /**
     * 文本理解
     */
    private TextUnderstander textUnderstander;
    /**
     * 语义理解回调接口
     */
    private AbstractVoice.TextUnderCallback textUnderCallback;

    protected VoiceTextRecognizer(Context context) {
        super(context);
        textUnderstander = TextUnderstander.createTextUnderstander(context, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    LogUtils.e(TAG, "文本理解初始化失败, 错误码：" + code);
                } else {
                    init();
                }
            }
        });

        boolean isAiUi = Boolean.parseBoolean(MyConfigure.getValue("use_aiui"));
        if(isAiUi) {
            // 3.0是AIUI
            textUnderstander.setParameter(SpeechConstant.NLP_VERSION, "3.0");
        }else {
            textUnderstander.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        }
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static VoiceTextRecognizer getInstance(Context context) {
        if (instance == null) {
            synchronized (VoiceTextRecognizer.class) {
                VoiceTextRecognizer temp = instance;
                if (temp == null) {
                    temp = new VoiceTextRecognizer(context);
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

    }

    /**
     * 这个是开始监听
     */
    public void start(String text) {
        if (null == textUnderstander) {
            return;
        }
        cancel();

        int ret = textUnderstander.understandText(text, this);
        if (ret != 0) {
            LogUtils.e(TAG, "文本理解失败, 错误码:" + ret);
        } else {
            LogUtils.e(TAG, "开始文本理解");
        }
    }

    /**
     * 取消本次会话功能
     */
    public void cancel() {
        if (null == textUnderstander) {
            return;
        }
        // 取消本次会话功能
        textUnderstander.cancel();
    }

    /**************************************【自身参数的设置】**************************************/
    /**
     * 设置语义理解回调接口，二级回调接口
     *
     * @param textUnderCallback
     */
    public void setTextUnderCallback(AbstractVoice.TextUnderCallback textUnderCallback) {
        this.textUnderCallback = textUnderCallback;
    }

    @Override
    public void onResult(UnderstanderResult understanderResult) {
        if (null != understanderResult) {
            if (null != textUnderCallback) {
                textUnderCallback.onResult(understanderResult.getResultString());
                return;
            }
        }
        if (null != textUnderCallback) {
            textUnderCallback.onError(new Throwable("Result == null"));
        }
    }

    @Override
    public void onError(SpeechError speechError) {
        if (null != textUnderCallback) {
            textUnderCallback.onError(speechError);
        }
    }

}
