package com.yongyida.robot.voice.frame.iflytek;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.utils.FileUtil;

/**
 * Created by Administrator on 2016/7/30 0030.
 *
 * Author 陈赫
 */
public class VoiceRecognizer extends CommVoiceParse {

    private static VoiceRecognizer recognizer;
    private SpeechRecognizer speechRecognizer;
    private String lexicon;

    private VoiceRecognizer(Context context) {

        this.context = context;

        lexicon = FileUtil.readFile(context, "userwords.txt", "utf-8");

        Log.d("jlog", lexicon);

        speechRecognizer = SpeechRecognizer.createRecognizer(context, new InitListener() {
            @Override
            public void onInit(int i) {
                if (i != ErrorCode.SUCCESS) {
                    Log.e("jlog", "初始化失败，错误码：" + i);
                }
            }
        });

        setParam(GeneralData.LANGUAGE_TEXT_ENCODING);
    }

    private void setParam(String languageTextEncoding) {
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, languageTextEncoding);
    }

    @Override
    public void start() {

        Log.d("jlog", "开始上传词表");
		
		if (speechRecognizer == null) {
            Log.e("jlog", "speechRecognizer == null");
            return;
        }
        if (lexicon == null) {
            Log.e("jlog", "lexicon == null");
            return;
        }

        int ret = speechRecognizer.updateLexicon("userword", lexicon, new LexiconListener() {
            @Override
            public void onLexiconUpdated(String s, SpeechError speechError) {
                if (speechError != null) {
                    Log.e("jlog", speechError.toString());
                }
            }
        });
        if (ret != ErrorCode.SUCCESS) {
            Log.e("jlog", "上传热词失败,错误码：" + ret);
        } else {
            Log.d("jlog", "词表上传成功");
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    public static VoiceRecognizer getInstance(Context context) {
        Log.d("jlog", "得到听写实例");

        if (recognizer == null) {
            recognizer = new VoiceRecognizer(context);
        }

        return recognizer;
    }
}
