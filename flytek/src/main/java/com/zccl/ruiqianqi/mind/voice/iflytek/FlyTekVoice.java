package com.zccl.ruiqianqi.mind.voice.iflytek;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.ProxyVoice;
import com.zccl.ruiqianqi.plugin.voice.Speaker;
import com.zccl.ruiqianqi.tools.StringUtils;

/**
 * Created by ruiqianqi on 2016/9/29 0029.
 *
 * 本地识别只支持16K采样率、位长16bit、单音轨、支持wav或者pcm
 * 云端支持8K、16K采样率、位长16bit、单音轨、支持wav或者pcm
 */
public class FlyTekVoice extends AbstractVoice {

    // 没有音频数据录入
    public static final int NO_VOICE = 10118;
    // 网络连接发生异常
    public static final int NO_NET = 10212;

    /** 全局上下文 */
    protected Context mContext;
    // 主线程的Handler
    protected Handler mHandler;
    // 语音唤醒
    protected VoiceWakeUp voiceWakeUp;
    // 语音识别
    protected VoiceRecognizer voiceRecognizer;
    // 语义理解
    protected VoiceUnderstander voiceUnderstander;
    // 文字转语音
    protected VoiceSynthesizer voiceSynthesizer;

    public FlyTekVoice(Context context){
        this.mContext = context.getApplicationContext();
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 初始化语音服务，在Application的OnCreate()中调用
     */
    @Override
    public void initSpeech(){
        Configuration.initSpeech(mContext);

        voiceWakeUp = VoiceWakeUp.getInstance(mContext);
        voiceUnderstander = VoiceUnderstander.getInstance(mContext);
        voiceSynthesizer = VoiceSynthesizer.getInstance(mContext);
        voiceRecognizer = VoiceRecognizer.getInstance(mContext);
    }

    /************************************【其他功能接口】******************************************/
    /**
     * 切换语言
     * @param language
     */
    @Override
    public void switchLanguage(String language) {
        Configuration.Language = language;

        voiceUnderstander.setLanguage(language);
        voiceSynthesizer.setLanguage(language);
        voiceRecognizer.setLanguage(language);
    }

    /**
     * 设置是否循环监听
     * @param recyclerListen
     */
    @Override
    public void setRecyclerListen(boolean recyclerListen){

    }

    /**
     * 设置新的发音人
     * @param speaker
     */
    @Override
    public void setTtsParams(Speaker speaker){
        voiceSynthesizer.setTtsParams(speaker);
    }

    /************************************【唤醒相关接口】******************************************/
    /**
     * 设置唤醒回调接口
     * @param wakeupCallback
     */
    @Override
    public void setWakeupCallback(WakeupCallback wakeupCallback) {
        voiceWakeUp.setWakeupCallback(wakeupCallback);
    }

    /**
     * 开始唤醒监听
     */
    @Override
    public void startWakeup() {
        voiceWakeUp.startWakeUp();
    }

    /**
     * 停止唤醒监听
     */
    @Override
    public void stopWakeup() {
        voiceWakeUp.stopWakeUp();
    }

    /**
     * 重启唤醒监听
     * 返回值：是否需要检测，并重启唤醒引擎
     */
    @Override
    public boolean reboot() {
        return VoiceWakeUp.reboot(mContext);
    }

    /**
     * 得到看门狗的值
     * @return
     */
    @Override
    public boolean isReboot() {
        return voiceWakeUp.isReboot();
    }

    /**
     * 设置看门狗的值
     * @param reboot
     */
    @Override
    public void setReboot(boolean reboot) {
        voiceWakeUp.setReboot(reboot);
    }

    /************************************【语义理解相关接口】**************************************/
    /**
     * 设置语义理解回调接口【集合】
     * @param key
     * @param understandCallback
     */
    @Override
    public void addUnderstandCallback(String key, UnderstandCallback understandCallback) {
        voiceUnderstander.addUnderstandCallback(key, understandCallback);
    }

    /**
     * 删除对应理解回调接口
     * @param key
     */
    @Override
    public void removeUnderstandCallback(String key) {
        voiceUnderstander.removeUnderstandCallback(key);
    }

    /**
     * 开始语义理解
     */
    @Override
    public void startUnderstand() {
        voiceUnderstander.start();
    }

    /**
     * 取消语义理解
     */
    @Override
    public void stopUnderstand() {
        voiceUnderstander.stop();
    }

    /**
     * 取消语义理解
     */
    @Override
    public void cancelUnderstand() {
        voiceUnderstander.stop();
        voiceUnderstander.cancel();
    }


    /**
     * 直接加载语义理解语音数据
     * @param dataS
     */
    @Override
    public void writeUnderstand(byte[] dataS) {
        voiceUnderstander.writeAudio(dataS);
    }

    /*********************************【语法识别相关接口】*****************************************/
    /**
     * 设置语法识别回调接口【集合】
     * @param key
     * @param recognizerCallback
     */
    @Override
    public void addRecognizerCallback(String key, ProxyVoice.RecognizerCallback recognizerCallback) {
        voiceRecognizer.addRecognizerCallback(key, recognizerCallback);
    }

    /**
     * 删除对应识别回调接口
     * @param key
     */
    @Override
    public void removeRecognizerCallback(String key) {
        voiceRecognizer.removeRecognizerCallback(key);
    }

    /**
     * 开始语法识别
     */
    @Override
    public void startRecognizer() {
        voiceRecognizer.start(Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_MSC);
    }

    @Override
    public void startRecognizer(int recFlag) {
        if (recFlag == 0) {
            voiceRecognizer.start(Configuration.E_ASR_TYPE.TYPE_ASR_LISTEN);
        } else if (recFlag == 1) {
            voiceRecognizer.start(Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE);
        } else if (recFlag == 2) {
            voiceRecognizer.start(Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_MSC);
        }else if (recFlag == 3) {
            voiceRecognizer.start(Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_PLUS);
        }
    }

    /**
     * 取消语法识别
     */
    @Override
    public void stopRecognizer() {
        voiceRecognizer.stop();
    }

    /**
     * 取消语法识别
     */
    @Override
    public void cancelRecognizer() {
        voiceRecognizer.cancel();
    }

    /**
     * 直接加载语法识别语音数据
     * @param dataS
     */
    @Override
    public void writeRecognizer(byte[] dataS) {
        voiceRecognizer.writeAudio(dataS);
    }

    /***********************************【文字理解相关接口】***************************************/
    /**
     * 设置文本识别回调接口
     * @param textUnderCallback
     */
    @Override
    public void setTextUnderCallback(TextUnderCallback textUnderCallback) {
        VoiceTextRecognizer.getInstance(mContext).setTextUnderCallback(textUnderCallback);
    }

    /**
     * 直接用文本和语音服务器交互
     * @param text
     */
    public void startText(String text){
        VoiceTextRecognizer.getInstance(mContext).start(text);
    }

    /************************************【语音合成相关接口】**************************************/
    /**
     * 开始语音合成
     * @param words
     * @param tag
     * @param synthesizerCallback
     */
    @Override
    public void startTTS(String words, String tag, SynthesizerCallback synthesizerCallback) {
        voiceSynthesizer.start(words, tag, synthesizerCallback);
    }

    /**
     * 开始语音合成
     * @param words  -------------------- 发音要读的文字
     * @param runnable
     */
    @Override
    public void startTTS(String words, final Runnable runnable) {
        voiceSynthesizer.start(words, null, new SynthesizerCallback() {
            @Override
            public void OnBegin() {

            }

            @Override
            public void OnPause() {

            }

            @Override
            public void OnResume() {

            }

            @Override
            public void OnComplete(Throwable throwable, String tag) {
                if(null != runnable){
                    runnable.run();
                }
            }
        });
    }

    /**
     * 暂停播放
     */
    @Override
    public void pauseTTS() {
        voiceSynthesizer.pause();
    }

    /**
     * 恢复播放
     */
    @Override
    public void resumeTTS() {
        voiceSynthesizer.resume();
    }

    /**
     * 停止播放
     */
    @Override
    public void stopTTS() {
        voiceSynthesizer.stop();
    }

    /**
     * 返回是否正在播音
     * @return
     */
    @Override
    public boolean isSpeaking() {
        return voiceSynthesizer.isSpeaking();
    }

/***********************************【脚本相关功能接口】***************************************/
    /**
     * 更新语法规则            【科大讯飞使用】【包括动态更新联系人】
     * @param rule             规则名
     * @param ruleValue       规则值
     */
    @Override
    public void updateRule(String rule, String ruleValue){
        if(!StringUtils.isEmpty(rule)) {

            if (!StringUtils.isEmpty(ruleValue)) {
                if(rule.equals("contact")) {
                    // 直接传入联系人进行更新
                    voiceRecognizer.updateContact(ruleValue);
                }else {
                    // 更新其他的语法规则
                    voiceRecognizer.updateMyselfWords(rule, ruleValue);
                }
            } else {
                if(rule.equals("contact")) {
                    // 用科大迅飞提供的方法进行更新
                    voiceRecognizer.updateContact();
                }else {
                    // 值都为空了，还更新个毛线
                }
            }
        }
    }

    /**
     * 动态加载，【在线、离线】命令词 【科大讯飞使用】
     * @param loadScriptType
     * {@link E_LOAD_SCRIPT_TYPE#ONLINE_ASSETS}:  从【assets】加载在线命令词
     * {@link E_LOAD_SCRIPT_TYPE#OFFLINE_ASSETS}：从【assets】加载离线命令词
     * {@link E_LOAD_SCRIPT_TYPE#ONLINE_ABSOLUTE}:  从【绝对路径】加载在线命令词
     * {@link E_LOAD_SCRIPT_TYPE#OFFLINE_ABSOLUTE}：从【绝对路径】加载离线命令词
     *
     * @param grammarPath     创建脚本路径
     * @param grammarDir    语法文件的生成解析路径
     */
    @Override
    public void loadCmdWords(E_LOAD_SCRIPT_TYPE loadScriptType, String grammarPath, String grammarDir) {

        if(E_LOAD_SCRIPT_TYPE.ONLINE_ASSETS == loadScriptType){
            voiceRecognizer.loadOnlineGrammar(grammarPath, MyConfigure.ONE_ASSETS);

        }else if(E_LOAD_SCRIPT_TYPE.OFFLINE_ASSETS == loadScriptType){
            voiceRecognizer.loadOfflineGrammar(grammarPath, MyConfigure.ONE_ASSETS, grammarDir);

        }else if(E_LOAD_SCRIPT_TYPE.ONLINE_ABSOLUTE == loadScriptType){
            voiceRecognizer.loadOnlineGrammar(grammarPath, MyConfigure.SIX_ABSOLUTE);

        }else if(E_LOAD_SCRIPT_TYPE.OFFLINE_ABSOLUTE == loadScriptType){
            voiceRecognizer.loadOfflineGrammar(grammarPath, MyConfigure.SIX_ABSOLUTE, grammarDir);

        }
    }

    /********************************【系统状态变化时的通知接口】**********************************/
    /**
     * 通知什么改变了
     * @param flag
     * @param obj
     */
    @Override
    public void notifyChange(int flag, Object obj){

    }

}
