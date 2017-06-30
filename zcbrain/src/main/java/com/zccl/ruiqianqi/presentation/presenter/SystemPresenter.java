package com.zccl.ruiqianqi.presentation.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.zccl.ruiqianqi.brain.service.MainService;
import com.zccl.ruiqianqi.brain.system.IAllTtsCallback;
import com.zccl.ruiqianqi.brain.system.IMainCallback;
import com.zccl.ruiqianqi.brain.system.IMainService;
import com.zccl.ruiqianqi.brain.system.ITtsCallback;
import com.zccl.ruiqianqi.brain.system.MainBean;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.utils.AppUtils;

import static com.zccl.ruiqianqi.brain.handler.BaseHandler.SCENE_MY_MUSIC;
import static com.zccl.ruiqianqi.config.MyConfig.TTS_NOT_DEAL_RESPONSE;

/**
 * Created by ruiqianqi on 2017/6/12 0012.
 */

public class SystemPresenter extends BasePresenter {

    // 类标志
    private static String TAG = SystemPresenter.class.getSimpleName();

    // 获得当前应用名称
    public static final int GET_CUR_PKG = 1;

    // 开始
    private static String TTS_BEGIN = "begin";
    // 暂停
    private static String TTS_PAUSE = "pause";
    // 恢复
    private static String TTS_RESUME = "resume";
    // 停止
    private static String TTS_STOP = "stop";
    // 完成
    private static String TTS_COMPLETE = "complete";

    // 单例引用
    private static SystemPresenter instance;
    // 讲话服务
    private IMainService mainService;

    /**
     * 连接远程服务
     */
    public ServiceConnection mainConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mainService = IMainService.Stub.asInterface(service);
            setAllTtsCallback();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mainService = null;
        }

    };

    /**
     * 构造方法
     */
    private SystemPresenter(){
        init();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static SystemPresenter getInstance() {
        if (instance == null) {
            synchronized (SystemPresenter.class) {
                SystemPresenter temp = instance;
                if (temp == null) {
                    temp = new SystemPresenter();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    private void init(){
        bindMainService();
    }

    /**********************************************************************************************/
    /**
     * 绑定服务，这个过程竟然要10多秒
     */
    private void bindMainService(){
        if(null != mContext && null == mainService) {
            ComponentName componentName = new ComponentName("com.yongyida.robot.system", "com.zccl.ruiqianqi.brain.system.MainService");
            Intent intent = new Intent();
            intent.setComponent(componentName);
            //intent.setPackage("");
            //intent.setAction("");
            mContext.bindService(intent, mainConn, Context.BIND_AUTO_CREATE);
            LogUtils.e(TAG, "bindSystemService");
        }
    }

    /**
     * 解绑服务
     */
    public void unbindSystemService(){
        if(null != mContext && null != mainService) {
            mContext.unbindService(mainConn);
        }
    }

    /**********************************************************************************************/
    /**
     * 异步回调
     * @param cmd
     * @param msg
     * @param callback
     */
    public void sendCommand(int cmd, String msg, IMainCallback callback){
        bindMainService();
        if(null != mainService){
            try {
                mainService.sendCommand(cmd, msg, callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 同步获取
     * @param cmd
     * @param msg
     * @return
     */
    public MainBean sendCommandSync(int cmd, String msg){
        bindMainService();
        if(null != mainService){
            try {
                return mainService.sendCommandSync(cmd, msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 发音过程回调
     * @param words -------------- 要发音的文字
     * @param from  -------------- 携带的标志
     * @param synthesizerCallback
     */
    public void startTTS(String words, String from, final AbstractVoice.SynthesizerCallback synthesizerCallback) {
        bindMainService();
        if(null != mainService) {
            try {
                mainService.startTTS(words, from, new ITtsCallback.Stub() {
                    @Override
                    public void OnBegin() throws RemoteException {
                        if (null != synthesizerCallback) {
                            synthesizerCallback.OnBegin();
                        }
                    }

                    @Override
                    public void OnPause() throws RemoteException {
                        if (null != synthesizerCallback) {
                            synthesizerCallback.OnPause();
                        }
                    }

                    @Override
                    public void OnResume() throws RemoteException {
                        if (null != synthesizerCallback) {
                            synthesizerCallback.OnResume();
                        }
                    }

                    @Override
                    public void OnComplete(String error, String tag) throws RemoteException {
                        if (null != synthesizerCallback) {
                            if (StringUtils.isEmpty(error)) {
                                synthesizerCallback.OnComplete(null, tag);
                            } else {
                                synthesizerCallback.OnComplete(new Throwable(error), tag);
                            }
                        }
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发音结束回调
     * @param words -------------- 要发音的文字
     * @param from --------------- 携带的标志
     * @param runnable
     */
    public void startTTS(final String words, String from, final Runnable runnable) {
        bindMainService();
        if(null != mainService){
            try {
                mainService.startTTS(words, from, new ITtsCallback.Stub() {
                    @Override
                    public void OnBegin() throws RemoteException {

                    }

                    @Override
                    public void OnPause() throws RemoteException {

                    }

                    @Override
                    public void OnResume() throws RemoteException {

                    }

                    @Override
                    public void OnComplete(String error, String tag) throws RemoteException {
                        if(null != runnable){
                            runnable.run();
                        }
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发音结束回调
     * @param words -------------- 要发音的文字
     * @param runnable
     */
    public void startTTS(String words, Runnable runnable) {
        startTTS(words, null, runnable);
    }

    /**
     * 暂停
     */
    public void pauseTTS()  {
        bindMainService();
        if(null != mainService){
            try {
                mainService.pauseTTS();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 恢复
     */
    public void resumeTTS() {
        bindMainService();
        if(null != mainService){
            try {
                mainService.resumeTTS();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止
     */
    public void stopTTS() {
        bindMainService();
        if(null != mainService){
            try {
                mainService.stopTTS(MainService.TAG);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否在发音
     * @return
     */
    public boolean isSpeaking() {
        bindMainService();
        if(null != mainService){
            try {
                return mainService.isSpeaking();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 设置所有TTS相关的回调
     */
    private void setAllTtsCallback(){
        bindMainService();
        if(null != mainService){
            try {
                mainService.setAllTTSCallback(new IAllTtsCallback.Stub() {
                    @Override
                    public void OnProgress(String from, String state) throws RemoteException {
                        dealWithMusic(from, state);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 如果音乐在最上层，则处理当前音乐
     * @param from
     * @param state
     */
    private void dealWithMusic(String from, String state){
        StatePresenter sp = StatePresenter.getInstance();
        String scene = sp.getScene();

        LogUtils.e(TAG, "TTS_STATE = " + state + "; from = " + from);
        if(TTS_NOT_DEAL_RESPONSE.equals(from)){
            return;
        }

        // 音乐播放器
        if(SCENE_MY_MUSIC.equals(scene)){
            if(TTS_BEGIN.equals(state)){
                AppUtils.controlMusicPlayer(mContext, "pause");
            }
            else if(TTS_COMPLETE.equals(state)){
                AppUtils.controlMusicPlayer(mContext, "play");
            }
        }

        /*
        // 讯飞音乐
        else if(SCENE_XF_MUSIC.equals(scene)){
            if(TTS_BEGIN.equals(state)){
                XiriPresenter xiriPresenter = new XiriPresenter();
                xiriPresenter.xfMusicAction("pause", null);
            }
            else if(TTS_COMPLETE.equals(state)){
                XiriPresenter xiriPresenter = new XiriPresenter();
                xiriPresenter.xfMusicAction("continue", null);
            }
        }
        */
    }
}

