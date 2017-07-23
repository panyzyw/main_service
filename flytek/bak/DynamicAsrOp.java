package zcclres;

import android.database.sqlite.SQLiteException;

import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.mind.voice.impl.VoiceRecognizer;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

import static zccl.com.ruiqianqi.mind.voice.BuildConfig.IS_TEST;

/**
 * Created by ruiqianqi on 2016/10/12 0012.
 */

public class DynamicAsrOp {

    private static final String TAG = DynamicAsrOp.class.getSimpleName();

    /** 要构建的脚本名字 */
    public static final String BNF_NAME = "question";
    /** 数据库里面的字符串分隔符 */
    public static final String SPLIT_STR = "\\|";
    private static String BNF_HEAD = "#BNF+IAT 1.0 UTF-8;\n" + "!grammar "+BNF_NAME+";\n";
    private static String SLOT1 = "!slot ";
    private static String SLOT2 = "<sx";
    private static String SLOT3 = ">;\n";
    private static String SLOT4 = "!start <openStart>;\n\n";

    private static String ASR1 = "<openStart>:\n";
    private static String ASR2 = "<sx";
    private static String ASR3 = ">|\n";
    private static String ASR4 = ">;\n\n";

    private static String WORD1 = "<sx";
    private static String WORD2 = ">:";
    private static String WORD3 = "!id(";
    private static String WORD4 = ");\n";
    private static String WORD5 = ")|\n";

    /** 语音识别对象 */
    private VoiceRecognizer mVoiceRecognizer;
    /** 数据库操作工具 */
    private MyDbFlow mMyDbFlow;
    /** 离线场景列表 */
    private String[] mScenes;
    /** 离线问题列表 */
    private String[] mQuestions;
    /** 离线答案列表 */
    private String[] mAnswers;

    private Subscription subscriptionCreator;
    private Subscription subscriptionUpdate;

    public DynamicAsrOp(VoiceRecognizer voiceRecognizer){
        this.mVoiceRecognizer = voiceRecognizer;
        this.mMyDbFlow = MyDbFlow.getInstance();

        init();
    }

    public DynamicAsrOp(VoiceRecognizer voiceRecognizer, String[] mScenes, String[] mQuestions, String[] mAnswers){
        this.mVoiceRecognizer = voiceRecognizer;
        this.mMyDbFlow = MyDbFlow.getInstance();
        this.mScenes = mScenes;
        this.mQuestions = mQuestions;
        this.mAnswers = mAnswers;

        init();
    }

    /**
     * 初始化
     */
    private void init(){

        //测试时用的
        if(IS_TEST) {
            MyDbFlow.getInstance().deleteOpenQAAll();
        }

        addQASome(new MyDbFlow.DbCallback() {
            @Override
            public void OnSuccess() {
                LogUtils.e(TAG, "addQASome Success");
            }

            @Override
            public void OnFailure(Throwable error) {
                LogUtils.e(TAG, "addQASome Failure");
            }
        });

    }

    /****************************************自身的参数设置***************************************/
    /**
     * 设置初始化问题
     * @param mQuestions
     */
    public void setQuestions(String[] mQuestions) {
        this.mQuestions = mQuestions;
    }

    /**
     * 设置初始化答案
     * @param mAnswers
     */
    public void setAnswers(String[] mAnswers) {
        this.mAnswers = mAnswers;
    }

