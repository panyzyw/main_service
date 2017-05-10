package com.zccl.ruiqianqi.mind.voice.iflytek;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.asr.PlusResult;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.Speaker;
import com.zccl.ruiqianqi.plugin.voice.TtsInfo;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import static com.zccl.ruiqianqi.plugin.voice.Speaker.OFF_LINE_SPEAKER;
import static com.zccl.ruiqianqi.plugin.voice.Speaker.ON_LINE_SPEAKER;

/**
 * Created by ruiqianqi on 2016/7/21 0021.
 * <p>
 * 设置使用的引擎类型：在线、离线、混合。在申请了离线合成资源和权限， 或使用语记方式时，可以选择使用本地或在线的方式进行语音服务。
 * 使用在线模式（又称云端模式）时，需要使用网络，产生一定流量，但有更好的识别 或合成的效果，如更高的识别匹配度，更多的发音人等。
 * 使用离线模式（又称本地模式）时，不需要使用网络，且识别和合成的速度更快，但同时要求使用对应的离线资源或安装“语记”（安卓或iOS平台）。
 * <p>
 * 在混合模式时，可以通过:
 * 混合类型、{MIXED_TYPE: null, "realtime", "delay" }
 * 云端超时、{MIXED_TIMEOUT: 2000 [0, 30000]}
 * 本地置信门限 {MIXED_THRESHOLD: 60 [0, 100]}
 * 使用对应的策略，提高识别准确度与成功率。详情参见前面的几个参数说明。
 * <p>
 * 在离线或混合模式下，需要设置对应的【资源路径】。请参考:
 * 合成资源路径：ResourceUtil.TTS_RES_PATH；
 * 识别资源路径：ResourceUtil.ASR_RES_PATH；
 * 唤醒资源路径：ResourceUtil.IVW_RES_PATH；
 * <p>
 * 通过设置此参数【ENGINE_START|ENGINE_DESTROY】，启动离线引擎。在离线功能使用时，首次设置【资源路径】时，需要设置此参数启动引擎。
 * 关于设置的方式，参考类说明。
 * 启动引擎参数值因业务类型而异：
 * 合成：SpeechConstant.ENG_TTS
 * 识别：SpeechConstant.ENG_ASR
 * 唤醒：SpeechConstant.ENG_IVW
 * <p>
 * public static final String ENGINE_START = "engine_start";
 * public static final String ENGINE_DESTROY = "engine_destroy";
 * public static final String ASR_RES_PATH = "asr_res_path";
 * public static final String GRM_BUILD_PATH = "grm_build_path";
 * public static final String TTS_RES_PATH = "tts_res_path";
 * public static final String IVW_RES_PATH = "ivw_res_path";
 * <p>
 * static java.lang.String	ENGINE_START
 * 启动引擎 通过设置此参数，启动离线引擎。
 * static java.lang.String	ENGINE_DESTROY
 * 销毁引擎 通过调用SpeechUtility.setParameter(String, String)设置此参数 以销毁引擎，并释放资源内存。
 * static java.lang.String	ASR_RES_PATH
 * 识别资源路径 在离线识别时，首次会话，或切换识别资源时，需要设置识别资源路径。
 * static java.lang.String	GRM_BUILD_PATH
 * 语法构建目录 在使用离线语法时，需要构建语法并保存到本地，在构建和使用语法时，都需要设置语法的构 建目录。
 * static java.lang.String	IVW_RES_PATH
 * 唤醒资源路径 唤醒需要使用本地资源，通过此参数设置本地资源所在的路径。
 * static java.lang.String	TTS_RES_PATH
 * 合成资源路径 在离线合成时，首次会话，或切换合成资源时，需要设置资源路径。
 * <p>
 * ResourceUtil：资源辅助类，将资源目录，转换为SDK要求的目录形式。
 * SDK要求的文件目录格式如下（由此类的函数生成，不关心的用户可忽略），两个文件间， 以英文分号";"分隔：
 * fd|file_info|offset|length;
 * fo|file_info|offset|length;
 * 其中fd表示文件标识符，通过assets、res读取时传递，fo表示文件路径方式。
 * 注意：每次通过generateResourcePath(Context, com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE, java.lang.String)
 * 生成assets、res的文件路径后，都会生成一个文件句柄，为了使句柄释放，需要通过对应的设置资源路径函数传给SDK，
 * 用于使用或释放，以免造成内存泄露。关于资源路径设置，请参考具体的业务类说明。
 * <p>
 * SDK加载离线资源有三种方式：
 * 1，通过SpeechUtility.createUtility(Context, java.lang.String)【初始化SDK时】加载，通过设置ENGINE_START参数，并根据业务不同，指定以下一个或多个的资源路径参数：
 * TTS_RES_PATH，
 * ASR_RES_PATH，
 * IVW_RES_PATH，
 * IVW_ENROLL_RES_PATH。
 * {
 * StringBuffer param = new StringBuffer();
 * param.append(SpeechConstant.APPID+"="+getString(R.string.app_id));
 * param.append(",");
 * param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
 * }
 * <p>
 * 2，通过SpeechUtility.setParameter(String, String)加载，通过设置ENGINE_START参数，并根据业务不同，指定以下一个或多个的资源路径参数：
 * TTS_RES_PATH，
 * ASR_RES_PATH，
 * IVW_RES_PATH，
 * IVW_ENROLL_RES_PATH。
 * SpeechUtility.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
 * <p>
 * 3，通过各业务开始会话的函数加载，通过各业务的setParameter函数，如 SpeechRecognizer.setParameter(String, String)，根据业务不同， 设置以下资源路径参数中的一个：
 * TTS_RES_PATH，
 * ASR_RES_PATH，
 * IVW_RES_PATH，
 * IVW_ENROLL_RES_PATH。
 * SpeechRecognizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
 * <p>
 * 在同一业务，新的资源加载后，上一个资源将自动被释放。如合成中，如果各发音人资源文件是分开的，则在加载当前发音人资源后，
 * 上一发音人资源将自动被释放。如果要释放当前的资源， 而不加载新的资源，需要同时销毁离线引擎，有以下三种方式：
 * 1，通过SpeechUtility.setParameter(String, String)，设置参数 ENGINE_DESTROY 即可。
 * SpeechUtility.setParameter(ResourceUtil.ENGINE_DESTROY, SpeechConstant.ENG_TTS);
 * 2，通过各业务的destory函数销毁：
 * SpeechRecognizer.destroy()
 * SpeechSynthesizer.destroy()
 * VoiceWakeuper.destroy()
 * 3，通过SpeechUtility.destroy()销毁。
 * 另外，在应用退出后，所有应用相关的资源会被销毁。因为销毁引擎后，重新启动引擎的时间要比 直接更换资源长（会使首次会话的时间变长），
 * 所以如果仅是短暂的停止使用，建议不需要销毁资源。
 */
