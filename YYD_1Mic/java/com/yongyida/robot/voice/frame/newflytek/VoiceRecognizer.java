package com.yongyida.robot.voice.frame.newflytek;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.cloud.util.ContactManager;
import com.iflytek.cloud.util.ResourceUtil;
import com.roboot.hdmictl.HdmiCtl;
import com.yongyida.robot.voice.R;
import com.yongyida.robot.voice.bean.BaseInfo;
import com.yongyida.robot.voice.frame.newflytek.bean.Answer;
import com.yongyida.robot.voice.frame.newflytek.bean.Cw;
import com.yongyida.robot.voice.frame.newflytek.bean.MoveInfo;
import com.yongyida.robot.voice.frame.newflytek.bean.QuestionInfo;
import com.yongyida.robot.voice.frame.newflytek.bean.Semantic;
import com.yongyida.robot.voice.frame.newflytek.bean.Slots;
import com.yongyida.robot.voice.frame.newflytek.bean.VoiceOffline;
import com.yongyida.robot.voice.frame.newflytek.bean.Ws;
import com.yongyida.robot.voice.utils.FileUtil;
import com.yongyida.robot.voice.utils.JsonParserUtils;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.MediaPlayUtils;
import com.yongyida.robot.voice.utils.SharePreferenceUtils;
import com.yongyida.robot.voice.utils.ShowToast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruiqianqi on 2016/8/10 0010.
 */
public class VoiceRecognizer implements RecognizerListener {
    /** 类日志标志 */
    private static String TAG = VoiceRecognizer.class.getSimpleName();
    /** 语法匹配 */
    private SpeechRecognizer speechRecognizer;
    /** 全局上下文 */
    private Context mContext;

    /** grammarid的KEY */
    public static final String KEY_GRAMMAR_ID = "grammar_id";
    /** 云端语法类型 */
    private final String GRAMMAR_TYPE_ABNF = "abnf";
    /** 本地语法类型 */
    private final String GRAMMAR_TYPE_BNF = "bnf";
    /** 云端引擎还是本地引擎 */
    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    /** 返回结果格式，支持：xml,json */
    private String mResultType = "json";

    /** 本地语法 */
    private String localGrammar;
    /** 本地联系人 */
    private String localContact;

    /** 云端语法 */
    private String cloudGrammar;

    /**
     * 选择语法文本
     * projection 离线命令不能用这个词，会报错。
     *
     */
    public static final String GRAMMAR = "commands";

    /** 语音数据来源 */
    public enum DATA_SOURCE_TYPE{
        TYPE_RECORD,
        TYPE_RAW_DATA,
    }
    /** 语音数据来源 */
    private final DATA_SOURCE_TYPE dataSourceType = DATA_SOURCE_TYPE.TYPE_RECORD;

    /** 语法构建路径 */
    private String SAVE_GRAMMAR_PATH = Environment.getExternalStorageDirectory()+"/msc/grammar_service";
    /** 音频保存路径 */
    private String SAVE_AUDIO_PATH = Environment.getExternalStorageDirectory()+"/msc/asr.wav";
    /** 门限值 */
    private int THRESHOLD = 20;

    /** 语义理解的回调 */
    private SpeechUnderstanderListener understanderListener;

    /** 离线聊天语料加载器 */
    private Properties properties;
    /** 音乐播放器 */
    private MyMediaPlayer mMediaPlayer = null;
    /** SLOT对应的ID集合 */
    private Map<String, Integer> mSlotIdMaps = null;

    /** 离线问题列表 */
    private String[] mQuestions;
    /** 离线答案列表 */
    private String[] mAnswers;
    /** 语法名称集合 */
    private ConcurrentMap<String, String> mGrammarMap = null;
    /** 要识别的语法值 */
    private volatile String  mGrammarValue = "";
    //private String grammarPath;
    /** 开始语法创建线程 */
    //private MyBNFCreator bnfCreator;

    //private static String project = SystemProperties.get("ro.product.device", "y20a_dev"); // For MT8735
    private static String project = SystemProperties.get("ro.yongyida.robot_raw_model", "y20c_dev");   // For MT8163
    protected MediaPlayUtils player = MediaPlayUtils.getInstance();

    protected VoiceRecognizer(Context mContext, SpeechRecognizer speechRecognizer){
        this.mContext = mContext;
        this.speechRecognizer = speechRecognizer;
        this.mMediaPlayer = new MyMediaPlayer(mContext);
        init();
    }

    /**
     * 类初始化
     */
    private void init(){
        localGrammar = FileUtil.readFile(mContext, "offline/"+GRAMMAR+".bnf", "utf-8");
        cloudGrammar = FileUtil.readFile(mContext, "offline/move.abnf", "utf-8");

        //加载正则匹配
        CmdRegex.getInstance(mContext).loadRegex(GRAMMAR);
        //loadAnswer();
        contactObserver();

        mGrammarMap = new ConcurrentHashMap<>();
        // 离线问题列表
        mQuestions = mContext.getResources().getStringArray(R.array.questions);
        // 离线答案列表
        mAnswers = mContext.getResources().getStringArray(R.array.answers);
        // 语法文件构造路径
        //grammarPath = mContext.getFileStreamPath("dynamic.bnf").getAbsolutePath();
        // 构造语法对象
        //bnfCreator = new MyBNFCreator(this, mQuestions, mAnswers, grammarPath);
        //bnfCreator.run();

        mSlotIdMaps = new HashMap<>();
        setLocalParams();
    }

