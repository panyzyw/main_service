package com.zccl.ruiqianqi.mind.voice.impl;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeechGrpc;
import com.google.cloud.speech.v1.StreamingRecognitionConfig;
import com.google.cloud.speech.v1.StreamingRecognitionResult;
import com.google.cloud.speech.v1.StreamingRecognizeRequest;
import com.google.cloud.speech.v1.StreamingRecognizeResponse;
import com.google.protobuf.ByteString;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static com.zccl.ruiqianqi.mind.voice.impl.BaseVoice.DATA_SOURCE_TYPE.TYPE_RECORD;
import static com.zccl.ruiqianqi.mind.voice.impl.BaseVoice.DATA_SOURCE_TYPE.TYPE_RAW_DATA;

/**
 * Created by rmalta on 06/09/16.
 */
public class RecordClient {

    // 类标志
    private static String TAG = RecordClient.class.getSimpleName();
    // 音频识别
    private GSpeechRecognizer mSpeechRecognizer;

    // 录制频率,采样率
    private int RECORDER_SAMPLE_RATE;
    // 声道，输入的单声道
    private int RECORDER_CHANNELS;
    // 位长16bit，每次采样用多少字节来存
    private int RECORDER_AUDIO_ENCODING;
    // 接收缓存大小
    private int bufferSize = 0;
    // 分配的缓存
    private byte[] buffer = null;
    // 录音类
    private AudioRecord recorder = null;

    // 连接通道管理器
    private ManagedChannel channel;
    private RecognitionConfig config;
    private SpeechGrpc.SpeechStub speechClient;
    private StreamingRecognizeRequest initial;
    private StreamingRecognitionConfig streamingConfig;
    private StreamObserver<StreamingRecognizeResponse> responseObserver;
    private StreamObserver<StreamingRecognizeRequest> requestObserver;

    // 单麦录音
    private volatile boolean isRecording = false;
    // 五麦监听
    private volatile boolean isListening = false;
    // 音频输入
    private volatile boolean isEndAudio = false;

    // 0表示录音 1表示音频数组
    private int mDataWay = 0;

    public RecordClient(ManagedChannel channel, GSpeechRecognizer speechRecognizer){
        this.channel = channel;
        this.mSpeechRecognizer = speechRecognizer;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        this.RECORDER_SAMPLE_RATE = 16000;
        this.RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
        this.RECORDER_AUDIO_ENCODING = ENCODING_PCM_16BIT;
        this.bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
        this.buffer = new byte[bufferSize];
        this.recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                this.RECORDER_SAMPLE_RATE,
                this.RECORDER_CHANNELS,
                this.RECORDER_AUDIO_ENCODING,
                bufferSize);