public class VoiceSynthesizer extends BaseVoice implements SynthesizerListener {

    /**
     * 类日志标志
     */
    private static String TAG = VoiceSynthesizer.class.getSimpleName();
    /**
     * 单例引用
     */
    private static VoiceSynthesizer instance;
    /**
     * 语音到文字
     */
    private SpeechSynthesizer speechSynthesizer;
    /**
     * 当前操作的语音合成实体
     */
    private TtsInfo mTtsInfo;
    // 音频保存路径
    private String SAVE_AUDIO_PATH = MyConfigure.SDCARD + "msc" + File.separator;
    // 语音播放次数
    private int ttsCount = 0;

    /**
     * 要说的文本集合
     */
    private LinkedList<TtsInfo> ttsInfoLinkedList = null;

    protected VoiceSynthesizer(Context context) {
        super(context);
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    LogUtils.e(TAG, "语音合成初始化失败, 错误码：" + code);
                } else {
                    init();
                }
            }
        });

        ttsInfoLinkedList = new LinkedList<>();

    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static VoiceSynthesizer getInstance(Context context) {
        if (instance == null) {
            synchronized (VoiceSynthesizer.class) {
                VoiceSynthesizer temp = instance;
                if (temp == null) {
                    temp = new VoiceSynthesizer(context);
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
        // 初始化语音合成参数
        initParam();
    }


    /*************************************【自身参数设置】*****************************************/
    /**
     * 设置语音合成参数
     */
    private void initParam() {
        speechSynthesizer.setParameter(SpeechConstant.PARAMS, null);

        // 在线【云端使用MSC】
        if (Configuration.E_SPEAKER_TYPE.TYPE_SPEAKER_ONLINE == Configuration.SPEAKER_TYPE) {
            // 设置使用云端引擎
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置发音人
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, Configuration.SPEAKER_NAME);

            // 设置合成语速
            speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
            // 设置合成音调
            speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
            // 设置合成音量
            speechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");

        }

        // 离线【云端使用MSC, 本地使用MSC】
        else if (Configuration.E_SPEAKER_TYPE.TYPE_SPEAKER_OFFLINE_MSC == Configuration.SPEAKER_TYPE){
            // 设置使用本地引擎
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置发音人
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, Configuration.SPEAKER_NAME);
            /**
             * 通过设置此参数，启动离线引擎。在离线功能使用时，首次设置资源路径时，需要设置此参数启动引擎。
             * 启动引擎参数值因业务类型而异：
             * 合成：SpeechConstant.ENG_TTS
             * 识别：SpeechConstant.ENG_ASR
             * 唤醒：SpeechConstant.ENG_IVW
             * 是否必须设置：是（在首次使用离线功能时）
             * 默认值：null
             */
            speechSynthesizer.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_TTS);
            // 设置发音人【资源路径】【通过generateResourcePath生成SDK要求的格式】
            speechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());

            // 设置合成语速
            speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
            // 设置合成音调
            speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
            // 设置合成音量
            speechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
        }

        // 语记【云端使用MSC, 本地优先使用语记】
        else if (Configuration.E_SPEAKER_TYPE.TYPE_SPEAKER_OFFLINE_PLUS == Configuration.SPEAKER_TYPE) {
            // 设置使用本地引擎
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置发音人【语记发音人为空，是可以通过语记APP来切换发音人的】
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, Configuration.SPEAKER_NAME);
            /**
             * 通过设置此参数，启动离线引擎。在离线功能使用时，首次设置资源路径时，需要设置此参数启动引擎。
             * 启动引擎参数值因业务类型而异：
             * 合成：SpeechConstant.ENG_TTS
             * 识别：SpeechConstant.ENG_ASR
             * 唤醒：SpeechConstant.ENG_IVW
             * 是否必须设置：是（在首次使用离线功能时）
             * 默认值：null
             */
            speechSynthesizer.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_TTS);
        }

        // 设置播放器音频流类型
        speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        speechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        speechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 语音合成路径
        setSaveAudio(false);
    }

    /**
     * 获取发音人资源路径，【通过generateResourcePath生成SDK要求的格式】
     */
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + Configuration.SPEAKER_NAME + ".jet"));
        return tempBuffer.toString();
    }

    /**
     * 是不是保存音频数据
     * @param saveAudio
     */
    protected void setSaveAudio(boolean saveAudio){
        if(saveAudio){
            // 录音存放路径
            speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, SAVE_AUDIO_PATH + "tts" + (ttsCount++) + ".wav");
        }else {
            ttsCount = 0;
            speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, null);
        }
    }

    /*************************************【设置语音合成参数】*************************************/


    /**
     * 设置新的发音人
     *
     * @param speaker
     */
    public void setTtsParams(Speaker speaker) {
        if (speaker == null)
            return;
        // 离线
        if (OFF_LINE_SPEAKER == speaker.getOffOnType()) {
            // 设置使用云端引擎
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 【设置离线MSC发音人】
            setOffLineSpeaker(speaker.getSpeakerName());
            // 【设置离线语记发音人】
            //setPlusSpeaker(null, speaker.getSpeakerIndex());
        }
        // 在线
        else if (ON_LINE_SPEAKER == speaker.getOffOnType()){
            // 设置使用云端引擎
            speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 【设置在线MSC发音人】
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, speaker.getSpeakerName());
        }
        // 设置合成语速
        speechSynthesizer.setParameter(SpeechConstant.SPEED, speaker.getSpeechRateValue());
        // 设置合成音调
        speechSynthesizer.setParameter(SpeechConstant.PITCH, speaker.getSpeechPitchValue());
        // 设置合成音量
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, speaker.getSpeechVolumeValue());
        // 设置音频流类型
        speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, speaker.getStreamTypeSelected());
    }

    /**
     * 设置发音人语言，但是一般的发音人要么只支持一种，要么都支持，好像不需要设置这个
     *
     * @param language
     */
    public void setLanguage(String language) {
        if (StringUtils.isEmpty(language))
            return;
        if (language.equals("en")) {
            // 设置语言
            speechSynthesizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else if (language.equals("zh")) {
            // 设置语言
            speechSynthesizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置【普通话 mandarin 粤语 cantonese 河南话 henanese】
            speechSynthesizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        }
    }

    /**
     * 设置语记内的发音人
     *
     * @param plusResult    语记携带的数据
     * @param index          要使用的发音人索引
     */
    public void setPlusSpeaker(PlusResult plusResult, int index) {
        if (Configuration.E_SPEAKER_TYPE.TYPE_SPEAKER_OFFLINE_PLUS == Configuration.SPEAKER_TYPE) {
            if (plusResult == null) {
                plusResult = Configuration.checkVoiceResource();
            }
            if (plusResult != null) {
                try {
                    List<PlusResult.TTS> tts = plusResult.getResult().getTts();
                    String speaker = "";
                    if (index < tts.size()) {
                        speaker = tts.get(index).getName();
                        // 设置发音人
                        LogUtils.e(TAG, "plusSpeaker: " + speaker);
                    }
                    // 空就表示用语记来设置发音人
                    speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, speaker);

                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 设置离线MSC发音人，还要加载对应的发音人资源
     *
     * @param speaker
     */
    private void setOffLineSpeaker(String speaker) {
        if (Configuration.E_SPEAKER_TYPE.TYPE_SPEAKER_OFFLINE_MSC == Configuration.SPEAKER_TYPE) {
            StringBuffer tempBuffer = new StringBuffer();
            tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
            tempBuffer.append(";");
            tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + speaker + ".jet"));
            // 设置发音人
            speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, speaker);
            // 设置发音人【资源路径】【通过generateResourcePath生成SDK要求的格式】
            speechSynthesizer.setParameter(ResourceUtil.TTS_RES_PATH, tempBuffer.toString());
            LogUtils.e(TAG, "offLineSpeaker: " + speaker);
        }
    }

    /************************************【提供给外部使用的方法】**********************************/
    /**
     * 开始语音合成
     *
     * @param words         合成的文字
     * @param tag           携带的TAG
     * @param synthesizerCallback  回调的接口
     */
    public void start(String words, String tag, AbstractVoice.SynthesizerCallback synthesizerCallback) {
        LogUtils.e(TAG, "startTTS: " + words);

        if (speechSynthesizer != null) {
            if (StringUtils.isEmpty(words)) {
                if (!ttsInfoLinkedList.isEmpty()) {
                    mTtsInfo = ttsInfoLinkedList.poll();
                    if (mTtsInfo != null) {
                        speechSynthesizer.stopSpeaking();
                        speechSynthesizer.startSpeaking(mTtsInfo.getText(), this);
                    }
                }
                return;

            } else {
                TtsInfo ttsInfo = new TtsInfo();
                ttsInfo.setText(words);
                ttsInfo.setTag(tag);
                ttsInfo.setSynthesizerCallback(synthesizerCallback);
                ttsInfoLinkedList.offer(ttsInfo);
                if (speechSynthesizer.isSpeaking()) {

                } else {
                    mTtsInfo = ttsInfoLinkedList.poll();
                    if (mTtsInfo != null) {
                        speechSynthesizer.stopSpeaking();
                        speechSynthesizer.startSpeaking(mTtsInfo.getText(), this);
                    }
                }
            }
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (speechSynthesizer != null) {
            speechSynthesizer.pauseSpeaking();
        }
    }

    /**
     * 恢复播放
     */
    public void resume() {
        if (speechSynthesizer != null) {
            speechSynthesizer.resumeSpeaking();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (speechSynthesizer != null) {
            speechSynthesizer.stopSpeaking();
        }
        if (ttsInfoLinkedList != null) {
            ttsInfoLinkedList.clear();
        }
    }

    /**
     * 返回是否正在播音
     * @return
     */
    public boolean isSpeaking(){
        return ttsInfoLinkedList.size() > 0;
    }

    /************************************【语音合成回调接口】**************************************/
    /**
     * 开始说话
     */
    @Override
    public void onSpeakBegin() {
        if (mTtsInfo != null && mTtsInfo.getSynthesizerCallback() != null) {
            mTtsInfo.getSynthesizerCallback().OnBegin();
        }
    }

    /**
     * 这个是字符转音频的情况
     * 合成进度
     *
     * @param percent
     * @param beginPos
     * @param endPos
     * @param info
     */
    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
    }

    /**
     * 说话暂停
     */
    @Override
    public void onSpeakPaused() {
        if (mTtsInfo != null && mTtsInfo.getSynthesizerCallback() != null) {
            mTtsInfo.getSynthesizerCallback().OnPause();
        }
    }

    /**
     * 说话恢复
     */
    @Override
    public void onSpeakResumed() {
        if (mTtsInfo != null && mTtsInfo.getSynthesizerCallback() != null) {
            mTtsInfo.getSynthesizerCallback().OnResume();
        }
    }

    /**
     * 播放进度
     */
    @Override
    public void onSpeakProgress(int i, int i1, int i2) {
    }

    /**
     * 说话完成
     * @param speechError
     */
    @Override
    public void onCompleted(SpeechError speechError) {
        LogUtils.e(TAG, "onCompleted");
        if (mTtsInfo != null && mTtsInfo.getSynthesizerCallback() != null) {
            if (speechError == null) {
                mTtsInfo.getSynthesizerCallback().OnComplete(null, mTtsInfo.getTag());
            } else {
                mTtsInfo.getSynthesizerCallback().OnComplete(new Throwable(speechError.getPlainDescription(true)), mTtsInfo.getTag());
            }
        }

        if (!ttsInfoLinkedList.isEmpty()) {
            start(null, null, null);
        } else {
            // 不能在这儿把这个置为空，如果是连着说话，在上一句的回调里，说下一句话。
            // 在OnComplete调用start()之后，集合就空了，紧接着就执行了mTtsInfo = null;
            // mTtsInfo也指向了集合中最后一个对象，而此时，在这儿把对象置空，就没法回调了，就报空指针了
            // mTtsInfo = null;
        }

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }

}
