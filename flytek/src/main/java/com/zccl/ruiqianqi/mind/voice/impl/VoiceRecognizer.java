package com.zccl.ruiqianqi.mind.voice.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.UserWords;
import com.zccl.ruiqianqi.mind.voice.R;
import com.zccl.ruiqianqi.mind.voice.impl.beans.asr.Cw;
import com.zccl.ruiqianqi.mind.voice.impl.beans.asr.VoiceBean;
import com.zccl.ruiqianqi.mind.voice.impl.beans.asr.Ws;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.zccl.ruiqianqi.mind.voice.impl.Configuration.E_ASR_TYPE.TYPE_ASR_LISTEN;
import static com.zccl.ruiqianqi.mind.voice.impl.Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_MSC;
import static com.zccl.ruiqianqi.mind.voice.impl.Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_PLUS;


/**
 * Created by ruiqianqi on 2016/8/10 0010.
 */
public class VoiceRecognizer extends BaseVoice implements RecognizerListener {
    /**
     * 类日志标志
     */
    private static final String TAG = VoiceRecognizer.class.getSimpleName();
    /**
     * 选择语法文本，及正则匹配文本
     * projection 离线命令不能用这个词，会报错。
     */
    public static final String GRAMMAR = "commands";
    /**
     * 云端语法文件名
     */
    public static final String CLOUDS = "clouds";
    /**
     * 离线GrammarId的KEY, 最主要的语法ID
     */
    private static final String KEY_GRAMMAR_ID = "grammar_id";

    /**
     * 离线模式选择，单纯的离线模式 or 混合离线模式
     */
    public static final OFFLINE_TYPE MIX_OR_NOT = OFFLINE_TYPE.MIX_TYPE_MODE;
    /**
     * 单例引用
     */
    private static VoiceRecognizer instance;
    /**
     * 云端语法类型
     */
    private final String GRAMMAR_TYPE_A_BNF = "abnf";
    /**
     * 本地语法类型
     */
    private final String GRAMMAR_TYPE_BNF = "bnf";
    /**
     * 语法匹配
     */
    private SpeechRecognizer speechRecognizer;
    /**
     * 云端引擎还是本地引擎
     */
    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    /**
     * 返回结果格式，支持：xml,json
     */
    private String mResultType = "json";
    /**
     * 本地语法
     */
    private String localGrammar;
    /**
     * 本地联系人
     */
    private String localContact;
    /**
     * 在线识别的识别门限值
     * 混合识别的本地门限值
     * 单独离线识别的分数值
     */
    private int THRESHOLD = 20;
    /**
     * 音频保存路径
     */
    private String SAVE_AUDIO_PATH = MyConfigure.SDCARD + "msc" + File.separator;
    /**
     * 语法构建路径,这个还不能和其他同样使用此功能的路径一样
     */
    private String SAVE_GRAMMAR_PATH = SAVE_AUDIO_PATH + "grammar" + File.separator;
    /**
     * 语音听写计数器
     */
    private int asrCount = 0;
    /**
     * 语法名称集合，只是取{@link VoiceRecognizer#mAllGrammarValue} 值的时候，用来做计算的
     */
    private ConcurrentMap<String, String> mGrammarMap = null;
    /**
     * 要识别的语法值，可能有多个离线语法文件
     */
    private volatile String mAllGrammarValue = "";
    /**
     * 云端语法
     */
    private String cloudGrammar;
    /**
     * 在线用户词表【文件形式】
     */
    private String userWordsAssets;
    /**
     * 在线用户词表【动态构造】
     */
    private UserWords userWords;
    /**
     * 离线命令词匹配的对象
     */
    //private CmdRegex mCmdRegex;
    /**
     * SLOT对应的ID集合，离线专用
     * 我要看视频|<iWant>*<look>*<medias>*|65535*65535*7*
     */
    private Map<String, String> mSlotIdMaps = null;
    /**
     * 在线听写结果
     */
    private StringBuffer strBufListen = null;
    /**
     * 语法识别回调接口，多接口
     */
    private Map<String, AbstractVoice.RecognizerCallback> recognizerCallbackMap;


