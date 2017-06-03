package com.zccl.ruiqianqi.mind.voice.iflytek;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.FileDownloadListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.zccl.ruiqianqi.mind.voice.allinone.R;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.OneWakeUpInfo;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.plugin.voice.ProxyVoice;
import com.zccl.ruiqianqi.plugin.voice.WakeInfo;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.SystemUtils;

import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by ruiqianqi on 2016/7/25 0025.
 */
public class VoiceWakeUp{

    /** 类日志标志 */
    private static String TAG = VoiceWakeUp.class.getSimpleName();
    /** 单例引用 */
    private static VoiceWakeUp instance;
    /** 录音设备 */
    public static final int RECORD_MODE = 1;
    /**
     * 唤醒文件的名字
     */
    private static String WAKE_UP_NAME = "wakeup";

    /** 生命周期最长的上下文 */
    protected Context mContext;
    /** 唤醒资源路径 */
    private String mResPath = null;
    /** 优化唤醒资源下载路径 */
    private String mJetDownLoadPath = null;
    /** 单麦的唤醒对象 */
    private VoiceWakeuper mVoiceWakeuper = null;
    /** 优化的唤醒文件地址 */
    private String mWakeUrl = null;
    /** 优化的唤醒文件MD5 */
    private String mWakeMd5 = null;
    /** 唤醒的门限值，尽最大努力让它唤醒，然后用其他值来开启录音*/
    private int curThresh = -50;
    /** 当前准备使用的录音阀值 */
    //private int curThreshUse = 10;

    /** 唤醒回调接口 */
    private AbstractVoice.WakeupCallback mWakeupCallback;
    // 需要重启吗
    private volatile boolean needReboot = false;
    // 需要检测重启吗，单麦没有东西喂狗
    private static final boolean needCheckReboot = false;

