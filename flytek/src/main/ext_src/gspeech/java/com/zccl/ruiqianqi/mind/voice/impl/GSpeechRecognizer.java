package com.zccl.ruiqianqi.mind.voice.impl;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.google.auth.oauth2.GoogleCredentials;
import com.zccl.ruiqianqi.mind.voice.R;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice.RecognizerCallback;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import io.grpc.ManagedChannel;
import io.grpc.auth.ClientAuthInterceptor;
import io.grpc.okhttp.OkHttpChannelBuilder;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

import static java.lang.Thread.MAX_PRIORITY;

/**
 * Created by ruiqianqi on 2016/11/29 0029.
 */

public class GSpeechRecognizer {

    // 类标识
    private static String TAG = GSpeechRecognizer.class.getSimpleName();
    // 单例引用
    private static GSpeechRecognizer instance;

    /** 生命周期最长的上下文 */
    protected Context mContext;
    // 语法识别回调接口，多接口
    protected Map<String, RecognizerCallback> recognizerCallbackMap;

    // 通道管理器
    private ManagedChannel channel;
    // Google Speech 客户端
    private RecordClient recordClient;
    // RX 开始发送音频资源
    private Subscription subscription;
    // 子线程消息循环
    private volatile Looper mServiceLooper;
    // 子线程Handler
    private SubThreadHandler subThreadHandler;
    // 子线程体
    private HandlerThread handlerThread;

    protected GSpeechRecognizer(Context context) {
        this.mContext = context;
        init();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static GSpeechRecognizer getInstance(Context context) {
        if (instance == null) {
            synchronized (GSpeechRecognizer.class) {
                GSpeechRecognizer temp = instance;
                if (temp == null) {
                    temp = new GSpeechRecognizer(context);
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

        // 子线程相关配置
        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.setPriority(MAX_PRIORITY);
        handlerThread.start();
        mServiceLooper = handlerThread.getLooper();
        subThreadHandler = new SubThreadHandler(mServiceLooper);
        recognizerCallbackMap = new ConcurrentHashMap<>();

        try {
            channel = createChannel(getAuthorFile());
            recordClient = new RecordClient(channel, this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 返回认证文件流
     * @return
     */
    private InputStream getAuthorFile(){
        return mContext.getResources().openRawResource(R.raw.credential);
    }

    /**
     *
     * @param authorizationFile
     * @throws IOException
     */
    private ManagedChannel createChannel(InputStream authorizationFile) throws IOException {
        GoogleCredentials credS = GoogleCredentials.fromStream(authorizationFile);
        credS = credS.createScoped(GSpeechConfig.OAUTH2_SCOPES);
        return OkHttpChannelBuilder.forAddress(GSpeechConfig.HOST, GSpeechConfig.PORT)
                .intercept(new ClientAuthInterceptor(credS, Executors.newSingleThreadExecutor()))
                .build();
    }

    /**
     * 重新走认证流程
     * @return
     */
    protected ManagedChannel reCreateChannel(){
        try {
            return createChannel(getAuthorFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 开始识别
     */
    public void start() {

        LogUtils.e(TAG, "start listening......");
        unSubscribe();

        subscription = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    recordClient.startRecording(BaseVoice.DATA_SOURCE_TYPE.TYPE_RAW_DATA.ordinal());
                    subscriber.onNext("over");
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).
        compose(MyRxUtils.<String>handleSchedulers())
        .subscribe();

    }

    /**
     * 结束本次请求
     */
    private void unSubscribe(){
        if(null != subscription){
            if(!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscription = null;
        }
    }

    /**
     * 停止识别
     */
    public void stop() {
        recordClient.stop("stop");
        //recordClient.endAudio();
    }

    /**
     * 取消会话
     */
    public void cancel() {
        recordClient.stop("cancel");
        //recordClient.endAudio();
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
        if (recordClient.isRecording()) {
            subThreadHandler.obtainMessage(0, dataS).sendToTarget();
        }else {
            subThreadHandler.sendEmptyMessage(1);
        }
    }

    /**
     * 在子线程中写数据
     *
     * @param dataS
     */
    private void writeAudioThread(byte[] dataS) {
        recordClient.writeAudio(dataS);
    }

    /**
     * 设置语法识别回调接口【集合】
     *
     * @param recognizerCallback
     */
    public void addRecognizerCallback(String key, RecognizerCallback recognizerCallback) {
        recognizerCallbackMap.put(key, recognizerCallback);
    }

    /**************************************自身参数设置********************************************/

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

    /**
     * 设置识别语言
     * @param language
     */
    public void setLanguage(String language){
        recordClient.setLanguage(language);
    }

    /**********************************************************************************************/
    /**
     * 子Handler
     *
     * @author zccl
     */
    private final class SubThreadHandler extends Handler {
        public SubThreadHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                writeAudioThread((byte[]) msg.obj);
            }else if(msg.what == 1){
                recordClient.endAudio();
            }
        }
    }

}
