package com.zccl.ruiqianqi.mind.voice.alexa;

import android.content.Context;

import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * Created by ruiqianqi on 2017/2/15 0015.
 */

public class VoiceRecognizer extends BaseVoice {

    // 类标志
    private static String TAG = VoiceRecognizer.class.getSimpleName();
    // 录音类
    private AlexaClient mRecordClient;

    public VoiceRecognizer(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        mRecordClient = AlexaClient.getInstance();
        mRecordClient.initAlexa(mContext);
    }

    /**
     * 开启监听
     */
    public void start() {

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    mRecordClient.startRecord(mDataSourceType.ordinal());
                    subscriber.onNext("over");
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).
        compose(MyRxUtils.handleSchedulers())
        .subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {

                    }
                });

    }

    /**
     * 直接加载语法识别语音数据
     * @param dataS
     */
    public void writeRecognizer(byte[] dataS){
        mRecordClient.writeRecognizer(dataS);
    }

    /**
     * 结束监听
     */
    public void stop() {
        mRecordClient.stopRecord();
    }

}