    /**
     * 观察联系人数据库
     */
    private void contactObserver(){
        //观察联系人数据库
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        mContext.getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                StringBuilder sb = new StringBuilder();

                Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
                ContentResolver resolver = mContext.getContentResolver();
                Cursor cursor = resolver.query(uriContact, new String[]{ContactsContract.Contacts._ID}, null, null, null);
                while (cursor.moveToNext()) {
                    int contractID = cursor.getInt(0);
                    uriContact = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
                    Cursor cursor1 = resolver.query(uriContact, new String[]{"mimetype", "data1", "data2"}, null, null, null);
                    while (cursor1.moveToNext()) {
                        String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
                        String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
                        if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
                            sb.append(data1+"\n");
                        } else if ("vnd.android.cursor.item/email_v2".equals(mimeType)) { //邮箱

                        } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机

                        }
                    }
                    cursor1.close();
                }
                cursor.close();

                //更新本地词典move.bnf的contact字段【好像只有contact可以更新】
                speechRecognizer.updateLexicon("contact", sb.toString(), new LexiconListener() {
                    @Override
                    public void onLexiconUpdated(String s, SpeechError speechError) {
                        if(speechError == null){
                            LogUtils.showLogError(TAG, "联系人更新成功"+s);
                        }else{
                            LogUtils.showLogError(TAG, "联系人更新失败,错误码："+speechError.getErrorCode());
                        }
                    }
                });

                //原始方法
                updateContact(sb.toString());

            }
        });
    }

    /**
     * 加载离线语料
     */
    private void loadAnswer(){
        properties = new Properties();
        try {
            InputStream is = FileUtil.getFileStream(mContext, "offline/move.answer");
            if(is!=null) {
                properties.load(is);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建本地语法
     */
    public void setLocalParams(){
        mEngineType = SpeechConstant.TYPE_LOCAL;
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 启动离线引擎
        speechRecognizer.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_ASR);
        // 设置引擎类型
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, mResultType);
        // 设置识别的门限值
        speechRecognizer.setParameter(SpeechConstant.ASR_THRESHOLD, THRESHOLD+"");

        // 设置语法构建路径
        speechRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, SAVE_GRAMMAR_PATH);
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 使用8k音频的时候请解开注释
        // speechRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        // 设置【资源路径】
        speechRecognizer.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        //speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        // 设置音频保存路径
        //speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, SAVE_AUDIO_PATH);

        int ret = speechRecognizer.buildGrammar(GRAMMAR_TYPE_BNF, localGrammar, new MyGrammarListener());
        if(ret != ErrorCode.SUCCESS){
            LogUtils.showLogError(TAG, "本地语法构建失败,错误码：" + ret);
        }else {
            //new Thread(bnfCreator).start();
        }

    }

    /**
     * 云端语法引擎
     */
    public void setCloudParams(){
        mEngineType = SpeechConstant.TYPE_CLOUD;
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 指定引擎类型
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, mResultType);

        int ret = speechRecognizer.buildGrammar(GRAMMAR_TYPE_ABNF, cloudGrammar, new MyGrammarListener());
        if(ret != ErrorCode.SUCCESS){
            LogUtils.showLogError(TAG, "云端语法构建失败,错误码：" + ret);
        }

    }

    /**
     * 设置使用混合引擎
     */
    private void setMixParams(){

        if(mEngineType== SpeechConstant.TYPE_LOCAL) {
            // 清空参数
            //speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
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
            // 优先使用本地匹配
            speechRecognizer.setParameter("local_prior", "1");
            //设置识别的门限值
            speechRecognizer.setParameter(SpeechConstant.MIXED_THRESHOLD, THRESHOLD+"");

            // 在使用离线语法时，需要构建语法并保存到本地，在构建和使用语法时，都需要设置语法的构 建目录。
            // 与识别资源路径值不一样的是，语法的路径值，【不需要通过generateResourcePath生成SDK要求的格式。】
            speechRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, SAVE_GRAMMAR_PATH);
            // 设置资源路径【通过generateResourcePath生成SDK要求的格式】
            speechRecognizer.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
            // 设置返回结果格式
            speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, mResultType);

            String grammarId = SharePreferenceUtils.getInstance(mContext).getString(KEY_GRAMMAR_ID, GRAMMAR);
            LogUtils.showLogDebug(TAG, "grammarId: " + grammarId);
            // 设置本地识别使用的语法id
            speechRecognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, grammarId);

            // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
            // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
            //speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            // 设置音频保存路径
            //speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, SAVE_AUDIO_PATH);
        }else {

        }

        //代理类已设置过了
        //设置语义理解参数
        //setUnderParams();
    }


    /**
     * 设置语义理解参数
     * @return
     */
    private void setUnderParams(){
        // 设置语言
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置普通话
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 服务器为不同的应用领域，定制了不同的听写匹配引擎，使用对应的领域能获取更 高的匹配率
        // { "iat", "video", "poi", "music" }
        speechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "10000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "500");
        // 采样率
        speechRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        // 设置标点符号，默认：1（有标点）
        speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        //speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 录音存放路径
        //speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, SAVE_AUDIO_PATH);

    }

    /**
     * 设置使用录音
     */
    private void setRecorderParams(){
        // 使用录音
        speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, null);
    }

    /**
     * 设置使用外部音频源
     */
    private void setWriteAudioParams(){
        // 使用外部音频
        speechRecognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
    }

    /**
     * 取到相应的参数
     * @param key
     * @return
     */
    public String getParameter(String key){
        if(speechRecognizer != null){
            return speechRecognizer.getParameter(key);
        }
        return null;
    }

    /**
     * 获取识别资源路径【通过generateResourcePath生成SDK要求的格式】
     */
    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        // 识别8k资源-使用8k的时候请解开注释
        //tempBuffer.append(";");
        //tempBuffer.append(ResourceUtil.generateResourcePath(mContext, RESOURCE_TYPE.assets, "asr/common_8k.jet"));
        return tempBuffer.toString();
    }

    /**
     * 开始识别
     */
    public int start(){
        if(speechRecognizer==null){
            return -1;
        }
        //代理类已设置过了
        stop();

        // 设置语义理解参数
        setUnderParams();

        //setMixParams();
        //String grammarId = SharePreferenceUtils.getInstance(mContext).getString(KEY_GRAMMAR_ID, GRAMMAR);
        //speechRecognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, grammarId);
        // 设置本地识别使用的语法id
        speechRecognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, mGrammarValue);
        LogUtils.showLogError(TAG, "GrammarValue：" + mGrammarValue);

        if(dataSourceType==DATA_SOURCE_TYPE.TYPE_RAW_DATA){
            setWriteAudioParams();
        }else {
            setRecorderParams();
        }

        int ret = speechRecognizer.startListening(this);
        if (ret != ErrorCode.SUCCESS) {
            LogUtils.showLogError(TAG, "开启识别失败,错误码: " + ret);
        }
        return ret;
    }

    /**
     * 停止识别
     */
    public void stop(){
        if(speechRecognizer.isListening()){
            //停止录音，然后上传
            speechRecognizer.stopListening();
        }
        //取消本次录音会话功能
        speechRecognizer.cancel();

    }

    /**
     * 加载原始录音
     * @param datas
     */
    public void loadAudio(byte[] datas){
        if(speechRecognizer==null){
            return;
        }

        stop();
        setMixParams();

        int ret = speechRecognizer.startListening(this);
        if(ret != ErrorCode.SUCCESS){
            LogUtils.showLogError(TAG, "开启识别失败,错误码: " + ret);
        }else {
            if (speechRecognizer.isListening()) {
                speechRecognizer.writeAudio(datas, 0, datas.length);
            }
        }

    }

    @Override
    public void onVolumeChanged(int i, byte[] bytes) {
        understanderListener.onVolumeChanged(i, bytes);
    }

    @Override
    public void onBeginOfSpeech() {
        understanderListener.onBeginOfSpeech();
    }

    @Override
    public void onEndOfSpeech() {
        understanderListener.onEndOfSpeech();
    }

    @Override
    public void onResult(RecognizerResult recognizerResult, boolean b) {
        if(recognizerResult!=null) {
            parseResult(recognizerResult.getResultString());
        }
    }

    @Override
    public void onError(SpeechError speechError) {
        if(speechError!=null){
            LogUtils.showLogError(TAG, "onError: "+speechError.getErrorDescription()+speechError.getErrorCode());
        }
        //understanderListener.onError(speechError);

        //ShowToast.getInstance(mContext).show("无匹配结果，请再说一次");
        //player.playMusic(mContext, "wait_9.mp3");
        //
        start();

    }

    @Override
    public void onEvent(int eventType, int i1, int i2, Bundle bundle) {
        // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        // 若使用本地能力，会话id为null
        if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            String sid = bundle.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            LogUtils.showLogDebug(TAG, "sessionId =" + sid);
        }
        understanderListener.onEvent(eventType, i1, i2, bundle);
    }

    /**
     * 解析匹配结果
     * @param result
     */
    public void parseResult(String result){
        LogUtils.showLogDebug(TAG, "result: "+result);

        mSlotIdMaps.clear();
        // 组成命令的命令词语法变量名称
        StringBuffer slots = new StringBuffer();
        // 组成命令的命令词编号
        StringBuffer codes = new StringBuffer();
        // 生成的命令
        StringBuffer words = new StringBuffer();

        VoiceOffline voice = JsonParserUtils.parseResult(result, VoiceOffline.class);
        if(voice != null){
            List<Ws> wsList = voice.getWs();
            if(wsList!=null) {
                for (int i = 0; i < wsList.size(); i++) {
                    Ws ws = wsList.get(i);

                    slots.append(ws.getSlot());

                    List<Cw> cwList = ws.getCw();
                    if(cwList!=null){
                        int sc = -1;
                        String w = null;
                        int id = 0;
                        for (int k = 0; k < cwList.size() ; k++) {
                            Cw cw = cwList.get(k);
                            if(cw.getSc()>sc){
                                sc = cw.getSc();
                                w = cw.getW();
                                id = cw.getId();
                                //如果是云端的话，分数是直接给的，不用一个个再计算
                                if(mEngineType== SpeechConstant.TYPE_CLOUD){
                                    voice.setSc(sc);
                                }

                            }
                        }
                        codes.append(id+"|");
                        words.append(w);
                        mSlotIdMaps.put(ws.getSlot(), id);
                    }
                }
            }

            voice.setSerialId(codes.toString());
            voice.setSerialWord(words.toString());
            voice.setSerialSlot(slots.toString());

            if(mEngineType==SpeechConstant.TYPE_LOCAL) {
                if (voice.getSc() >= THRESHOLD) {
                    parseOffline(voice);
                } else {
                    if(voice.getSc()==-1) {
                        parseOnline(result);
                    }else {
                        playWords("请联网体验更多功能");
                    }
                }
            }else {
                result = words.toString();
                if(result.contains("nomatch")){
                    LogUtils.showLogError(TAG, "匹配结果有问题[云端]");
                }else {
                    if (voice.getSc() >= THRESHOLD) {
                        LogUtils.showLogDebug(TAG, words.toString());
                    } else {
                        LogUtils.showLogError(TAG, "匹配结果有问题[云端]");
                    }
                }
            }
        } else {
            //当成在线的处理
            parseOnline(result);
        }
    }

    /**
     * 播放指定语音
     * @param words
     */
    private void playWords(String words){
        /*
        QuestionInfo questionInfo = new QuestionInfo();
        Answer answer = new Answer();
        questionInfo.setAnswer(answer);

        questionInfo.setServiceType("openQA");
        questionInfo.setOperation("ANSWER");
        questionInfo.setText(words);
        questionInfo.setSuccess(0);

        answer.setType("T");
        Gson gson = new Gson();
        String json = null;
        try {
            //answer.setText(properties.getProperty(words));
            json = gson.toJson(questionInfo);
        }catch (Exception e){

        }
        */
        understanderListener.onResult(new UnderstanderResult(""));
        ShowToast.getInstance(mContext).show(words+"");
        return;
    }



    /**
     * 解析出离线命令词了，开始取对应的指令
     * service$operation$other
     * @param voiceOffline   出来的结果
     */
    private void parseOffline(VoiceOffline voiceOffline){
        
        if(TextUtils.isEmpty(voiceOffline.getSerialSlot())){
            playWords("离线SLOT异常");
            return;
        }

        // 先交给标签值处理
        if(!parseSerialSlot(voiceOffline)) {

            // 没有处理的话，再由正则匹配处理
            String serialWord = voiceOffline.getSerialWord();
            if (TextUtils.isEmpty(serialWord)) {
                playWords("离线WORD异常");
                return;
            }
            parseSerialWord(serialWord);

        }

    }

    /**
     * 通过槽来进行指令判断
     * @param voiceOffline
     */
    private boolean parseSerialSlot(VoiceOffline voiceOffline){

        String serialSlot = voiceOffline.getSerialSlot();
        Gson gson = new Gson();
        String json = null;
        LogUtils.showLogError(TAG, "serialSlot: "+serialSlot);
        // 唱歌
        if(serialSlot.contains("<music>")){
            if(project.equals("y20a_dev") || project.equals("y20c_dev")) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                bundle.putString("offline", "offline");
                MyAppUtils.startApp(mContext, "com.yongyida.robot.resourcemanager", "com.yongyida.robot.resourcemanager.activity.MusicAndImageActivity", bundle);
                playWords(voiceOffline.getSerialWord());

            }else if(project.equals("y50bpro_dev")) {
                if (voiceOffline.getSerialWord().contains("戏曲") ||
                        voiceOffline.getSerialWord().contains("舞曲") ||
                        voiceOffline.getSerialWord().contains("儿歌")) {
                        start();
                } else{
                    MoveInfo moveInfo = new MoveInfo();
                    Semantic semantic = new Semantic();
                    Slots slots = new Slots();
                    semantic.setSlots(slots);
                    moveInfo.setSemantic(semantic);

                    moveInfo.setServiceType("music");
                    moveInfo.setText(voiceOffline.getSerialWord());
                    moveInfo.setSuccess(0);
                    slots.setmDefault(voiceOffline.getSerialWord());
                    json = gson.toJson(moveInfo);
                    understanderListener.onResult(new UnderstanderResult(json));
                }
            }
            return true;
        }
        // 跳舞
        else if(serialSlot.contains("<dance>")){
            if(project.equals("y20a_dev")) {
                start();
                return true;
            }else if(project.equals("y20c_dev")){
                start();
                return true;
            }else if(project.equals("y50bpro_dev")){

            }

            MoveInfo moveInfo = new MoveInfo();
            moveInfo.setServiceType("dance");
            moveInfo.setText("");
            moveInfo.setSuccess(0);
            json = gson.toJson(moveInfo);
            understanderListener.onResult(new UnderstanderResult(json));
            ShowToast.getInstance(mContext).show(voiceOffline.getSerialWord());

            return true;
        }
        // 照相
        else if(serialSlot.contains("<picture>") || serialSlot.contains("<camera>")){
            MoveInfo moveInfo = new MoveInfo();
            moveInfo.setServiceType("camera");
            moveInfo.setText(voiceOffline.getSerialWord());
            moveInfo.setSuccess(0);
            json = gson.toJson(moveInfo);
            understanderListener.onResult(new UnderstanderResult(json));
            return true;
        }
        // 打电话
        else if(serialSlot.contains("<phone>")){
            if(serialSlot.contains("给")){

                if(project.equals("y20a_dev")) {

                }else if(project.equals("y20c_dev")){
                    start();
                    return true;
                }else if(project.equals("y50bpro_dev")){

                }

                // 捕获到的名字 或 电话号码
                CmdRegex.getInstance(mContext).filter(voiceOffline.getSerialWord());
                String name = CmdRegex.getInstance(mContext).filterCatch(voiceOffline.getSerialWord());
                MoveInfo moveInfo = new MoveInfo();
                Semantic semantic = new Semantic();
                Slots slots = new Slots();
                semantic.setSlots(slots);
                moveInfo.setSemantic(semantic);

                moveInfo.setServiceType("telephone");
                moveInfo.setOperation("CALL");
                voiceOffline.setSerialWord(voiceOffline.getSerialWord().replace("，", ""));
                moveInfo.setText(voiceOffline.getSerialWord());
                moveInfo.setSuccess(0);

                LogUtils.showLogError(TAG, "name = " + name);
                Pattern pat = Pattern.compile("\\d+");
                Matcher mat = pat.matcher(name);
                if(mat.matches()){
                    slots.setCode(name);
                }else {
                    slots.setName(name);
                }
                json = gson.toJson(moveInfo);
                understanderListener.onResult(new UnderstanderResult(json));

            }else {
                // 打开电话
                if(project.equals("y20a_dev")) {

                }else if(project.equals("y20c_dev")){
                    start();
                    return true;
                }else if(project.equals("y50bpro_dev")){

                }
                MyAppUtils.openApp(mContext, "com.android.dialer");
                playWords(voiceOffline.getSerialWord());

            }
            return true;
        }
        // 空调、电视、投影
        else if(serialSlot.contains("<household>")){
            // 打开
            int id = mSlotIdMaps.get("<household>");

            // 投影
            if(id==3){

                if(project.equals("y20a_dev")) {

                }else if(project.equals("y20c_dev")){
                    start();
                    return true;
                }else if(project.equals("y50bpro_dev")){
                    start();
                    return true;
                }

                if(serialSlot.contains("<turnOn>")){
                    int power = HdmiCtl.HdmiDppPowerStatus();
                    if (power == 0) {
                        HdmiCtl.HdmiDppPowerON();
                    } else {

                    }
                }else {
                    int power = HdmiCtl.HdmiDppPowerStatus();
                    if (power == 1) {
                        HdmiCtl.HdmiDppPowerOFF();
                    } else {

                    }
                }
                playWords(voiceOffline.getSerialWord());

            }else {
                if(project.equals("y20a_dev")) {

                }else if(project.equals("y20c_dev")){
                    start();
                    return true;
                }else if(project.equals("y50bpro_dev")){

                }

                MoveInfo moveInfo = new MoveInfo();
                Semantic semantic = new Semantic();
                Slots slots = new Slots();
                semantic.setSlots(slots);
                moveInfo.setSemantic(semantic);

                moveInfo.setServiceType("smarthome");
                moveInfo.setText(voiceOffline.getSerialWord());
                moveInfo.setSuccess(0);
                // 空调
                if(id==1){
                    slots.setDevice("AIR_CONDITIONER");
                }
                // 电视
                else if(id==2){
                    slots.setDevice("TV");
                }
                if(serialSlot.contains("<turnOn>")){
                    slots.setAction("open");
                }else {
                    slots.setAction("stop");
                }
                json = gson.toJson(moveInfo);
                understanderListener.onResult(new UnderstanderResult(json));
            }
            return true;
        }
        // 关机
        else if(serialSlot.contains("<shutdown>")){
            MoveInfo moveInfo = new MoveInfo();
            moveInfo.setServiceType("shutdown");
            moveInfo.setText(voiceOffline.getSerialWord());
            moveInfo.setSuccess(0);
            moveInfo.setOperation("poweroff");
            json = gson.toJson(moveInfo);
            understanderListener.onResult(new UnderstanderResult(json));
            return true;
        }
        // 查看照片
        else if(serialSlot.contains("<photos>")){
            MoveInfo moveInfo = new MoveInfo();
            Semantic semantic = new Semantic();
            Slots slots = new Slots();
            semantic.setSlots(slots);
            moveInfo.setSemantic(semantic);

            moveInfo.setServiceType("photos");
            moveInfo.setOperation("query");
            moveInfo.setText(voiceOffline.getSerialWord());
            moveInfo.setSuccess(0);
            slots.setPhoto("相册");
            json = gson.toJson(moveInfo);
            understanderListener.onResult(new UnderstanderResult(json));
            return true;
        }
        // 多媒体视频
        else if(serialSlot.contains("<medias>")){
            int id = mSlotIdMaps.get("<medias>");
            int pagerIndex = 0;
            switch (id){
                case 1: // 电视剧
                    pagerIndex = 0;
                    break;
                case 2: // 电影
                    pagerIndex = 1;
                    break;
                case 3: // 动漫
                    pagerIndex = 2;
                    break;
                case 4: // 综艺
                    pagerIndex = 3;
                    break;
                case 5: // 体育
                    pagerIndex = 4;
                    break;
                case 6: // 直播
                    pagerIndex = 5;
                    break;
                case 7: // 视频
                    if(project.equals("y20a_dev") || project.equals("y20c_dev")) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        bundle.putString("offline", "offline");
                        MyAppUtils.startApp(mContext, "com.yongyida.robot.resourcemanager", "com.yongyida.robot.resourcemanager.activity.VideoActivity", bundle);
                        playWords(voiceOffline.getSerialWord());
                    }else if(project.equals("y50bpro_dev")){
                        start();
                    }
                    return true;
                default:
                    playWords("离线ID异常");
                    return false;
            }
            Intent resolveIntent = new Intent("action_yydrobot_video_main");
            resolveIntent.addCategory(Intent.CATEGORY_DEFAULT);
            Intent intent = MyAppUtils.openAppIntent(mContext, "com.yongyida.robot.videotutarial", resolveIntent);
            intent.putExtra("pagerIndex", pagerIndex);
            mContext.startActivity(intent);
            playWords(voiceOffline.getSerialWord());
            return true;
        }
        // 多媒体音频
        else if(serialSlot.contains("<arts>")){
            int id = mSlotIdMaps.get("<arts>");
            int pagerIndex = 0;
            switch (id){
                case 1: // 戏曲
                    pagerIndex = 4;
                    break;
                case 2: // 儿歌
                    pagerIndex = 3;
                    break;
                case 3: // 文学
                    pagerIndex = 2;
                    break;
                case 4: // 电台
                    pagerIndex = 1;
                    break;
                default:
                    playWords("离线ID异常");
                    return false;
            }
            Intent intent = MyAppUtils.openAppIntent(mContext, "com.yongyida.robot.artmuseum", null);
            intent.putExtra("index", pagerIndex);
            mContext.startActivity(intent);
            playWords(voiceOffline.getSerialWord());
            return true;
        }
        // 多媒体音频
        else if(serialSlot.contains("音乐")){
            int id = mSlotIdMaps.get("音乐");
            if(id==5){
                Intent intent = MyAppUtils.openAppIntent(mContext, "com.yongyida.robot.artmuseum", null);
                intent.putExtra("index", 0);
                mContext.startActivity(intent);
                playWords(voiceOffline.getSerialWord());
                return true;
            }else {
                playWords("离线ID异常");
                return false;
            }
        }
        // 打开游戏
        else if(serialSlot.contains("<game>")){
            if(project.equals("y20a_dev")){
                MyAppUtils.openApp(mContext, "com.yongyida.robot.gamecenter");
            }
            else if(project.equals("y50bpro_dev")) {
                MyAppUtils.openApp(mContext, "com.yongyida.robot.game");
            }
            else if (project.equals("y20c_dev")){
                MyAppUtils.openApp(mContext, "com.yongyida.robot.game");
            }
            playWords(voiceOffline.getSerialWord());
            return true;
        }
        // 二维码
        else if(serialSlot.contains("<qrCode>")){
            int id = mSlotIdMaps.get("<qrCodeScan>");

            MoveInfo moveInfo = new MoveInfo();
            moveInfo.setServiceType("yydchat");
            moveInfo.setText(voiceOffline.getSerialWord());
            moveInfo.setSuccess(0);
            // 绑定二维码
            if(id==1){
                moveInfo.setOperation("qrcode_bind");
            }
            // 下载二维码
            else {
                moveInfo.setOperation("qrcode_download");
            }
            json = gson.toJson(moveInfo);
            understanderListener.onResult(new UnderstanderResult(json));
            return true;
        }
        // 打开一些设置
        else if(serialSlot.contains("<settings>")){
            int id = mSlotIdMaps.get("<settings>");
            switch (id){
                case 1: // 设置
                    MyAppUtils.openSetting(mContext);
                    break;
                case 2: // 网络设置
                    MyAppUtils.openNetSetting(mContext);
                    break;
                case 3: // WIFI
                    String serialWord = voiceOffline.getSerialWord();
                    if(!TextUtils.isEmpty(serialWord)){
                        if(serialWord.contains("歪发")){
                            voiceOffline.setSerialWord(serialWord.replace("歪发", "WIFI"));
                        }
                    }
                    MyAppUtils.openWifiSetting(mContext);
                    break;
                case 4: // 蓝牙
                    MyAppUtils.openBluetoothSetting(mContext);
                    break;
                case 5: // 关于
                    MyAppUtils.openAboutSetting(mContext);
                    break;
                case 6: // 飞行模式
                    if(project.equals("y20a_dev") || project.equals("y50bpro_dev")) {
                        MyAppUtils.openToolsUI2(mContext, Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                    }else {
                        start();
                        return true;
                    }
                    break;
                case 7: // 网络热点
                    MyAppUtils.openToolsUI(mContext, "com.android.settings.TetherSettings");
                    break;
                case 8: // 显示
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.DisplaySettings");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_DISPLAY_SETTINGS);
                    break;
                case 9: // 提示音
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.SoundSettings");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_SOUND_SETTINGS);
                    break;
                case 10: // 位置信息
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.SecuritySettings");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    break;
                case 11: // 语言
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.LocalePicker");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_LOCALE_SETTINGS);
                    break;
                case 12: // 日期
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.DateTimeSettings");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_DATE_SETTINGS );
                    break;
                case 13: // 出厂设置
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.MasterClear");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_PRIVACY_SETTINGS);
                    break;
                default:
                    playWords("离线ID异常");
                    return false;
            }
            playWords(voiceOffline.getSerialWord());
            return true;
        }
        // 打开一些设置
        else if(serialSlot.contains("<scanSettings>")){
            int id = mSlotIdMaps.get("<scanSettings>");
            switch (id){
                case 14: // 存储状况
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.UsageStats");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_INTERNAL_STORAGE_SETTINGS);
                    break;
                case 15: // 电池电量
                    // MyAppUtils.startBatteryUsage(mContext);
                    MyAppUtils.startBatteryStatus(mContext);
                    break;
                case 16: // 应用列表
                    // MyAppUtils.openToolsUI(mContext, "com.android.settings.ManageApplications");
                    MyAppUtils.openToolsUI2(mContext, Settings.ACTION_APPLICATION_SETTINGS);
                    break;
                default:
                    playWords("离线ID异常");
                    return false;
            }
            playWords(voiceOffline.getSerialWord());
            return true;
        }
        // 打开工具类
        else if(serialSlot.contains("<tools>")){
            int id = mSlotIdMaps.get("<tools>");
            switch (id){
                case 1: // 下载
                    MyAppUtils.openApp(mContext, "com.android.providers.downloads.ui");
                    break;
                case 2: // 信息
                    if(project.equals("y20a_dev") || project.equals("y50bpro_dev")) {
                        MyAppUtils.openApp(mContext, "com.android.mms");
                    }else {
                        return true;
                    }
                    break;
                case 3: // 录音机
                    MyAppUtils.openApp(mContext, "com.android.soundrecorder");
                    break;
                case 4: // 文件管理
                    //MyAppUtils.openApp(mContext, "com.mediatek.filemanager");
                    MyAppUtils.openApp(mContext, "com.yongyida.robot.resourcemanager");
                    break;
                case 5: // 日历
                    MyAppUtils.openApp(mContext, "com.android.calendar");
                    break;
                case 6: // 时钟
                    MyAppUtils.openApp(mContext, "com.android.deskclock");
                    break;
                case 7: // 浏览器
                    MyAppUtils.openApp(mContext, "com.android.browser");
                    break;
                case 8: // 通讯录
                    if(project.equals("y20a_dev") || project.equals("y50bpro_dev")) {
                        MyAppUtils.openApp(mContext, "com.android.contacts");
                    }else {
                        start();
                        return true;
                    }
                    break;
                default:
                    playWords("离线ID异常");
                    return false;
            }
            playWords(voiceOffline.getSerialWord());
            return true;
        }
        // 打开Launcher
        else if(serialSlot.contains("<launcher>")){
            int id = mSlotIdMaps.get("<launcher>");
            switch (id){
                case 1: // 应用
                    if(project.equals("y20a_dev") || project.equals("y20c_dev") || project.equals("y50bpro_dev")) {
                        MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.activity.sub.UserAppActivity");
                    }
                    break;
                case 2: // 影视
                    if(project.equals("y20a_dev") || project.equals("y20c_dev")|| project.equals("y50bpro_dev")) {
                        MyAppUtils.openApp(mContext, "com.yongyida.robot.videotutarial");
                    }
                    break;
                case 3: // 文艺馆
                    if(project.equals("y20a_dev") || project.equals("y20c_dev")|| project.equals("y50bpro_dev")) {
                        MyAppUtils.openApp(mContext, "com.yongyida.robot.artmuseum");
                    }
                    break;
                case 4: // 工具
                    if(project.equals("y20a_dev") || project.equals("y20c_dev")|| project.equals("y50bpro_dev")) {
                        MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.activity.sub.ToolsAppActivity");
                    }
                    break;
                case 5: // 投影设置
                    if(project.equals("y20a_dev")) {
                        MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.projector.ProjectorActivity");
                    }else {
                        start();
                        return true;
                    }
                    break;
                case 6: // 学习
                    if(project.equals("y20a_dev") || project.equals("y20c_dev")|| project.equals("y50bpro_dev")) {
                        MyAppUtils.startApp(mContext, "com.yongyida.robot.launcher2", "com.yongyida.robot.launcher2.activity.sub.EducationActivity");
                    }
                    break;
                case 7: // 遥控器
                    if(project.equals("y50bpro_dev")) {
                        MyAppUtils.openApp(mContext, "com.yongyida.robot.irremote");
                    }else {
                        start();
                        return true;
                    }
                    break;
                default:
                    playWords("离线ID异常");
                    return false;
            }
            playWords(voiceOffline.getSerialWord());
            return true;
        }
        // 音量调节
        else if(serialSlot.contains("<volume>")){
            return false;
        }
        // 自定义SLOT
        else if(serialSlot.contains("<updateWords>")){
            int serialId = mSlotIdMaps.get("<updateWords>");
            String[] answers = mAnswers[serialId-1].split("\\|");
            int index = (int) (Math.random() * answers.length);

            QuestionInfo questionInfo = new QuestionInfo();
            Answer answer = new Answer();
            questionInfo.setAnswer(answer);

            questionInfo.setServiceType("openQA");
            questionInfo.setOperation("ANSWER");
            questionInfo.setText(voiceOffline.getSerialWord());
            questionInfo.setSuccess(0);

            answer.setType("T");
            answer.setText(answers[index]);
            try {
                json = gson.toJson(questionInfo);
            }catch (Exception e){

            }
            if(!TextUtils.isEmpty(json)) {
                understanderListener.onResult(new UnderstanderResult(json));
            }
            return true;
        }

        return false;
    }


    /**
     * 通过槽来进行指令判断
     * @param serialWord
     */
    private void parseSerialWord(String serialWord){
        String order = CmdRegex.getInstance(mContext).filter(serialWord);
        if(TextUtils.isEmpty(order)){
            playWords("离线命令词没有匹配到指令");
            return;
        }

        String[] words = order.split("\\$");
        if(words!=null && words.length > 0){

            Gson gson = new Gson();
            String json = null;

            if(words[0].equals("sound")){
                MoveInfo moveInfo = new MoveInfo();
                Semantic semantic = new Semantic();
                Slots slots = new Slots();
                semantic.setSlots(slots);
                moveInfo.setSemantic(semantic);

                moveInfo.setServiceType(words[0]);
                moveInfo.setOperation(words[1]);
                moveInfo.setText(serialWord);
                moveInfo.setSuccess(0);

                slots.setVolume("音量");
                json = gson.toJson(moveInfo);
                understanderListener.onResult(new UnderstanderResult(json));
                return;
            }else {

            }
        }
        playWords("离线命令词解析出现异常");
    }


    /**
     * 当成语义理解来处理
     * @param result
     */
    private void parseOnline(String result){
        LogUtils.showLogError(TAG, "parseOnline: "+result);
        BaseInfo type = JsonParserUtils.parseResult(result, BaseInfo.class);
        if(type==null){
            return;
        } else {
            understanderListener.onResult(new UnderstanderResult(result));
        }
    }

    public void setUnderstanderListener(SpeechUnderstanderListener understanderListener) {
        this.understanderListener = understanderListener;
    }

    /**
     * 用代码打开或关闭wifi
     * @param mContext
     * @param isEnable
     */
    public void setWifi(Context mContext, boolean isEnable) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        //开启wifi
        if (isEnable) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        }
        //关闭wifi
        else {
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    /**
     * 构建语法监听器。
     */
    private class MyGrammarListener implements GrammarListener {

        @Override
        public void onBuildFinish(String grammarId, SpeechError speechError) {
            if(speechError == null){

                LogUtils.showLogDebug(TAG, "语法构建成功：" + grammarId);

                SharePreferenceUtils.getInstance(mContext).putString(KEY_GRAMMAR_ID, grammarId);

                if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
                    // 设置云端识别使用的语法id
                    speechRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, grammarId);
                    // 上传热词到云端词典
                    updateHotWords();

                }else {
                    // 设置语法名称【会不会是更新联系人的时候用的】
                    speechRecognizer.setParameter(SpeechConstant.GRAMMAR_LIST, grammarId);
                    // 更新联系人
                    updateContact();

                    // 放入集合
                    mGrammarMap.put(grammarId, grammarId);
                    StringBuffer sb = new StringBuffer();
                    for (String value : mGrammarMap.values()) {
                        sb.append(";"+value);
                    }
                    mGrammarValue = sb.substring(1);

                    //loadOfflineGrammar(grammarPath);
                }
            }else{
                LogUtils.showLogError(TAG, "语法构建失败,错误码：" + speechError.getErrorCode());
            }
        }
    }

    /**
     * 更新本地联系人词典【离线的】不需要查询
     * "contact"："AA\nBB\nCC\n"
     * @param contactInfoS
     */
    public void updateContact(String contactInfoS){

        String grammarId = SharePreferenceUtils.getInstance(mContext).getString(KEY_GRAMMAR_ID, GRAMMAR);
        // 设置语法名称【指定更新词典的时候更新哪个语法】
        speechRecognizer.setParameter(SpeechConstant.GRAMMAR_LIST, grammarId);

        speechRecognizer.updateLexicon("contact", contactInfoS, new LexiconListener(){
            @Override
            public void onLexiconUpdated(String s, SpeechError speechError) {
                if(speechError == null){
                    LogUtils.showLogError(TAG, "联系人更新成功");
                }else{
                    LogUtils.showLogError(TAG, "联系人更新失败,错误码："+speechError.getErrorCode());
                }
            }
        });
    }

    /**
     * 更新本地联系人词典【离线的】
     * "contact"："AA\nBB\nCC\n"
     */
    private void updateContact(){
        // 获取联系人，更新本地词典时使用
        ContactManager mgr = ContactManager.createManager(mContext, new ContactManager.ContactListener(){
            @Override
            public void onContactQueryFinish(String contactInfos, boolean b) {
                localContact = contactInfos;

                //更新本地词典move.bnf的contact字段【好像只有contact可以更新】
                speechRecognizer.updateLexicon("contact", localContact, new LexiconListener(){
                    @Override
                    public void onLexiconUpdated(String s, SpeechError speechError) {
                        if(speechError == null){
                            LogUtils.showLogDebug(TAG, "词典联系人更新成功");

                            //new Thread(bnfCreator).start();

                        }else{
                            LogUtils.showLogError(TAG, "词典联系人更新失败,错误码："+speechError.getErrorCode());
                        }
                    }
                });

                /*
                String myWords = "中国\n美国\n英国\n";
                speechRecognizer.updateLexicon("myWords", myWords, new LexiconListener() {
                    @Override
                    public void onLexiconUpdated(String s, SpeechError speechError) {
                        if(speechError == null){
                            showTip("词典国家更新成功");
                        }else{
                            showTip("词典国家更新失败,错误码："+speechError.getErrorCode());
                        }
                    }
                });
                */

            }
        });
        // 异步查询联系人名 异步查询，返回查询结果，不会阻塞调用线程
        mgr.asyncQueryAllContactsName();
    }

    /**
     * 开放语义中，更新热词，更新了怎么使用呢
     */
    private void updateHotWords(){

    }

    /***********************************动态加载离线语法*******************************************/
    /**
     * 动态加载离线语法【仅仅是加载】
     * @param grammarPath
     */
    public void loadOfflineGrammar(String grammarPath){
        LogUtils.showLogDebug(TAG, "grammarPath: "+grammarPath);
        StringBuffer sb = null;
        try {
            sb = FileUtil.readStreamToStrBuf(new FileInputStream(grammarPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        LogUtils.showLogDebug(TAG, "length: "+sb);

        // 设置文本编码格式
        speechRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");

        // 加载离线语法
        int ret = speechRecognizer.buildGrammar(GRAMMAR_TYPE_BNF, sb.toString(), new GrammarListener(){
            @Override
            public void onBuildFinish(String grammarId, SpeechError speechError) {
                if(null==speechError){

                    //放入集合
                    mGrammarMap.put(grammarId, grammarId);

                    StringBuffer sb = new StringBuffer();
                    for (String value : mGrammarMap.values()) {
                        sb.append(";"+value);
                    }
                    mGrammarValue = sb.substring(1);

                    LogUtils.showLogDebug(TAG, "动态MSC语法构建成功："+grammarId);
                }else{

                    LogUtils.showLogDebug(TAG, "动态MSC语法构建失败,错误码："+speechError.getErrorCode());
                }
            }
        });
    }
}
