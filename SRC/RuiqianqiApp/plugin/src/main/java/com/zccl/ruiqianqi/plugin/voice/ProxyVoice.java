package com.zccl.ruiqianqi.plugin.voice;

/**
 * Created by ruiqianqi on 2016/9/28 0028.
 */

public class ProxyVoice extends AbstractVoice{

    /** 音频处理类 */
    protected AbstractVoice realVoice;

    /**
     * 构造子，传入真正的实体对象
     * @param realVoice
     */
    public ProxyVoice(AbstractVoice realVoice){
        this.realVoice = realVoice;
    }

    /**
     * 初始化语音服务
     * MyApplication的OnCreate()中调用
     */
    @Override
    public void initSpeech(){
        realVoice.initSpeech();
    }

    /************************************【其他功能接口】******************************************/

    /**
     * 设置是否循环监听
     * @param recyclerListen
     */
    @Override
    public void setRecyclerListen(boolean recyclerListen){
        realVoice.setRecyclerListen(recyclerListen);
    }

    /**
     * 切换语言
     * @param language
     */
    @Override
    public void switchLanguage(String language){
        realVoice.switchLanguage(language);
    }

    /**
     * 设置新的发音人
     * @param speaker
     */
    @Override
    public void setTtsParams(Speaker speaker){
        realVoice.setTtsParams(speaker);
    }

    /************************************【唤醒回调接口】******************************************/
    /**
     * 设置唤醒后的回调接口
     * @param wakeupCallback
     */
    @Override
    public void setWakeupCallback(WakeupCallback wakeupCallback){
        realVoice.setWakeupCallback(wakeupCallback);
    }

    /**
     * 返回唤醒后的回调接口
     */
    @Override
    public WakeupCallback getWakeupCallback(){
        return realVoice.getWakeupCallback();
    }

    /**
     * 唤醒开启接口
     */
    @Override
    public void startWakeup(){
        realVoice.startWakeup();
    }

    /**
     * 需要检测，并重启唤醒 【默认的设置是不需要】
     * @return true 代表需要检测，并重启唤醒
     *          false 代表不需要检测，不需要重启唤醒
     */
    @Override
    public boolean reboot(){
        return false;
    }

    /**
     * 设置看门狗的值
     * @param reboot
     */
    @Override
    public void setReboot(boolean reboot){
        realVoice.setReboot(reboot);
    }

    /**
     * 得到看门狗的值
     * @return
     */
    @Override
    public boolean isReboot(){
        return false;
    }

    /************************************【语义理解相关接口】**************************************/
    /**
     * 设置语义理解回调接口【集合】
     * @param understandCallback
     */
    @Override
    public void addUnderstandCallback(String key, ProxyVoice.UnderstandCallback understandCallback) {
        realVoice.addUnderstandCallback(key, understandCallback);
    }

    /**
     * 删除对应回调接口
     * @param key
     */
    @Override
    public void removeUnderstandCallback(String key){
        realVoice.removeUnderstandCallback(key);
    }

    /**
     * 开始语义理解
     */
    @Override
    public void startUnderstand(){
        realVoice.startUnderstand();
    }

    /**
     * 停止录音，上传数据
     */
    @Override
    public void stopUnderstand(){
        realVoice.stopUnderstand();
    }

    /**
     * 取消会话
     */
    @Override
    public void cancelUnderstand(){
        realVoice.cancelUnderstand();
    }

    /**
     * 直接加载语义理解语音数据
     * @param dataS
     */
    @Override
    public void writeUnderstand(byte[] dataS){
        realVoice.writeUnderstand(dataS);
    }


    /************************************语法识别相关接口******************************************/
    /**
     * 设置语法识别回调接口【集合】
     * @param key
     * @param recognizerCallback
     */
    @Override
    public void addRecognizerCallback(String key, RecognizerCallback recognizerCallback){
        realVoice.addRecognizerCallback(key, recognizerCallback);
    }