    protected VoiceRecognizer(Context context) {
        super(context);

        speechRecognizer = SpeechRecognizer.createRecognizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    LogUtils.e(TAG, mContext.getString(R.string.asr_init_failure) + code);
                } else {
                    init();
                    LogUtils.e(TAG, mContext.getString(R.string.asr_init_success));
                }
            }
        });

        // 听写结果存储
        strBufListen = new StringBuffer();
        // 识别回调接口
        recognizerCallbackMap = new ConcurrentHashMap<>();
        // 多个离线文件ID
        mGrammarMap = new ConcurrentHashMap<>();
        // SLOT对应的ID集合，离线专用
        mSlotIdMaps = new HashMap<>();

        // 用正则表达式，匹配出命令词
        //mCmdRegex = CmdRegex.getInstance(mContext);
        // 加载命令匹配正则
        //mCmdRegex.load(GRAMMAR);

    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static VoiceRecognizer getInstance(Context context) {
        if (instance == null) {
            synchronized (VoiceRecognizer.class) {
                VoiceRecognizer temp = instance;
                if (temp == null) {
                    temp = new VoiceRecognizer(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /****************************************【初始化】********************************************/
    /**
     * 类初始化
     */
    private void init() {

        // 语音听写【MSC】
        if (TYPE_ASR_LISTEN == Configuration.ASR_TYPE) {
            initWordsListen();
        }
        // 在线命令词【MSC】
        else if (Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE == Configuration.ASR_TYPE) {
            initWordsOnline();
        }
        // 离线命令词
        else {
            initWordsOffline();
        }

    }

    /**
     * 初始化语音听写
     */
    private void initWordsListen() {
        setListenParams();
    }

    /**
     * 初始化在线命令词
     */
    private void initWordsOnline() {
        // 在线命令语法
        cloudGrammar = FileUtils.readStreamToStr(FileUtils.getFileStream(mContext, CLOUDS + ".abnf", MyConfigure.ONE_ASSETS));
        // 文件中的在线命令词
        userWordsAssets = FileUtils.readStreamToStr(FileUtils.getFileStream(mContext, "userWords", MyConfigure.ONE_ASSETS));
        /**
         * 有一个默认的key: default,没有加key操作的方法，默认都是这个key
         * 【一个key对应的有很多个word】
         这个是用户热词表【是在线用的】
         {
         "userword":
         [
         { "name" : "default" , "words" : [ "默认词条1", "默认词条2" ] },
         { "name" : "词表名称1", "words": [ "词条1的第一个词", "词条1的第二个词"] },
         { "name" : "词表名称2", "words": [ "词条2的第一个词", "词条2的第二个词"] }
         ]
         }
         */
        // 动态构造的在线命令词
        userWords = new UserWords();
        ArrayList<String> hotWords = new ArrayList<>();
        hotWords.add("洪荒之力");
        hotWords.add("何弃疗");
        hotWords.add("吃瓜群众");
        hotWords.add("铲屎官");
        hotWords.add("千禧一代");
        hotWords.add("悔脱欧");
        // userWords.putWords(hotWords) == userWords.putWords("default", hotWords)
        userWords.putWords("热词", hotWords);

        setCloudParams(true);
    }

    /**
     * 初始化离线命令词
     */
    private void initWordsOffline() {
        localGrammar = FileUtils.readStreamToStr(FileUtils.getFileStream(mContext, GRAMMAR + ".bnf", MyConfigure.ONE_ASSETS));

        // 离线命令词【MSC】
        if(TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE){
            setLocalParams(true, null);
        }
        // 离线命令词【语记】
        else if (TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE) {
            setYuJiParams(true);
        }

    }

    /***********************************【自身参数设置】*******************************************/
    /**
     * 设置语法识别回调接口【集合】，二级回调接口
     *
     * @param recognizerCallback
     */
    public void addRecognizerCallback(String key, AbstractVoice.RecognizerCallback recognizerCallback) {
        recognizerCallbackMap.put(key, recognizerCallback);
    }

    /**
     * 删除对应回调接口
     *
     * @param key
     */
    public void removeRecognizerCallback(String key) {
        if (recognizerCallbackMap.containsKey(key)) {
            recognizerCallbackMap.remove(key);
        }
    }

    /*************************************【语法识别参数设置】*************************************/
    /**
     * 【在线听写】
     * 设置语音听写参数，只能在线使用
     */
    private void setListenParams() {
        mEngineType = SpeechConstant.TYPE_CLOUD;
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎类型
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "UTF-8");
    }

    /**
     * 【在线命令词】【要设置识别门限值】
     * 云端语法引擎
     * 科大迅飞服务器能识别的格式是：采样率16k或者8k，采样精度16bit，
     * 单声道pcm或者wav格式的音频
     *
     * @param loadDefaultScript     是否加载默认脚本
     */
    private void setCloudParams(boolean loadDefaultScript) {
        mEngineType = SpeechConstant.TYPE_CLOUD;
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 指定引擎类型
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "UTF-8");
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置识别的门限值
        speechRecognizer.setParameter(SpeechConstant.ASR_THRESHOLD, THRESHOLD+"");

        if (loadDefaultScript) {
            int ret = speechRecognizer.buildGrammar(GRAMMAR_TYPE_A_BNF, cloudGrammar, new MyGrammarListener());
            if (ret != ErrorCode.SUCCESS) {
                //LogUtils.e(TAG, mContext.getString(R.string.asr_online_grammar_failure) + ret);
            } else {
                //LogUtils.e(TAG, mContext.getString(R.string.asr_online_grammar_success));
            }
        }
    }

    /**
     * 【离线MSC命令词】
     *  构建本地语法
     * 【离线单独使用的时候没有门限值的限制，设置了也没用，只有匹配的分数返回】
     *
     * @param loadDefaultScript    是否加载默认离线语法
     * @param grammarDir            语法释放目录
     */
    protected void setLocalParams(boolean loadDefaultScript, String grammarDir) {
        mEngineType = SpeechConstant.TYPE_LOCAL;
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 启动离线引擎
        speechRecognizer.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_ASR);

        // 设置语法构建路径
        // 在使用离线语法时，需要构建语法并保存到本地，在构建和使用语法时，都需要设置语法的构建目录。
        // 与识别资源路径值不一样的是，语法的路径值，【不需要通过generateResourcePath生成SDK要求的格式。】
        if (StringUtils.isEmpty(grammarDir)) {
            speechRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, SAVE_GRAMMAR_PATH);
        } else {
            speechRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, grammarDir);
        }
        // 设置资源路径【通过generateResourcePath生成SDK要求的格式】
        speechRecognizer.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        // 设置引擎类型
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "UTF-8");

        if (loadDefaultScript) {
            // 加载离线命令
            int ret = speechRecognizer.buildGrammar(GRAMMAR_TYPE_BNF, localGrammar, new MyGrammarListener());
            if (ret != ErrorCode.SUCCESS) {
                LogUtils.e(TAG, mContext.getString(R.string.asr_msc_grammar_failure) + ret);
            } else {
                LogUtils.e(TAG, mContext.getString(R.string.asr_msc_grammar_success));
            }
        }
    }

    /**
     * 获取识别资源路径【通过generateResourcePath生成SDK要求的格式】
     */
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        // 识别8k资源-使用8k的时候请解开注释
        //tempBuffer.append(";");
        //tempBuffer.append(ResourceUtil.generateResourcePath(mContext, RESOURCE_TYPE.assets, "asr/common_8k.jet"));
        return tempBuffer.toString();
    }

    /**
     * 设置使用录音
     */
    private void setRecorderParams() {
        speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, null);
    }

    /**
     * 设置使用外部音频源
     */
    private void setWriteAudioParams() {
        speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
    }

    /**
     * 设置语义理解参数
     *
     * @return
     */
    private void setUnderParams() {

        // 设置识别语言
        setLanguage(Configuration.Language);

        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, mResultType);

        // 服务器为不同的应用领域，定制了不同的听写匹配引擎，使用对应的领域能获取更 高的匹配率
        // { "iat", "video", "poi", "music" }
        speechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "8000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入，自动停止录音
        speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "800");
        // 设置标点符号，默认：1（有标点）
        speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");
        // 使用8k音频的时候请解开注释
        // speechRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        // 采样率
        speechRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        // 设置音频保存路径，保存音频格式支持pcm、wav，注：AUDIO_FORMAT参数语记需要更新版本才能生效
        speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 录音存放路径
        setSaveAudio(false);

    }

    /**
     * 是不是保存音频数据
     *
     * @param saveAudio
     */
    protected void setSaveAudio(boolean saveAudio) {
        if (saveAudio) {
            // 录音存放路径
            speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, SAVE_AUDIO_PATH + "asr" + (asrCount++) + ".wav");
        } else {
            asrCount = 0;
            speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, null);
        }
    }

    /************************************【提供给外部使用的方法】**********************************/
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
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");

            // 设置远场模式
            speechRecognizer.setParameter("ent", "smsfar16k");

        } else if (language.equals("zh")) {
            // 设置语言
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

            // 解除麦克风独占
            speechRecognizer.setParameter("domain", "fariat");

            speechRecognizer.setParameter("aue", "speex-wb;10");
        }
        // 设置【普通话 mandarin 粤语 cantonese 河南话 henanese】
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
    }

    /**
     * 开始识别
     * @param asrType 识别方式不可能随意切换的，只能根据最初 Configuration.ASR_TYPE 的配置来运行
     *
     * Configuration.E_ASR_TYPE.TYPE_ASR_LISTEN        在线识别          OK
     * Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE        在线命令词
     * Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_MSC   离线【MSC】命令词 OK
     * Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_PLUS  离线【PLUS】命令词
     */
    public void start(Configuration.E_ASR_TYPE asrType) {

        Configuration.E_ASR_TYPE tmpAsrType;
        // 如果是英文，则只有听写功能
        if (Configuration.Language.equals("en")) {
            tmpAsrType = TYPE_ASR_LISTEN;
        }
        // 如果是中文，只有【在线听写】能和【剩余三种识别方式】切换
        else {
            if(TYPE_ASR_LISTEN == asrType){
                tmpAsrType = TYPE_ASR_LISTEN;
            }else {
                tmpAsrType = TYPE_ASR_OFFLINE_MSC;
            }
        }

        Configuration.ASR_TYPE = tmpAsrType;

        if (speechRecognizer == null) {
            return;
        }
        stop();

        // 在线听写
        if (TYPE_ASR_LISTEN == tmpAsrType) {
            mEngineType = SpeechConstant.TYPE_CLOUD;
            speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);

        }
        // 在线命令词
        else if (Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE == tmpAsrType) {

        }
        // 离线命令词【语记】【MSC】
        else if (TYPE_ASR_OFFLINE_MSC == tmpAsrType ||
                TYPE_ASR_OFFLINE_PLUS == tmpAsrType) {

            // 单独的离线模式
            if (OFFLINE_TYPE.OFFLINE_TYPE_MODE == MIX_OR_NOT) {
                mEngineType = SpeechConstant.TYPE_LOCAL;
                speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
                // 设置本地识别使用的语法id
                speechRecognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, mAllGrammarValue);
            }
            // 混合模式
            else if(OFFLINE_TYPE.MIX_TYPE_MODE == MIX_OR_NOT){
                setMixParams();
            }
            LogUtils.e(TAG, "多个语法文件ID集合：" + mAllGrammarValue);
        }

        // 设置语言及听写参数
        setUnderParams();

        // 设置音频来源
        if (mDataSourceType == DATA_SOURCE_TYPE.TYPE_RAW_DATA) {
            setWriteAudioParams();
        } else {
            setRecorderParams();
        }

        int ret = speechRecognizer.startListening(this);
        if (ret != ErrorCode.SUCCESS) {
            handleError(mContext.getString(R.string.asr_start_failure) + ret);

        } else {
            if (TYPE_ASR_LISTEN == tmpAsrType) {
                LogUtils.e(TAG, "开始听写......");
            }
            // 在线命令词
            else if (Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE == tmpAsrType) {
                LogUtils.e(TAG, "在线命令......");
            }
            // 离线命令词【MSC】
            else if(TYPE_ASR_OFFLINE_MSC == tmpAsrType){
                LogUtils.e(TAG, "离线【MSC】+ 语义......");
            }
            // 离线命令词【语记】
            else if (TYPE_ASR_OFFLINE_PLUS == tmpAsrType) {
                LogUtils.e(TAG, "离线【PLUS】+ 语义......");
            }
        }
    }

    /**
     * 停止识别，然后上传
     */
    public void stop() {
        if (null == speechRecognizer) {
            return;
        }
        if (speechRecognizer.isListening()) {
            speechRecognizer.stopListening();
        }
    }

    /**
     * 取消会话
     */
    public void cancel() {
        if (null == speechRecognizer) {
            return;
        }
        speechRecognizer.cancel();
    }

    /**
     * 是不是已开启监听
     *
     * @return
     */
    public boolean isListening() {
        if (null == speechRecognizer) {
            return false;
        }
        return speechRecognizer.isListening();
    }

    /**
     * 加载原始录音
     * 一次（也可以分多次）写入音频文件数据，数据格式必须是采样率为8KHz或16KHz（本地识别只支持16K采样率，云端都支持），位长16bit，单声道的wav或者pcm
     * 写入8KHz采样的音频时，必须先调用setParameter(SpeechConstant.SAMPLE_RATE, "8000")设置正确的采样率
     * 注：当音频过长，静音部分时长超过VAD_EOS将导致静音后面部分不能识别
     *
     * @param dataS
     */
    public void writeAudio(byte[] dataS) {
        if (null == speechRecognizer) {
            return;
        }
        if (mDataSourceType == DATA_SOURCE_TYPE.TYPE_RAW_DATA) {
            if (speechRecognizer.isListening()) {
                speechRecognizer.writeAudio(dataS, 0, dataS.length);
            }
        }
    }

    /**
     * 单独加载音频资源
     *
     * @param buffer
     */
    /*
    public void loadAudio(byte[] buffer) {
        if (speechRecognizer == null) {
            return;
        }
        stop();
        setWriteAudioParams();

        int ret = speechRecognizer.startListening(this);
        if (ret != 0) {
            LogUtils.e(TAG, mContext.getString(R.string.asr_start_failure) + ret);
        } else {
            if (speechRecognizer.isListening()) {
                speechRecognizer.writeAudio(buffer, 0, buffer.length);
            }
        }
    }
    */

    /********************************【语音听写回调及二次回调】************************************/
    /**
     * 音频大小回调
     * @param volume
     * @param bytes
     */
    @Override
    public void onVolumeChanged(int volume, byte[] bytes) {
        for (String key : recognizerCallbackMap.keySet()) {
            if (recognizerCallbackMap.get(key) != null) {
                recognizerCallbackMap.get(key).onVolumeChanged(volume);
            }
        }
    }

    /**
     * 开始说话
     */
    @Override
    public void onBeginOfSpeech() {
        for (String key : recognizerCallbackMap.keySet()) {
            if (recognizerCallbackMap.get(key) != null) {
                recognizerCallbackMap.get(key).onBeginOfSpeech();
            }
        }
    }

    /**
     * 结束说话
     */
    @Override
    public void onEndOfSpeech() {
        for (String key : recognizerCallbackMap.keySet()) {
            if (recognizerCallbackMap.get(key) != null) {
                recognizerCallbackMap.get(key).onEndOfSpeech();
            }
        }
    }

    /**
     * 识别结果
     * @param recognizerResult
     * @param isLast --------- 标志每句话最后的返回，true表明话结束了，false表明在中途解析中
     */
    @Override
    public void onResult(RecognizerResult recognizerResult, boolean isLast) {
        if (recognizerResult != null) {
            parseResult(recognizerResult.getResultString(), isLast);
        } else {
            for (String key : recognizerCallbackMap.keySet()) {
                if (recognizerCallbackMap.get(key) != null) {
                    recognizerCallbackMap.get(key).onError(new Throwable(mContext.getString(R.string.server_exception)));
                }
            }
        }
    }

    /**
     * 错误回调
     * MSP_ERROR_NO_MORE_DATA	10119	没有更多的数据
     *
     * @param speechError
     */
    @Override
    public void onError(SpeechError speechError) {
        if (speechError != null) {
            String error = speechError.getErrorDescription() + "-" + speechError.getErrorCode();
            // 错误回调
            handleError(error);
        } else {
            handleError("SpeechError is null");
        }
    }

    @Override
    public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
        // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        // 若使用本地能力，会话id为null
        if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            String sid = bundle.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            LogUtils.d(TAG, "sessionId =" + sid);
        }
    }

    /**
     * 处理异常
     *
     * @param msg
     */
    private void handleError(String msg) {
        // 错误回调
        for (String key : recognizerCallbackMap.keySet()) {
            if (recognizerCallbackMap.get(key) != null) {
                recognizerCallbackMap.get(key).onError(new Throwable(msg));
            }
        }
    }


    /***********************************【动态加载离线语法】***************************************/
    /**
     * 动态加载离线语法【仅仅是加载，也就是加载现存的，并不需要创建】
     *
     * @param grammarPath 语法文件的绝对路径
     * @param loadWay     加载方式
     * {@link MyConfigure#ONE_ASSETS}        从assets加载
     * {@link MyConfigure#SIX_ABSOLUTE}      从绝对路径加载
     *
     * @param grammarDir  语法文件的生成解析路径
     */
    public void loadOfflineGrammar(String grammarPath, int loadWay, String grammarDir) {
        // 加载文件
        String dynamicGrammar = FileUtils.readStreamToStr(FileUtils.getFileStream(mContext, grammarPath, loadWay));
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "UTF-8");
        // 设置是否自动加载默认脚本
        // 及脚本解压路径
        setLocalParams(false, grammarDir);

        // 加载离线语法，10109是无效的数据
        //int ret =
        speechRecognizer.buildGrammar(GRAMMAR_TYPE_BNF, dynamicGrammar, new GrammarListener() {
            @Override
            public void onBuildFinish(String grammarId, SpeechError speechError) {
                if (null == speechError) {

                    // 放入集合
                    mGrammarMap.put(grammarId, grammarId);
                    StringBuffer sb = new StringBuffer();
                    for (String value : mGrammarMap.values()) {
                        sb.append(";" + value);
                    }
                    // 取值形如：【AA;BB】【多语法文件格式】
                    mAllGrammarValue = sb.substring(1);

                    LogUtils.e(TAG, mContext.getString(R.string.asr_dy_msc_grammar_success) + grammarId);
                } else {
                    LogUtils.e(TAG, mContext.getString(R.string.asr_dy_msc_grammar_failure) + speechError.getErrorCode());
                }
            }
        });
        /*
        if(ret != ErrorCode.SUCCESS){
            LogUtils.e(TAG, mContext.getString(R.string.asr_dy_msc_grammar_failure) + ret);
        }else {
            LogUtils.e(TAG, mContext.getString(R.string.asr_dy_msc_grammar_success));
        }
        */
    }

    /**********************************【动态加载在线语法】****************************************/
    /**
     * 动态加载在线语法【仅仅是加载，也就是加载现存的，并不需要创建】
     *
     * @param grammarPath
     * @param loadWay                       加载方式
     * {@link MyConfigure#ONE_ASSETS}        从assets加载
     * {@link MyConfigure#SIX_ABSOLUTE}      从绝对路径加载
     */
    public void loadOnlineGrammar(String grammarPath, int loadWay) {
        // 加载文件
        String dynamicGrammar = FileUtils.readStreamToStr(FileUtils.getFileStream(mContext, grammarPath, loadWay));
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "UTF-8");
        // 设置是否自动加载默认脚本
        setCloudParams(false);

        // 加载在线语法，10109是无效的数据
        speechRecognizer.buildGrammar(GRAMMAR_TYPE_A_BNF, dynamicGrammar, new GrammarListener() {
            @Override
            public void onBuildFinish(String grammarId, SpeechError speechError) {
                if (null == speechError) {
                    // 设置云端识别使用的语法id
                    speechRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);

                    LogUtils.e(TAG, mContext.getString(R.string.asr_dy_msc_grammar_success) + grammarId);
                } else {
                    LogUtils.e(TAG, mContext.getString(R.string.asr_dy_msc_grammar_failure) + speechError.getErrorCode());
                }
            }
        });
    }

    /********************************【动态更新离线语法中的联系人】********************************/
    /**
     * 离线
     * 更新本地联系人词典【离线的】不需要查询
     * "contact"："AA\nBB\nCC\n"
     *
     * @param contactInfoS
     */
    public void updateContact(String contactInfoS) {

        SharedPreferences sp = ShareUtils.getP(mContext);
        String grammarId = sp.getString(KEY_GRAMMAR_ID, GRAMMAR);
        // 设置语法名称【指定更新词典的时候更新哪个语法】
        speechRecognizer.setParameter(SpeechConstant.GRAMMAR_LIST, grammarId);

        speechRecognizer.updateLexicon("contact", contactInfoS, new LexiconListener() {
            @Override
            public void onLexiconUpdated(String s, SpeechError speechError) {
                if (null == speechError) {
                    LogUtils.e(TAG, mContext.getString(R.string.update_contact_success) + s);
                } else {
                    LogUtils.e(TAG, mContext.getString(R.string.update_contact_failure) + speechError.getErrorCode());
                }
            }
        });
    }

    /**
     * 离线
     * 更新本地联系人词典【离线的】，先查询
     * "contact"："AA\nBB\nCC\n"
     */
    public void updateContact() {

        SharedPreferences sp = ShareUtils.getP(mContext);
        String grammarId = sp.getString(KEY_GRAMMAR_ID, GRAMMAR);
        // 设置语法名称【指定更新词典的时候更新哪个语法】
        speechRecognizer.setParameter(SpeechConstant.GRAMMAR_LIST, grammarId);

        // 获取联系人，更新本地词典时使用
        ContactManager mgr = ContactManager.createManager(mContext, new ContactManager.ContactListener() {
            @Override
            public void onContactQueryFinish(String contactInfos, boolean b) {
                localContact = contactInfos + "10086\n";
                LogUtils.e(TAG, "localContact: " + localContact);

                // 更新本地词典****.bnf的contact字段【好像只有contact可以更新】
                speechRecognizer.updateLexicon("contact", localContact, new LexiconListener() {
                    @Override
                    public void onLexiconUpdated(String s, SpeechError speechError) {
                        if (speechError == null) {
                            LogUtils.e(TAG, mContext.getString(R.string.update_contact_success) + s);
                        } else {
                            LogUtils.e(TAG, mContext.getString(R.string.update_contact_failure) + speechError.getErrorCode());
                        }
                    }
                });

            }
        });

        // 异步查询联系人名 异步查询，返回查询结果，不会阻塞调用线程
        mgr.asyncQueryAllContactsName();
    }

    /******************************【动态更新离线语法中的指定的SLOT】******************************/
    /**
     * 离线
     * updateMyselfWords("updateWords", "美国\n英国\n");
     * <p>
     * 更新自定义字段，语法中的字段是可以这样更新的
     *
     * @param updateWords      语法中的字段
     * @param updateWordsValue 语法中字段的值  美国\n英国\n
     */
    public void updateMyselfWords(String updateWords, String updateWordsValue) {
        SharedPreferences sp = ShareUtils.getP(mContext);
        String grammarId = sp.getString(KEY_GRAMMAR_ID, GRAMMAR);
        // 设置语法名称【指定更新词典的时候更新哪个语法】
        speechRecognizer.setParameter(SpeechConstant.GRAMMAR_LIST, grammarId);

        speechRecognizer.updateLexicon(updateWords, updateWordsValue, new LexiconListener() {
            @Override
            public void onLexiconUpdated(String s, SpeechError speechError) {
                if (speechError == null) {
                    LogUtils.e(TAG, mContext.getString(R.string.update_custom_success));
                } else {
                    LogUtils.e(TAG, mContext.getString(R.string.update_custom_failure) + speechError.getErrorCode());
                }
            }
        });
    }

    /**************************************【更新热词】********************************************/
    /**
     * 开放语义中，更新热词，更新了只是增加了在线命令词的识别率
     */
    private void updateHotWords() {
        // 在语法文件中使用了的话，匹配度能大大提高，就这么个作用
        // userword 是用户词库
        speechRecognizer.updateLexicon("userword", userWords.toString(), new LexiconListener() {
            @Override
            public void onLexiconUpdated(String s, SpeechError speechError) {
                if (speechError == null) {
                    LogUtils.e(TAG, mContext.getString(R.string.update_hot_word_success));
                    // 再更新常用词
                    updateUsualWords(null);

                } else {
                    LogUtils.e(TAG, mContext.getString(R.string.update_hot_word_failure) + speechError.getErrorCode());
                }
            }
        });
    }

    /**
     * 开放语义中，更新常用词，更新了只是增加了在线命令词的识别率
     */
    public void updateUsualWords(String usualWords) {
        // 在语法文件中使用了的话，匹配度能大大提高，就这么个作用
        if(StringUtils.isEmpty(usualWords)){
            usualWords = userWordsAssets;
        }
        // userword 是用户词库
        speechRecognizer.updateLexicon("userword", usualWords, new LexiconListener() {
            @Override
            public void onLexiconUpdated(String s, SpeechError speechError) {
                if (speechError == null) {
                    LogUtils.e(TAG, mContext.getString(R.string.update_usual_word_success));
                } else {
                    LogUtils.e(TAG, mContext.getString(R.string.update_usual_word_failure) + speechError.getErrorCode());
                }
            }
        });
    }

    /**************************************【自定义的一些类】**************************************/


    /**
     * 离线模式
     * 混合模式
     */
    public enum OFFLINE_TYPE {
        OFFLINE_TYPE_MODE,
        MIX_TYPE_MODE
    }

    /**
     * 默认的语法加载回调接口，构建语法监听器。
     */
    private class MyGrammarListener implements GrammarListener {

        @Override
        public void onBuildFinish(String grammarId, SpeechError speechError) {
            if (speechError == null) {

                // 保存语法ID
                SharedPreferences.Editor editor = ShareUtils.getE(mContext);
                if (!StringUtils.isEmpty(grammarId)) {
                    editor.putString(KEY_GRAMMAR_ID, grammarId);
                }
                editor.commit();

                if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
                    LogUtils.e(TAG, mContext.getString(R.string.asr_online_grammar_success) + grammarId);

                    // 设置云端识别使用的语法id
                    speechRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
                    // 上传热词到云端词典
                    updateHotWords();

                } else {

                    /*
                    // 离线命令词【MSC】
                    if(TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE){
                        LogUtils.e(TAG, mContext.getString(R.string.asr_msc_grammar_success) + grammarId);
                    }
                    // 离线命令词【语记】
                    else if (Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE) {
                        LogUtils.e(TAG, mContext.getString(R.string.asr_yuji_grammar_success) + grammarId);
                    }
                    */

                    LogUtils.e(TAG, mContext.getString(R.string.asr_msc_grammar_success) + grammarId);

                    // 设置语法名称【指定更新词典的时候更新哪个语法】
                    speechRecognizer.setParameter(SpeechConstant.GRAMMAR_LIST, grammarId);

                    // 更新联系人
                    updateContact();

                    // 放入集合
                    mGrammarMap.put(grammarId, grammarId);
                    StringBuffer sb = new StringBuffer();
                    for (String value : mGrammarMap.values()) {
                        sb.append(";" + value);
                    }
                    mAllGrammarValue = sb.substring(1);

                    // 加载离线语句【第二个语法文件】
                    //loadOfflineGrammar("dynamic.bnf", MyConfigure.ONE_ASSETS);

                    // 加载离线语句【测试版，最多使用98个SLOT组成句子】
                    //loadOfflineGrammar("test.bnf", MyConfigure.ONE_ASSETS);

                }
            } else {
                if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
                    LogUtils.e(TAG, mContext.getString(R.string.asr_online_grammar_failure) + speechError.getErrorCode());
                } else {

                    LogUtils.e(TAG, mContext.getString(R.string.asr_msc_grammar_failure) + speechError.getErrorCode());
                    /*
                    // 离线命令词【MSC】
                    if (TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE){
                        LogUtils.e(TAG, mContext.getString(R.string.asr_msc_grammar_failure) + speechError.getErrorCode());
                    }
                    // 离线命令词【语记】
                    else if (Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE) {
                        LogUtils.e(TAG, mContext.getString(R.string.asr_yuji_grammar_failure) + speechError.getErrorCode());
                    }
                    */
                }
            }
        }
    }

    /************************************【特殊的使用方式】****************************************/
    /**
     * 【离线语记命令词】【必须要下载好语记，并且下载好离线识别包】
     *  构建本地语法
     * 【离线单独使用的时候没有门限值的限制，设置了也没用，只有匹配的分数返回】
     * @param loadDefaultScript
     */
    private void setYuJiParams(boolean loadDefaultScript){
        mEngineType = SpeechConstant.TYPE_LOCAL;
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 设置语法构建路径
        // 在使用离线语法时，需要构建语法并保存到本地，在构建和使用语法时，都需要设置语法的构建目录。
        // 与识别资源路径值不一样的是，语法的路径值，【不需要通过generateResourcePath生成SDK要求的格式。】
        speechRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, SAVE_GRAMMAR_PATH);
        // 设置引擎类型
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "UTF-8");

        if(loadDefaultScript) {
            int ret = speechRecognizer.buildGrammar(GRAMMAR_TYPE_BNF, localGrammar, new MyGrammarListener());
            if (ret != ErrorCode.SUCCESS) {
                //LogUtils.e(TAG, mContext.getString(R.string.asr_yuji_grammar_failure) + ret);
            } else {
                //LogUtils.e(TAG, mContext.getString(R.string.asr_yuji_grammar_success));
            }
        }
    }

    /**
     * 设置使用混合引擎
     */
    private void setMixParams(){
        // 设置引擎为混合模式
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_MIX);
        // 是否进行语义识别
        speechRecognizer.setParameter("asr_sch", "1");
        // 通过此参数，设置开放语义协议版本号。
        speechRecognizer.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        // 混合模式的类型
        // realtime：实时，同时向云端和本地发送音频，在云端超时，或本地置信门限大于指定值时，使用本地结果；
        // delay:    延时，在云端识别超时后，向本地发送音频。即优先用云端的语义理解。
        speechRecognizer.setParameter(SpeechConstant.MIXED_TYPE, "realtime");
        // 混合超时，仅在在delay类型下生效，延时等待云端语义，若云端语义超时返回本地识别结果
        //speechRecognizer.setParameter("mixed_timeout", "2500");
        // 优先使用本地匹配
        speechRecognizer.setParameter("local_prior", "1");
        // 设置识别的门限值
        speechRecognizer.setParameter(SpeechConstant.MIXED_THRESHOLD, THRESHOLD + "");
        // 设置本地识别使用的语法id
        speechRecognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, mAllGrammarValue);
    }


    /**********************************************************************************************/
    /**
     * 解析匹配结果
     *
     * @param result
     */
    public void parseResult(String result, boolean isLast) {
        LogUtils.e(TAG, "parseResult: " + result);

        if (TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE
                || TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE) {
            mSlotIdMaps.clear();
        }

        // 组成命令的命令词语法变量名称
        StringBuffer slots = new StringBuffer();
        // 组成命令的命令词编号
        StringBuffer codes = new StringBuffer();
        // 生成的命令
        StringBuffer words = new StringBuffer();

        // 在线听写、在线命令词、离线命令词，都可以转换的对象
        VoiceBean voice = JsonUtils.parseJson(result, VoiceBean.class);

        if (voice != null) {

            List<Ws> wsList = voice.getWs();
            if (wsList != null) {
                for (int i = 0; i < wsList.size(); i++) {
                    Ws ws = wsList.get(i);

                    slots.append(ws.getSlot() + "*");
                    List<Cw> cwList = ws.getCw();

                    if (cwList != null) {
                        int sc = -1;
                        String w = null;
                        int id = 0;
                        for (int k = 0; k < cwList.size(); k++) {
                            Cw cw = cwList.get(k);
                            if (cw.getSc() > sc) {
                                sc = cw.getSc();
                                w = cw.getW();

                                // 就取分数最高的ID
                                id = cw.getId();

                                if (TYPE_ASR_LISTEN == Configuration.ASR_TYPE) {
                                    // 不处理
                                }
                                // 如果是云端的话，分数是直接给的，不用一个个再计算
                                else if (Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE == Configuration.ASR_TYPE) {
                                    voice.setSc(sc);
                                }
                                // 离线匹配
                                else if (TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE
                                        || TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE) {
                                    // 不处理
                                }

                            }
                        }

                        if (TYPE_ASR_LISTEN == Configuration.ASR_TYPE) {
                            words.append(w);
                        } else if (Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE == Configuration.ASR_TYPE) {
                            words.append(w);
                        } else if (TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE
                                || TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE) {
                            words.append(w);
                            codes.append(id + "*");
                            mSlotIdMaps.put(ws.getSlot(), id + "");
                        }

                    }
                }
            }

            voice.setSerialWord(words.toString());
            voice.setSerialSlot(slots.toString());
            voice.setSerialId(codes.toString());

            mSlotIdMaps.put("text", words.toString());
            voice.setJson(new Gson().toJson(mSlotIdMaps));

            // 【语音听写】
            if (TYPE_ASR_LISTEN == Configuration.ASR_TYPE) {
                //parseListenWords(words.toString(), voice.isLs());
                parseListenWords(words.toString(), isLast);

            }
            // 【在线命令词】
            // 如果是云端的话，分数是直接给的，不用一个个再计算
            else if (Configuration.E_ASR_TYPE.TYPE_ASR_ONLINE == Configuration.ASR_TYPE) {
                if (words.toString().contains("nomatch")) {
                    // 云端无匹配结果
                    handleError(mContext.getString(R.string.cloud_no_match));

                } else {
                    if (voice.getSc() >= THRESHOLD) {
                        parseOnlineWords(words.toString());

                    } else {
                        // 云端匹配度太低
                        handleError(mContext.getString(R.string.cloud_low_match));
                    }
                }
            }
            // 【离线匹配】
            else if (TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE
                    || TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE) {

                // 混合模式
                //【有网络的情况下，满足阀值取本地命令，不满足的话，请求网络语义】
                //【没有网络的情况下，满足阀值取本地命令，不满足的话，照样以本地命令返回，
                // 不过阀值不满足而已，然后就是没有匹配到离线命令词，识别度小于阀值】
                if (voice.getSc() >= THRESHOLD) {
                    parseOfflineWords(voice);

                }
                // 【OneShot及语义理解，会用到】
                else {
                    // JSON解析不对，返回对象存在，但是没赋值，
                    // 如果这个对象的字段还是初始值的话，则就是语义理解
                    if (voice.getSc() == -1) {
                        parseUnderstand(result);
                    }
                    // 没有匹配到离线命令词，识别度小于阀值
                    else {
                        handleError(mContext.getString(R.string.please_online));
                    }
                }
            }
        } else {
            // 【OneShot及语义理解会用到】
            // JSON解析不对，返回对象为空，则就是语义理解
            parseUnderstand(result);
        }
    }

    /**
     * 当成语义理解来处理
     *
     * @param result
     */
    private void parseUnderstand(String result) {
        if (!StringUtils.isEmpty(result)) {
            for (String key : recognizerCallbackMap.keySet()) {
                if (recognizerCallbackMap.get(key) != null) {
                    recognizerCallbackMap.get(key).onResult(result, AbstractVoice.RecognizerCallback.LISTEN_UNDERSTAND);
                }
            }
        } else {
            handleError(mContext.getString(R.string.error_result_is_null));
        }
    }

    /**
     * 解析出听写语句
     *
     * @param result
     */
    private void parseListenWords(String result, boolean end) {
        if (!StringUtils.isEmpty(result)) {
            strBufListen.append(result);
        }
        if (end) {
            for (String key : recognizerCallbackMap.keySet()) {
                if (recognizerCallbackMap.get(key) != null) {
                    recognizerCallbackMap.get(key).onResult(strBufListen.toString(), AbstractVoice.RecognizerCallback.LISTEN);
                }
            }
            strBufListen.setLength(0);
        }
    }

    /**
     * 解析出在线命令词
     *
     * @param result
     */
    private void parseOnlineWords(String result) {
        if (!StringUtils.isEmpty(result)) {
            // 在线命令词回调
            for (String key : recognizerCallbackMap.keySet()) {
                if (recognizerCallbackMap.get(key) != null) {
                    recognizerCallbackMap.get(key).onResult(result, AbstractVoice.RecognizerCallback.ONLINE_WORD);
                }
            }
        } else {
            handleError(mContext.getString(R.string.error_result_is_null));
        }
    }

    /**
     * 解析命令词，开始取对应的指令
     *
     * @param voiceBean 解析到的命令词
     */
    private void parseOfflineWords(VoiceBean voiceBean) {
        if (StringUtils.isEmpty(voiceBean.getSerialSlot())) {
            handleError(mContext.getString(R.string.offline_serial_slot_error));
            return;
        }
        if (StringUtils.isEmpty(voiceBean.getSerialWord())) {
            handleError(mContext.getString(R.string.offline_serial_word_error));
            return;
        } else {
            //String str = voiceBean.getSerialWord() + "|" + voiceBean.getSerialSlot() + "|" + voiceBean.getSerialId();
            String str = voiceBean.getJson();
            // 离线命令词回调
            for (String key : recognizerCallbackMap.keySet()) {
                if (recognizerCallbackMap.get(key) != null) {
                    recognizerCallbackMap.get(key).onResult(str, AbstractVoice.RecognizerCallback.OFFLINE_WORD);
                }
            }
        }
    }

}
