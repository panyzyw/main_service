package com.zccl.ruiqianqi.presentation.presenter;

import android.content.Context;

import com.zccl.ruiqianqi.domain.interactor.IHttpReqInteractor;
import com.zccl.ruiqianqi.domain.interactor.httpreq.HttpReqInteractor;
import com.zccl.ruiqianqi.domain.model.httpreq.BoYanDown;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.storage.HttpReqRepository;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.utils.AppUtils;

/**
 * Created by ruiqianqi on 2016/7/19 0019.
 */
public class HttpReqPresenter extends BasePresenter {

    /** 类标识 */
    private static String TAG = HttpReqPresenter.class.getSimpleName();

    // 薄言循环监听标志
    private final String mBoYan = "listen_BoYan";

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
     * RX执行：请求薄言语料
     * @param words
     */
    public void queryBoYan(String words, String rId, String rName) {
        httpReqInteractor.queryBoYan(words, rId, rName);
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
                                LogUtils.e(TAG, "tts success");
                                AppUtils.startListen(mContext, mBoYan, true, true);
                            }
                        });
                    }else {
                        // 没有回答语句，开始监听
                        LogUtils.e(TAG, "text is null");
                        AppUtils.startListen(mContext, mBoYan, true, true);
                    }
                }else {
                    // 没有对象，开始监听
                    LogUtils.e(TAG, "obj is null");
                    AppUtils.startListen(mContext, mBoYan, true, true);
                }
            }
        }

        @Override
        public void OnHttpFailure(int cmd, Throwable e) {
            if(IHttpReqInteractor.QUERY_BO_YAN == cmd){
                // 请求出错，开始监听
                LogUtils.e(TAG, "failure");
                AppUtils.startListen(mContext, mBoYan, true, true);
            }
        }

    }

}