    /**
     * 删除对应回调接口
     * @param key
     */
    @Override
    public void removeRecognizerCallback(String key){
        realVoice.removeRecognizerCallback(key);
    }

    /**
     * 开始语法识别
     */
    @Override
    public void startRecognizer(){
        realVoice.startRecognizer();
    }

    /**
     * 开始语法识别
     * @param recFlag
     * 0：在线听写
     * 1：在线命令词
     * 2：离线命令词
     */
    @Override
    public void startRecognizer(int recFlag){
        realVoice.startRecognizer(recFlag);
    }

    /**
     * 停止录音，上传数据
     */
    @Override
    public void stopRecognizer(){
        realVoice.stopRecognizer();
    }

    /**
     * 取消会话
     */
    @Override
    public void cancelRecognizer(){
        realVoice.cancelRecognizer();
    }

    /**
     * 直接加载语法识别语音数据
     * @param dataS
     */
    @Override
    public void writeRecognizer(byte[] dataS){
        realVoice.writeRecognizer(dataS);
    }

    /***********************************【文字理解相关接口】***************************************/
    /**
     * 设置文本理解回调接口
     * @param textUnderCallback
     */
    @Override
    public void setTextUnderCallback(TextUnderCallback textUnderCallback){
        realVoice.setTextUnderCallback(textUnderCallback);
    }

    /**
     * 直接用文本和语音服务器交互
     * @param text
     */
    @Override
    public void startText(String text){
        realVoice.startText(text);
    }

    /************************************【语音合成相关接口】**************************************/
    /**
     * 开始语音合成
     * @param words  -------------------- 发音要读的文字
     * @param tag    -------------------- 随军携带的标志
     * @param synthesizerCallback
     */
    @Override
    public void startTTS(String words, String tag, SynthesizerCallback synthesizerCallback){
        realVoice.startTTS(words, tag, synthesizerCallback);
    }

    /**
     * 暂停播放
     */
    @Override
    public void pauseTTS(){
        realVoice.pauseTTS();
    }

    /**
     * 恢复播放
     */
    @Override
    public void resumeTTS(){
        realVoice.resumeTTS();
    }

    /**
     * 停止播放
     */
    @Override
    public void stopTTS(){
        realVoice.stopTTS();
    }

    /***********************************【脚本相关功能接口】***************************************/
    /**
     * 更新离线命令语法规则    【科大讯飞使用】【包括动态更新联系人】
     * @param rule             规则名
     * @param ruleValue       规则值
     */
    @Override
    public void updateRule(String rule, String ruleValue){
        realVoice.updateRule(rule, ruleValue);
    }

    /**
     * 动态加载，在线、离线命令词 【科大讯飞使用】
     * @param loadScriptType
     * {@link E_LOAD_SCRIPT_TYPE#ONLINE_ASSETS}:  从【assets】加载在线命令词
     * {@link E_LOAD_SCRIPT_TYPE#OFFLINE_ASSETS}：从【assets】加载离线命令词
     * {@link E_LOAD_SCRIPT_TYPE#ONLINE_ABSOLUTE}:  从【绝对路径】加载在线命令词
     * {@link E_LOAD_SCRIPT_TYPE#OFFLINE_ABSOLUTE}：从【绝对路径】加载离线命令词
     *
     * @param grammarPath     创建脚本路径
     * @param grammarDir      语法文件的生成解析路径
     *
     */
    @Override
    public void loadCmdWords(E_LOAD_SCRIPT_TYPE loadScriptType, String grammarPath, String grammarDir){
        realVoice.loadCmdWords(loadScriptType, grammarPath, grammarDir);
    }

    /********************************【系统状态变化时的通知接口】**********************************/
    /**
     * 通知什么改变了
     * @param flag
     * @param obj
     */
    @Override
    public void notifyChange(int flag, Object obj){
        realVoice.notifyChange(flag, obj);
    }

}
