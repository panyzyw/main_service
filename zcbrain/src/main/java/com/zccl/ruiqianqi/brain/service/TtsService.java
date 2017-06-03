package com.zccl.ruiqianqi.brain.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.zccl.ruiqianqi.brain.ITtsBack;
import com.zccl.ruiqianqi.brain.ITtsService;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2016/11/24 0024.
 */

public class TtsService extends Service {

    // 类标志
    private static String TAG = TtsService.class.getSimpleName();
    // 具体的音频操作类
    private AbstractVoice voiceDevice;

    public TtsService() {
        voiceDevice = MindPresenter.getInstance().getVoiceDevice();
    }

    /**
     * 这个是服务体，是服务具体实现的地方，在这边没有通讯相关的了，通讯已经过来了
     */
    private final ITtsService.Stub mBinder = new ITtsService.Stub() {

        @Override
        public void startTTS(String text, String tag, final ITtsBack callback) throws RemoteException {
            voiceDevice.startTTS(text, tag, new AbstractVoice.SynthesizerCallback() {
                @Override
                public void OnBegin() {
                    try {
                        LogUtils.e(TAG, "OnBegin = " + callback);
                        if(null != callback) {
                            callback.OnBegin();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnPause() {
                    try {
                        LogUtils.e(TAG, "OnPause = " + callback);
                        if(null != callback) {
                            callback.OnPause();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnResume() {
                    try {
                        LogUtils.e(TAG, "OnResume = " + callback);
                        if(null != callback) {
                            callback.OnResume();
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void OnComplete(Throwable throwable, String tag) {
                    try {
                        if(null != callback) {
                            if (null == throwable) {
                                LogUtils.e(TAG, "OnCompleted = " + callback);
                                callback.OnComplete(tag);
                            } else {
                                LogUtils.e(TAG, "OnError = " + callback);
                                callback.OnError(throwable.getMessage(), tag);
                            }
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            });
        }

        @Override
        public void pauseTTS() throws RemoteException {
            voiceDevice.pauseTTS();
        }

        @Override
        public void resumeTTS() throws RemoteException {
            voiceDevice.resumeTTS();
        }

        @Override
        public void stopTTS() throws RemoteException {
            voiceDevice.stopTTS();
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.e(TAG, "onBind");
        return mBinder;
    }

}
