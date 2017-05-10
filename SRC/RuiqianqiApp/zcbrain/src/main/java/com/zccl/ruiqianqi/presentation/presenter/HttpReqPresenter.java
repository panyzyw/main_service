package com.zccl.ruiqianqi.presentation.presenter;

import android.content.Context;
import android.os.Bundle;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.domain.interactor.IHttpReqInteractor;
import com.zccl.ruiqianqi.domain.interactor.httpreq.HttpReqInteractor;
import com.zccl.ruiqianqi.domain.model.httpreq.BoYanDown;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.storage.HttpReqRepository;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import static com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver.ACT_RECYCLE_LISTEN;

/**
 * Created by ruiqianqi on 2016/7/19 0019.
 */
public class HttpReqPresenter extends BasePresenter {

    /** 类标识 */
    private static String TAG = HttpReqPresenter.class.getSimpleName();

    /** USE_CASE：翻译用例 */
    private IHttpReqInteractor httpReqInteractor;

    // 语音处理类
    private AbstractVoice voice;

    public HttpReqPresenter(Context context) {
        super(context);

        voice = MindPresenter.getInstance().getVoiceDevice();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        httpReqInteractor = new HttpReqInteractor(mContext, new HttpReqRepository(mContext));
        httpReqInteractor.setHttpReqCallback2P(new HttpReqListener());
    }

    /************************************【中心对外提供的方法】************************************/
    /**
     * RX执行：翻译任务
     * @param words
     */
    public void queryBoYan(String words, String rId, String rName) {
        httpReqInteractor.queryBoYan(words, rId, rName);
    }

    /**
     * 开始监听
     */
    private void startListen(String tag){
        LogUtils.e(TAG, tag);
        Bundle bundle = new Bundle();
        bundle.putString(mContext.getString(R.string.recycle_from_key), "BoYan");
        bundle.putBoolean(mContext.getString(R.string.recycle_expression_key), true);
        MyAppUtils.sendBroadcast(mContext, ACT_RECYCLE_LISTEN, bundle);
    }

    /***********************************【领域给中心的回调实现】***********************************/
    /**
     * 来自DOMAIN层的回调接口
     */
    private class HttpReqListener<T> implements IHttpReqInteractor.HttpReqCallback2P<T>{

        @Override
        public void OnHttpSuccess(int cmd, T t) {
            if(IHttpReqInteractor.QUERY_BO_YAN == cmd){
                BoYanDown boYanDown = (BoYanDown) t;
                if(null != boYanDown){
                    if(!StringUtils.isEmpty(boYanDown.getAnswer())){
                        voice.startTTS(boYanDown.getAnswer(), new Runnable() {
                            @Override
                            public void run() {
                                // 说话说完了开始监听
                                startListen("tts success");
                            }
                        });
                    }else {
                        // 没有回答语句，开始监听
                        startListen("text is null");
                    }
                }else {
                    // 没有对象，开始监听
                    startListen("obj is null");
                }
            }
        }

        @Override
        public void OnHttpFailure(int cmd, Throwable e) {
            if(IHttpReqInteractor.QUERY_BO_YAN == cmd){
                // 请求出错，开始监听
                startListen("failure");
            }
        }

    }

}