        initStream();
    }

    /**
     * 初始化Google Speech配置
     */
    private void initStream(){

        this.speechClient = SpeechGrpc.newStub(channel);
        this.config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(this.RECORDER_SAMPLE_RATE)
                .setLanguageCode(GSpeechConfig.Language_USE)
                .build();

        // Streaming config
        streamingConfig = StreamingRecognitionConfig.newBuilder()
                .setConfig(config)
                .setInterimResults(false)  // 只返回一条最优结果
                .setSingleUtterance(false) // 是否进行连续监听
                .build();

        // First request
        initial = StreamingRecognizeRequest.newBuilder()
                .setStreamingConfig(streamingConfig)
                .build();

        // 结果观察
        responseObserver = new StreamObserver<StreamingRecognizeResponse>() {
            @Override
            public void onNext(StreamingRecognizeResponse response) {
                LogUtils.e(TAG, "onNext = " + response);

                if (response == null)
                    return;

                /*
                if(response.getEndpointerType()==EndpointerType.START_OF_SPEECH){
                    // 开始说话
                }else if(response.getEndpointerType()==EndpointerType.END_OF_SPEECH){

                }else if(response.getEndpointerType()==EndpointerType.END_OF_AUDIO){

                }
                */

                int numOfResults = response.getResultsCount();
                if (numOfResults > 0) {
                    for (int i = 0; i < numOfResults; i++) {
                        StreamingRecognitionResult result = response.getResultsList().get(i);
                        if (result.getIsFinal()) {

                            // 主动识别音频，直到结束【一】
                            stop("isFinal");

                            String resultStr = result.getAlternatives(0).getTranscript();
                            if(StringUtils.isEmpty(resultStr)){
                                callbackError(new Throwable("result is null"));

                            }else {
                                callbackResult(resultStr);

                            }
                        } else {

                        }
                    }
                }
            }

            @Override
            public void onError(Throwable error) {
                LogUtils.e(TAG, "onError");
                stop("onError");

                callbackError(error);
            }

            @Override
            public void onCompleted() {
                LogUtils.e(TAG, "onCompleted");
                stop("onCompleted");
            }
        };
    }

    /**
     * 设置识别语言
     * @param language
     */
    protected void setLanguage(String language){
        config.toBuilder().setLanguageCode(language);
    }

    /**
     * 正常的回调
     * @param result
     */
    private void callbackResult(String result){
        for (String key : mSpeechRecognizer.recognizerCallbackMap.keySet()) {
            if (mSpeechRecognizer.recognizerCallbackMap.get(key) != null) {
                mSpeechRecognizer.recognizerCallbackMap.get(key).onResult(result, AbstractVoice.RecognizerCallback.LISTEN);
            }
        }
    }

    /**
     * 失败的回调
     * @param error
     */
    private void callbackError(Throwable error){
        for (String key : mSpeechRecognizer.recognizerCallbackMap.keySet()) {
            if (mSpeechRecognizer.recognizerCallbackMap.get(key) != null) {
                mSpeechRecognizer.recognizerCallbackMap.get(key).onError(error);
            }
        }
    }

    /**
     * Send streaming recognize requests to server.
     * 开始进行语音识别
     */
    public void startRecording(int dataWay){
        this.mDataWay = dataWay;

        // 认证有问题，重新认证
        try {
            requestObserver = speechClient.streamingRecognize(responseObserver);
        }catch (StatusRuntimeException e){
            channel = mSpeechRecognizer.reCreateChannel();
            if(channel != null) {
                initStream();
                requestObserver = speechClient.streamingRecognize(responseObserver);
            }
        }
        if(null != requestObserver) {
            requestObserver.onNext(initial);
        }

        // 录音
        if(mDataWay == TYPE_RECORD.ordinal()){
            try {
                recorder.startRecording();
                recorder.setPositionNotificationPeriod(bufferSize / (2 * RECORDER_SAMPLE_RATE * RECORDER_CHANNELS / 8));
                recorder.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                    @Override
                    public void onMarkerReached(AudioRecord recorder) {

                    }

                    @Override
                    public void onPeriodicNotification(AudioRecord recorder) {

                    }
                });
                isRecording = true;
                long timeStart = System.currentTimeMillis();
                int count;
                while (isRecording && (count = recorder.read(buffer, 0, buffer.length)) > AudioRecord.SUCCESS) {
                    LogUtils.e(TAG, "isRecording = "+ count + "-" + buffer.length);
                    StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingConfig)
                            .setAudioContent(ByteString.copyFrom(buffer, 0, buffer.length))
                            .build();
                    if (requestObserver != null) {
                        requestObserver.onNext(request);
                    }

                    // 设置一个超时时间，超时结束【二】
                    /*
                    long timeOver = System.currentTimeMillis();
                    if(timeOver - timeStart > 10000){
                        isRecording = false;
                    }
                    */

                }
                recorder.stop();
                requestObserver.onCompleted();

            } catch (RuntimeException e) {
                requestObserver.onError(e);
            }

        }
        // 音频数据
        else if(mDataWay == TYPE_RAW_DATA.ordinal()){
            isListening = true;

        }
    }

    /**
     * 直接输入音频数组
     * @param dataS
     */
    public void writeAudio(byte[] dataS){
        if(isListening) {
            LogUtils.e(TAG, "writeAudio = " + dataS.length);
            try {
                StreamingRecognizeRequest request = StreamingRecognizeRequest.newBuilder()
                        .setAudioContent(ByteString.copyFrom(dataS, 0, dataS.length))
                        .build();
                if (null != requestObserver) {
                    requestObserver.onNext(request);
                }
            } catch (RuntimeException e) {
                requestObserver.onError(e);
            }
        }
    }

    /**
     * 结束音频输入
     */
    public void endAudio(){
        if(mDataWay == TYPE_RECORD.ordinal()) {

        }else {
            if (null != requestObserver) {
                requestObserver.onCompleted();
                requestObserver = null;
            }
        }
    }

    /**
     * 是不是正在识别
     * @return
     */
    public boolean isRecording(){
        if(mDataWay == TYPE_RECORD.ordinal()){
            return isRecording;
        }else {
            return isListening;
        }
    }

    /**
     * 停止当前的识别，手动结束语音输入
     * 被动地由外部结束【三】
     */
    public void stop(String from) {
        LogUtils.e(TAG, "stop listening......" + mDataWay + " from = " + from);
        if(mDataWay == TYPE_RECORD.ordinal()){
            if(isRecording) {
                isRecording = false;
            }
        }else {
            if(isListening) {
                isListening = false;
            }
        }
    }

    /**
     * 销毁释放资源，这是要重启的
     */
    public void release() {
        stop("release");

        if(mDataWay == TYPE_RECORD.ordinal()){
            if (null != recorder) {
                recorder.release();
                recorder = null;
            }
        }else {
            if(null != channel) {
                channel.shutdownNow();
                channel = null;
            }
        }
    }

}