    /*************************************数据库操作方法*******************************************/
    /**
     * 初始化一些离线命令词
     * android.database.sqlite.SQLiteConstraintException: UNIQUE constraint failed
     */
    public void addQASome(MyDbFlow.DbCallback dbCallback){
        if(mScenes!=null && mQuestions != null && mAnswers != null){
            List<OpenQA> openQAList = new ArrayList<>();
            for (int i = 0; i < mQuestions.length; i++) {
                OpenQA openQA = new OpenQA();
                openQA.scene = "问答"+(i+1);
                openQA.question = mQuestions[i];
                openQA.answer = mAnswers[i];
                openQAList.add(openQA);

                LogUtils.e(TAG, openQA.question);
            }
            try {
                mMyDbFlow.insertQASome(openQAList, dbCallback);
            }catch (SQLiteException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 增加一条
     * @param openQA
     * @param openQA
     * @return
     */
    public void addQA(OpenQA openQA, MyDbFlow.DbCallback dbCallback){
        try {
            mMyDbFlow.insertAsyncOpenQA(openQA, dbCallback);
        }catch (SQLiteException e){
            if(dbCallback!=null){
                dbCallback.OnFailure(e);
            }
        }
    }

    /**
     * 删除一条
     * @param scene
     */
    public void deleteOpenQA(String scene){
        mMyDbFlow.deleteOpenQA(scene);
    }

    /**
     * 删除一些
     * @param scenes
     */
    public void deleteOpenQASome(List<String> scenes){
        mMyDbFlow.deleteOpenQASome(scenes);
    }

    /**
     * 更新一条
     * @param openQA
     * @param dbCallback
     */
    public void updateOpenQA(OpenQA openQA, MyDbFlow.DbCallback dbCallback){
        try {
            mMyDbFlow.updateAsyncOpenQA(openQA, dbCallback);
        }catch (SQLiteException e){
            if(dbCallback!=null){
                dbCallback.OnFailure(e);
            }
        }
    }

    /**
     * 查询一条
     * @param id
     * @return
     */
    public OpenQA queryQA(int id){
        return mMyDbFlow.queryQaById(id);
    }

    /**
     * 构造BNF脚本文件
     * @param grammarPath   语法的绝对路径
     */
    public void queryAndCreateBNF(String grammarPath){
        mMyDbFlow.queryAsyncQaAll(new QueryCallback(this, QueryCallback.CREATE_BNF, grammarPath));
    }

    /**
     * 更新BNF脚本文件
     */
    public void queryAndUpdateBNF(){
        mMyDbFlow.queryAsyncQaAll(new QueryCallback(this, QueryCallback.UPDATE_BNF));
    }

    /**
     * 真正的脚本文件构造者
     * @param openQAList     语句集合
     * @param grammarPath    语法的绝对路径
     */
    private void createBNF(final List<OpenQA> openQAList, final String grammarPath){

        // 没有unsubscribe()
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {

                if(openQAList != null){
                    for (int i = 0; i < openQAList.size(); i++) {
                        LogUtils.e(TAG, openQAList.get(i).question);
                    }
                    try {
                        FileOutputStream output = new FileOutputStream(grammarPath);
                        //BufferedOutputStream buff = new BufferedOutputStream(output);
                        //FileChannel fileChannel = output.getChannel();
                        StringBuffer sb = new StringBuffer();
                        // 写头
                        output.write(BNF_HEAD.getBytes());

                        // 声明变量
                        output.write((SLOT1 + SLOT2 + SLOT3).getBytes());
                        output.write(SLOT4.getBytes());

                        // 组合语句
                        output.write(ASR1.getBytes());
                        output.write((ASR2 + ASR4).getBytes());

                        // 定义变量
                        output.write((WORD1 + WORD2).getBytes());
                        for (int i = 0; i < openQAList.size(); i++) {
                            if(StringUtils.isEmpty(openQAList.get(i).question)){
                                continue;
                            }
                            String[] questions = openQAList.get(i).question.split(SPLIT_STR);
                            if(questions != null ){
                                for (int j = 0; j < questions.length; j++) {
                                    sb.setLength(0);
                                    sb.append(questions[j]);
                                    sb.append(WORD3);
                                    sb.append(i+1);
                                    if(!(i==openQAList.size()-1 && j==questions.length-1)){
                                        sb.append(WORD5);
                                    }else {
                                        sb.append(WORD4);
                                    }
                                    output.write(sb.toString().getBytes());
                                }
                            }
                        }
                        output.close();

                        subscriber.onNext(grammarPath);
                        subscriber.onCompleted();
                    } catch (FileNotFoundException e) {
                        subscriber.onError(e);
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }else {
                    subscriber.onError(new Throwable("查询结果为空"));
                }

            }
        })
        .compose(MyRxUtils.<String>handleSchedulers());

        subscriptionCreator = observable.subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String grammarPath) {
                        mVoiceRecognizer.loadOfflineGrammar(grammarPath, MyConfigure.SIX_ABSOLUTE, null);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if(subscriptionCreator!=null && !subscriptionCreator.isUnsubscribed()) {
                            subscriptionCreator.unsubscribe();
                        }
                    }
                });
    }

    /**
     * 真正的脚本命令更新者，更新就是这样的格式，用 \n 隔开
     * @param openQAList     语句集合
     */
    private void updateBNF(final List<OpenQA> openQAList){
        if(openQAList==null)
            return;

        // 没有unsubscribe()
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < openQAList.size(); i++) {
                    if(StringUtils.isEmpty(openQAList.get(i).question)){
                        continue;
                    }
                    String[] questions = openQAList.get(i).question.split(SPLIT_STR);
                    if(questions != null ){
                        for (int j = 0; j < questions.length; j++) {
                            sb.append(questions[j]);
                            sb.append(WORD3);
                            sb.append(i+1);
                            sb.append(")\n");
                        }
                    }
                }
                mVoiceRecognizer.updateMyselfWords("updateWords", sb.toString());
                subscriber.onNext(sb.toString());
                subscriber.onCompleted();
            }
        })
        .compose(MyRxUtils.<String>handleSchedulers());

        subscriptionUpdate = observable.subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        LogUtils.e(TAG, "updateInfo: " + s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if(subscriptionUpdate!=null && !subscriptionUpdate.isUnsubscribed()) {
                            subscriptionUpdate.unsubscribe();
                        }
                    }
                });

    }

    /**
     * 异步查询的回调接口实现
     */
    private static class QueryCallback implements MyDbFlow.IQueryCallback{

        // 查询并利用结果构造脚本
        private static final int CREATE_BNF = 0;
        // 查询并利用结果更新脚本
        private static final int UPDATE_BNF = 1;
        // 查询并显示出来
        private static final int QUERY_SHOW = 2;

        private WeakReference<DynamicAsrOp> weak;
        // 查询之后的功能选择
        private int functionFlag = 0;
        // 语法构建的路径【语法的绝对路径】
        private String grammarPath = null;

        private QueryCallback(DynamicAsrOp dynamicAsrOp, int functionFlag){
            weak = new WeakReference<>(dynamicAsrOp);
            this.functionFlag = functionFlag;
        }

        private QueryCallback(DynamicAsrOp dynamicAsrOp, int functionFlag, String grammarPath){
            weak = new WeakReference<>(dynamicAsrOp);
            this.functionFlag = functionFlag;
            this.grammarPath = grammarPath;
        }

        @Override
        public void OnQueryResult(List<OpenQA> openQAList) {
            DynamicAsrOp dynamicAsrOp = weak.get();
            if(dynamicAsrOp != null){
                if(functionFlag==CREATE_BNF) {
                    dynamicAsrOp.createBNF(openQAList, grammarPath);

                }else if(functionFlag==UPDATE_BNF){
                    dynamicAsrOp.updateBNF(openQAList);

                }else if(functionFlag==QUERY_SHOW){

                }
            }
        }

        @Override
        public void OnSuccess() {

        }

        @Override
        public void OnFailure(Throwable error) {

        }
    }

}
