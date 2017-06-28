package com.zccl.ruiqianqi.plugin.voice;

/**
 * Created by ruiqianqi on 2016/9/28 0028.
 */

public abstract class AbstractVoice {

    /**
     * 初始化语音服务
     */
    public abstract void initSpeech();

    /************************************【其他功能接口】******************************************/
    /**
     * 设置是否循环监听
     * @param recyclerListen
     */
    public void setRecyclerListen(boolean recyclerListen){
    }

    /**
     * 切换语言
     * @param language
     */
    public void switchLanguage(String language){
    }

    /**
     * 设置新的发音人
     * @param speaker
     */
    public void setTtsParams(Speaker speaker){
    }

    /************************************【唤醒相关接口】******************************************/
    /**
     * 唤醒相关的回调
     */
    public interface WakeupCallback{
        // 唤醒成功
        void wakeSuccess(WakeInfo wakeInfo);
        // 音频数据
        void onAudio(byte[] audio, int audioLen);
        // 唤醒失败
        void wakeFailure(Throwable e);
        // 单次唤醒及说话
        void oneShot(String msg);

        // 是不是触摸唤醒
        boolean isTouchWake();
    }

    /**
     * 设置唤醒后的回调接口【主动获取数据】
     * @param wakeupCallback
     */
    public void setWakeupCallback(WakeupCallback wakeupCallback){
    }

    /**
     * 返回唤醒后的回调接口【被动获取数据】
     */
    public WakeupCallback getWakeupCallback(){
        return null;
    }

    /**
     * 开启唤醒接口
     */
    public void startWakeup(){
    }

    /**
     * 停止唤醒接口
     */
    public void stopWakeup(){
    }

    /**
     * 需要检测，并重启唤醒 【默认的设置是不需要，空实现】
     * @return true 代表需要检测，并重启唤醒
     *          false 代表不需要检测，不需要重启唤醒
     */
    public boolean reboot(){
        return false;
    }

    /**
     * 设置看门狗的值
     * @param reboot
     */
    public void setReboot(boolean reboot){
    }

    /**
     * 得到看门狗的值
     * @return
     */
    public boolean isReboot(){
        return false;
    }

    /************************************【语义理解相关接口】**************************************/
    /**
     * 语义理解的回调
     */
    public interface UnderstandCallback{

        // 语义理解出错
        int UNDERSTAND_FAILURE = 0;
        // 语义理解成功
        int UNDERSTAND_SUCCESS = 1;

        // 音量大小
        void onVolumeChanged(int volume);
        // 开始说话
        void onBeginOfSpeech();
        // 结束说话
        void onEndOfSpeech();
        // 返回结果
        void onResult(String result);
        // 理解出错
        void onError(Throwable e);
    }

    /**
     * 设置语义理解回调接口【集合】
     * @param understandCallback
     */
    public void addUnderstandCallback(String key, UnderstandCallback understandCallback) {
    }

    /**
     * 删除对应回调接口
     * @param key
     */
    public void removeUnderstandCallback(String key){
    }

    /**
     * 开始语义理解
     */
    public void startUnderstand(){
    }

    /**
     * 停止录音，上传数据
     */
    public void stopUnderstand(){
    }

    /**
     * 取消会话
     */
    public void cancelUnderstand(){
    }

    /**
     * 直接加载语义理解语音数据
     * @param dataS
     */
    public void writeUnderstand(byte[] dataS){
    }


    /*********************************【语法识别相关接口】*****************************************/
    /**
     * 语法识别的回调
     */
    public interface RecognizerCallback{

        // 听写出错
        int LISTEN_ERROR = -1;
        // 在线听写
        int LISTEN = 0;
        // 在线命令词
        int ONLINE_WORD = 1;
        // 离线命令词
        int OFFLINE_WORD = 2;
        // 语义理解
        int LISTEN_UNDERSTAND = 3;

        // 音量大小
        void onVolumeChanged(int volume);
        // 开始说话
        void onBeginOfSpeech();
        // 结束说话
        void onEndOfSpeech();
        // 返回结果
        void onResult(String result, int flag);
        // 识别出错
        void onError(Throwable e);
    }

    /**
     * 设置语法识别回调接口【集合】
     * @param key
     * @param recognizerCallback
     */
    public void addRecognizerCallback(String key, RecognizerCallback recognizerCallback){
    }