    /** 云端引擎还是本地引擎[oneshot使用的] */
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static VoiceWakeUp getInstance(Context context) {
        if(instance == null) {
            synchronized(VoiceWakeUp.class) {
                VoiceWakeUp temp = instance;
                if(temp == null) {
                    temp = new VoiceWakeUp(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 构造方法
     * @param context
     */
    private VoiceWakeUp(Context context) {
        this.mContext = context.getApplicationContext();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        // 加载唤醒资源
        mResPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + WAKE_UP_NAME + ".jet");
        // 优化唤醒路径
        mJetDownLoadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/res/" + WAKE_UP_NAME + ".jet";

    }

    /**
     * 开始底层录音，循环录音
     */
    public void startWakeUp(){
        setWakeUpParams();

        mVoiceWakeuper.startListening(new MyWakeuperListener());
        LogUtils.e(TAG, "startListening");
    }

    /**
     * 停止底层录音
     */
    public void stopWakeUp(){
        if (mVoiceWakeuper != null) {
            mVoiceWakeuper.cancel();
            mVoiceWakeuper.destroy();
            mVoiceWakeuper = null;
        }
    }

    /**
     * 开始底层录音，单次录音
     */
//    public void startOneShot(){
//
//        //目前不支持：唤醒 + 本地识别（语音+）
//        //目前支持的：唤醒 + 本地命令词（MSC）
//        VoiceConfigure.SPEAKER = VoiceConfigure.OFFLINE_MSC;
//        VoiceRecognizer.mFunctionType = VoiceRecognizer.FUNCTION_TYPE.TYPE_OFFLINE_WORDS;
//        //重新初始化
//        VoiceRecognizer.getInstance(mContext).reInit();
//        //得到引擎类型
//        mEngineType = VoiceRecognizer.getInstance(mContext).getParameter(SpeechConstant.ENGINE_TYPE);
//
//        // 清空参数
//        mVoiceWakeuper.setParameter(SpeechConstant.PARAMS, null);
//        /**
//         * 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
//         * 示例demo默认设置第一个唤醒词，建议开发者根据定制资源中唤醒词个数进行设置
//         */
//        mVoiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+curThresh+";1:"+curThresh);
//        // 设置唤醒+识别模式
//        mVoiceWakeuper.setParameter(SpeechConstant.IVW_SST, "oneshot");
//        // 设置返回结果格式
//        mVoiceWakeuper.setParameter(SpeechConstant.RESULT_TYPE, "json");
//        // 设置闭环优化网络模式，设置了不下载优化过的唤醒资源，照样没什么用
//        mVoiceWakeuper.setParameter(SpeechConstant.IVW_NET_MODE, "2");
//        // 设置采样率
//        mVoiceWakeuper.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
//        // 设置唤醒资源路径
//        mVoiceWakeuper.setParameter(ResourceUtil.IVW_RES_PATH, mResPath);
//        // 设置识别引擎
//        mVoiceWakeuper.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
//
//        if(mEngineType==SpeechConstant.TYPE_LOCAL){
//            // 设置本地识别资源
//            mVoiceWakeuper.setParameter(ResourceUtil.ASR_RES_PATH, VoiceRecognizer.getInstance(mContext).getParameter(ResourceUtil.ASR_RES_PATH));
//            // 设置语法构建路径
//            mVoiceWakeuper.setParameter(ResourceUtil.GRM_BUILD_PATH, VoiceRecognizer.getInstance(mContext).getParameter(ResourceUtil.GRM_BUILD_PATH));
//        }else {
//
//        }
//
//        Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//
//                long time = System.currentTimeMillis();
//                String grammarId;
//                while (true) {
//                    SharedPreferences sp = ShareUtils.getP(mContext);
//                    grammarId = sp.getString(VoiceRecognizer.KEY_GRAMMAR_ID, null);
//                    if (StringUtils.isEmpty(grammarId)) {
//                        long time2 = System.currentTimeMillis();
//                        //给你10秒时间，构建语法
//                        if(time2 - time > 10000){
//                            break;
//                        }
//                        continue;
//                    }else {
//                        break;
//                    }
//                }
//                if(!StringUtils.isEmpty(grammarId)){
//                    subscriber.onNext(grammarId);
//                }else {
//                    subscriber.onError(new Throwable("没有语法ID"));
//                }
//            }
//        })
//        .compose(MyRxUtils.<String>handleSchedulers())
//        .doOnNext(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                LogUtils.e(TAG, "语法ID: "+s);
//
//                //要对数据做改变的话，不要改变数据流，就是线程
//                Schedulers.io().createWorker().schedule(new Action0() {
//                    @Override
//                    public void call() {
//                        //存储什么的
//                    }
//                });
//
//            }
//        })
//        .subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//
//                if(mEngineType==SpeechConstant.TYPE_LOCAL) {
//                    // 设置本地识别使用语法id
//                    mVoiceWakeuper.setParameter(SpeechConstant.LOCAL_GRAMMAR, s);
//                }else {
//                    // 设置云端识别使用的语法id
//                    mVoiceWakeuper.setParameter(SpeechConstant.CLOUD_GRAMMAR, s);
//                }
//
//                LogUtils.e(TAG, "startListening");
//                mVoiceWakeuper.startListening(new MyWakeuperListener());
//            }
//        }, new Action1<Throwable>() {
//            @Override
//            public void call(Throwable throwable) {
//                LogUtils.e(TAG, "RxOnError: "+throwable.getMessage());
//            }
//        });
//
//
//    }



    /**************************************设置唤醒参数********************************************/
    /**
     * 闭环优化网络模式有三种：
     * 模式0：关闭闭环优化功能
     *
     * 模式1：开启闭环优化功能，允许上传优化数据。需开发者自行管理优化资源。
     * sdk提供相应的查询和下载接口，请开发者参考API文档，具体使用请参考本示例
     * queryResource及downloadResource方法；
     *
     * 模式2：开启闭环优化功能，允许上传优化数据及启动唤醒时进行资源查询下载；
     * 本示例为方便开发者使用仅展示模式0和模式2；
     */
    private void setWakeUpParams(){

        mVoiceWakeuper = VoiceWakeuper.createWakeuper(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    LogUtils.e(TAG, "语音唤醒初始化失败, 错误码："+code);
                }else {

                }
            }
        });

        // 清空参数
        mVoiceWakeuper.setParameter(SpeechConstant.PARAMS, null);
        // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
        //mVoiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+curThresh+";1:"+curThresh);
        mVoiceWakeuper.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"+curThresh);
        // 设置唤醒模式
        mVoiceWakeuper.setParameter(SpeechConstant.IVW_SST, "wakeup");
        // 设置返回结果格式
        mVoiceWakeuper.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置持续进行唤醒
        mVoiceWakeuper.setParameter(SpeechConstant.KEEP_ALIVE, "1");
        // 设置闭环优化网络模式，设置了不下载优化过的唤醒资源，照样没什么用
        mVoiceWakeuper.setParameter(SpeechConstant.IVW_NET_MODE, "2");
        // 设置采样率
        mVoiceWakeuper.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        /**
         * 通过设置此参数，启动离线引擎。在离线功能使用时，首次设置资源路径时，需要设置此参 数启动引擎。
         * 启动引擎参数值因业务类型而异：
         * 合成：SpeechConstant.ENG_TTS
         * 识别：SpeechConstant.ENG_ASR
         * 唤醒：SpeechConstant.ENG_IVW
         * 是否必须设置：是（在首次使用离线功能时）
         * 默认值：null
         */
        mVoiceWakeuper.setParameter(ResourceUtil.ENGINE_START, SpeechConstant.ENG_IVW);
        // 设置唤醒资源路径
        mVoiceWakeuper.setParameter(SpeechConstant.IVW_RES_PATH, mResPath);
    }

    /**
     * 查询闭环优化唤醒资源，获取URL和MD5，供后续下载使用
     * 请在闭环优化网络模式1或者模式2使用
     */
    public void queryResource() {
        int ret = mVoiceWakeuper.queryResource(mResPath, new RequestListener() {
            @Override
            public void onEvent(int i, Bundle bundle) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                try {
                    String resultInfo = new String(bytes, "utf-8");
                    JSONTokener token = new JSONTokener(resultInfo);
                    JSONObject object = new JSONObject(token);

                    int ret = object.getInt("ret");
                    if(ret == 0) {
                        mWakeUrl = object.getString("dlurl");
                        mWakeMd5 = object.getString("md5");
                        LogUtils.e(TAG, "queryResource: success");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                if(speechError != null) {
                    LogUtils.e(TAG, speechError.getPlainDescription(true));
                }
            }
        });
        LogUtils.e(TAG, "queryResource: " + ret);
    }

    /**
     * 下载闭环优化唤醒资源
     * 请在闭环优化网络模式1或者模式2使用
     * @param uri 查询请求返回下载链接
     * @param md5 查询请求返回资源md5
     */
    public void downloadResource(String uri, String md5) {
        if(StringUtils.isEmpty(uri)) {
            LogUtils.e(TAG, "downloadUri is null");
            return;
        }
        // 开始下载优化过的资源
        int ret = mVoiceWakeuper.downloadResource(uri, mJetDownLoadPath, md5, new FileDownloadListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onProgress(int i) {

            }

            @Override
            public void onCompleted(String filePath, SpeechError speechError) {
                // 下载完成回调
                if(speechError != null) {
                    LogUtils.e(TAG, speechError.getPlainDescription(true));
                } else {
                    LogUtils.e(TAG, filePath);
                }
            }
        });
        LogUtils.e(TAG, "downloadResource: "+ret);
    }

    /**
     * 设置拾音麦口【五麦】
     *   2
     * 3   1
     *   0
     * @param beam
     */
    public void setRealBeam(int beam){
    }

    /*************************************【单麦回调】*********************************************/
    /**
     * 原始录音回调接口，非静态内部类持有外部类的引用
     */
    private class MyWakeuperListener implements WakeuperListener {

        @Override
        public void onBeginOfSpeech() {
            //LogUtils.e(TAG, "onBeginOfSpeech");
        }

        @Override
        public void onResult(WakeuperResult wakeuperResult) {
            LogUtils.e(TAG, "onWakeup: " + wakeuperResult.getResultString());
            //{"score":15, "eos":14160, "id":2, "bos":13280, "sst":"wakeup"}
            OneWakeUpInfo oneWakeUpInfo = JsonUtils.parseJson(wakeuperResult.getResultString(), OneWakeUpInfo.class);
            if(null != oneWakeUpInfo){
                WakeInfo wakeInfo = new WakeInfo();
                wakeInfo.setScore(oneWakeUpInfo.getScore());
                wakeInfo.setId(oneWakeUpInfo.getId());
                if(null != mWakeupCallback){
                    mWakeupCallback.wakeSuccess(wakeInfo);
                }

            }else {
                if(null != mWakeupCallback){
                    mWakeupCallback.wakeFailure(new Throwable(mContext.getString(R.string.wakeup_parse_error)));
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            //LogUtils.e(TAG, "SpeechError");
            if(speechError != null) {
                if(null != mWakeupCallback){
                    mWakeupCallback.wakeFailure(speechError);
                }
            }
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle bundle) {
            //LogUtils.d(TAG, "onEvent: eventType:"+eventType+ "arg1:"+isLast + "arg2:" + arg2);
            // 识别结果
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                RecognizerResult result = ((RecognizerResult)bundle.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                if(null != mWakeupCallback){
                    mWakeupCallback.oneShot(result.getResultString());
                }
            }
        }

        @Override
        public void onVolumeChanged(int i) {

        }
    }


    /**
     * 重启唤醒
     * @param context
     * 返回值：true  表示需要检测
     *         false 表示不需要检测
     */
    public static boolean reboot(Context context){

        if(needCheckReboot) {
            if(null != instance && null != instance.mVoiceWakeuper){
                instance.mVoiceWakeuper.setParameter(SpeechConstant.PARAMS, null);
                instance.mVoiceWakeuper.cancel();
                instance.mVoiceWakeuper.destroy();

                instance.mVoiceWakeuper = null;

                // 单例销毁的话，监听就要重新设置了
                //instance = null;
            }

            // 唤醒监听开启
            VoiceWakeUp.getInstance(context).startWakeUp();
        }

        return needCheckReboot;
    }

    /**************************************自身参数设置********************************************/
    /**
     * 设置唤醒回调接口
     * @param wakeupCallback
     */
    public void setWakeupCallback(AbstractVoice.WakeupCallback wakeupCallback) {
        this.mWakeupCallback = wakeupCallback;
    }

    public boolean isReboot() {
        return needReboot;
    }

    public void setReboot(boolean needReboot) {
        this.needReboot = needReboot;
    }
}