    /**
     * 删除对应回调接口
     * @param key
     */
    public void removeRecognizerCallback(String key){
    }

    /**
     * 开始语法识别
     */
    public void startRecognizer(){
    }

    /**
     * 开始语法识别
     * @param recFlag
     * 0：在线听写
     * 1：在线命令词
     * 2：离线命令词
     */
    public void startRecognizer(int recFlag){
    }

    /**
     * 停止录音，上传数据
     */
    public void stopRecognizer(){
    }

    /**
     * 取消会话
     */
    public void cancelRecognizer(){
    }

    /**
     * 直接加载语法识别语音数据
     * @param dataS
     */
    public void writeRecognizer(byte[] dataS){
    }

    /***********************************【文字理解相关接口】***************************************/
    /**
     * 文字识别的回调接口
     */
    public interface TextUnderCallback{
        // 文字理解结果
        void onResult(String result);
        // 文字理解出错
        void onError(Throwable e);
    }

    /**
     * 设置文本理解回调接口
     * @param textUnderCallback
     */
    public void setTextUnderCallback(TextUnderCallback textUnderCallback){
    }

    /**
     * 直接用文本和语音服务器交互
     * @param text
     */
    public void startText(String text){
    }

    /************************************【语音合成相关接口】**************************************/
    /**
     * 语音合成的回调
     */
    public interface SynthesizerCallback{
        // 合成开始
        void OnBegin();
        // 合成暂停
        void OnPause();
        // 合成恢复
        void OnResume();
        // 合成完成
        void OnComplete(Throwable throwable, String tag);
    }

    /**
     * 开始语音合成
     * @param words  -------------------- 发音要读的文字
     * @param tag    -------------------- 随军携带的标志
     * @param synthesizerCallback
     */
    public void startTTS(String words, String tag, SynthesizerCallback synthesizerCallback){
    }

    /**
     * 开始语音合成
     * @param words  -------------------- 发音要读的文字
     * @param tag    -------------------- 随军携带的标志
     * @param runnable
     */
    public void startTTS(String words, String tag, Runnable runnable){
    }

    /**
     * 开始语音合成
     * @param words  -------------------- 发音要读的文字
     * @param runnable
     */
    public void startTTS(String words, Runnable runnable){
    }

    /**
     * 暂停播放
     */
    public void pauseTTS(){
    }

    /**
     * 恢复播放
     */
    public void resumeTTS(){
    }

    /**
     * 停止播放
     */
    public void stopTTS(){
    }

    /**
     * 是不是正在播音
     * @return
     */
    public boolean isSpeaking(){
        return false;
    }
    /***********************************【脚本相关功能接口】***************************************/
    /**
     * 更新离线命令语法规则    【科大讯飞使用】【包括动态更新联系人】
     * @param rule             规则名
     * @param ruleValue       规则值
     */
    public void updateRule(String rule, String ruleValue){
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
    public void loadCmdWords(E_LOAD_SCRIPT_TYPE loadScriptType, String grammarPath, String grammarDir){
    }


    /********************************【系统状态变化时的通知接口】**********************************/
    // 网络状态改变了
    public static final int NET_CHANGE = 1;
    // 电话状态改变了
    public static final int PHONE_CHANGE = 2;
    // 传感器状态改变了
    public static final int SENSOR_CHANGE = 3;
    // 电池状态变化了
    public static final int BATTERY_CHANGE = 4;
    // APP状态改变了
    public static final int APP_STATUS_CHANGE = 5;
    // 循环监听来了
    public static final int RECYCLE_LISTEN_CHANGE = 6;
    // 停止监听来了
    public static final int STOP_LISTEN_CHANGE = 7;
    // HDMI状态改变了
    public static final int HDMI_CHANGE = 8;

    /**
     * 通知什么改变了
     * @param flag
     * @param obj   携带信息
     */
    public void notifyChange(int flag, Object obj){

    }

    /**
     * 加载【在线、离线】命令词
     */
    public enum E_LOAD_SCRIPT_TYPE{
        ONLINE_ASSETS,
        OFFLINE_ASSETS,
        ONLINE_ABSOLUTE,
        OFFLINE_ABSOLUTE,
    }
}
