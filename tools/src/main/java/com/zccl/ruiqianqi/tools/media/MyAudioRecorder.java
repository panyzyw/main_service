package com.zccl.ruiqianqi.tools.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;

import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zccl.ruiqianqi.tools.media.MyAudioRecorder.State.ERROR;
import static com.zccl.ruiqianqi.tools.media.MyAudioRecorder.State.INITIALIZED;

/**
 * Created by ruiqianqi on 2016/8/5 0005.
 *
 * SampleRate:16000
 * Channel:2
 * Format:2
 * FramePeriod:640
 * BufferSize:5120
 * MinBufferSize:2048
 * ActualBufferSize:1280
 *
 */
public class MyAudioRecorder {

    private static String TAG = MyAudioRecorder.class.getSimpleName();
    /**
     * 全局上下文
     */
    private Context mContext;

    /**
     * 录制频率，单位hz.这里的值注意了，写的不好，可能实例化AudioRecord对象的时候，会出错。我开始写成11025就不行。这取决于硬件设备
     */
    private int sampleRate = 16000;
    /**
     * 声道，输入的单声道
     */
    // private int channelConfig = AudioFormat.CHANNEL_IN_DEFAULT;
    // 设置音频的录制的声道
    // CHANNEL_IN_MONO   为单声道
    // CHANNEL_IN_STEREO 为双声道，
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 位长16bit，每次采样用多少字节来存
     */
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    // 每次采样用多少位来存
    private short mBitsPerSample;
    // 采样通道的数量
    private short mNumOfChannels = 1;
    // 每采样多少次进行通知
    private int mPeriodInFrames;
    // 输出的字节缓存
    private byte[] buffer;
    // 录音总大小
    private int payloadSize = 0;
    // 文件读写
    private RandomAccessFile randomAccessWriter;
    // 文件保存路径
    private String mFilePath;

    /**
     * 录制条件
     */
    private boolean isRecording = false;
    /**
     * 接收缓存大小
     */
    private int recvBufSize = 0;
    /**
     * 录音类
     */
    private AudioRecord audioRecord = null;
    // 录音器当前状态
    private State mState;

    // 降噪用的
    private int mAudioSessionId;
    // 降噪用的
    private NoiseSuppressor mNoiseSuppressor;
    // 降噪用的
    private AcousticEchoCanceler mAcousticEchoCanceler;
    // 降噪用的
    private AutomaticGainControl mAutomaticGainControl;

    public MyAudioRecorder(Context context) {
        this.mContext = context;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        try {

            if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                mBitsPerSample = 16;
            } else {
                mBitsPerSample = 8;
            }

            switch (channelConfig) {
                case AudioFormat.CHANNEL_IN_DEFAULT: // AudioFormat.CHANNEL_CONFIGURATION_DEFAULT
                case AudioFormat.CHANNEL_IN_MONO:
                case AudioFormat.CHANNEL_CONFIGURATION_MONO:
                    mNumOfChannels = 1;
                    break;
                case AudioFormat.CHANNEL_IN_STEREO:
                case AudioFormat.CHANNEL_CONFIGURATION_STEREO:
                case (AudioFormat.CHANNEL_IN_FRONT | AudioFormat.CHANNEL_IN_BACK):
                    mNumOfChannels = 2;
                    break;
                case AudioFormat.CHANNEL_INVALID:
                default:
                    mState = ERROR;
                    return;
            }

            // 录音通知周期，一毫秒采样16次，100毫秒通知一次，100毫秒采样1600次
            // 表示每采样1600次通知一次
            mPeriodInFrames = sampleRate / 1000 * 100;
            // 进行数据存储，这个缓存区就必须能存下，1600次采样的数据量，其实就是一半的采样率的时候就通知一次
            recvBufSize = (mPeriodInFrames * 2) * mBitsPerSample * mNumOfChannels / 8;

            // 标准的最小缓存区
            int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);

            if (recvBufSize < minBufSize) {
                // Check to make sure buffer size is not smaller than the smallest allowed one
                recvBufSize = minBufSize;
                //【mBitsPerSample * mNumOfChannels / 8 表示每次采样的字节数】
                mPeriodInFrames = recvBufSize / (2 * mBitsPerSample * mNumOfChannels / 8);
            }

            // 采样率，通道，格式
            //recvBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
            // 构造录音对象
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, recvBufSize);

            // 这个特性使用时有个注意点，就是回调只会发生在实际数据读取之后，
            // 也就是使用者通过read方法读取出periodInFrames这么多数据时才会触发这个回调，否则什么也不会发生。
            audioRecord.setPositionNotificationPeriod(mPeriodInFrames);
            // 录音通知回调接口
            audioRecord.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioRecord recorder) {
                    // 标记性的回调
                }

                @Override
                public void onPeriodicNotification(AudioRecord recorder) {
                    // 周期性的回调
                }
            });

            // 定义缓冲,16bit
            //short[] buffer = new short[recvBufSize];
            buffer = new byte[recvBufSize];
            //buffer = new byte[mPeriodInFrames * mBitsPerSample * mNumOfChannels / 8];

            // Reset file path
            mFilePath = null;
            // 降噪
            filterVoice();

            mState = INITIALIZED;

            LogUtils.e(TAG, "recvBufSize = " + recvBufSize);
            LogUtils.e(TAG, "minBufSize = " + minBufSize);

        } catch (Exception e) {
            mState = ERROR;
        }
    }

    /**
     * 降噪
     */
    private void filterVoice() {
        mAudioSessionId = audioRecord.getAudioSessionId();
        if (mAudioSessionId != 0 && android.os.Build.VERSION.SDK_INT >= 16) {
            if (NoiseSuppressor.isAvailable()) {
                if (mNoiseSuppressor != null) {
                    mNoiseSuppressor.release();
                    mNoiseSuppressor = null;
                }
                mNoiseSuppressor = NoiseSuppressor.create(mAudioSessionId);
                if (mNoiseSuppressor != null) {
                    mNoiseSuppressor.setEnabled(true);
                } else {
                }
            } else {
            }

            if (AcousticEchoCanceler.isAvailable()) {
                if (mAcousticEchoCanceler != null) {
                    mAcousticEchoCanceler.release();
                    mAcousticEchoCanceler = null;
                }

                mAcousticEchoCanceler = AcousticEchoCanceler.create(mAudioSessionId);
                if (mAcousticEchoCanceler != null) {
                    mAcousticEchoCanceler.setEnabled(true);
                    // mAcousticEchoCanceler.setControlStatusListener(listener)setEnableStatusListener(listener)
                } else {
                    mAcousticEchoCanceler = null;
                }
            } else {

            }

            if (AutomaticGainControl.isAvailable()) {
                if (mAutomaticGainControl != null) {
                    mAutomaticGainControl.release();
                    mAutomaticGainControl = null;
                }

                mAutomaticGainControl = AutomaticGainControl.create(mAudioSessionId);
                if (mAutomaticGainControl != null) {
                    mAutomaticGainControl.setEnabled(true);
                } else {
                }

            } else {
            }
        } else {
        }
    }

    /**
     * 设置输出的文件路径
     *
     * @param filePath 文件路径
     */
    public void setOutputFile(String filePath) {
        if (mState == INITIALIZED) {
            this.mFilePath = filePath;
        }
    }

    /**
     * 初始化WAV文件头
     */
    private void initWaveHeader() {
        if (!StringUtils.isEmpty(mFilePath)) {
            try {
                // 写文件头
                randomAccessWriter = new RandomAccessFile(mFilePath, "rw");
                // 设置文件长度为0，为了防止这个file以存在
                randomAccessWriter.setLength(0);

                randomAccessWriter.writeBytes("RIFF");
                // 不知道文件最后的大小，所以设置0
                randomAccessWriter.writeInt(0);
                randomAccessWriter.writeBytes("WAVE");
                randomAccessWriter.writeBytes("fmt ");
                // Sub-chunk size, 16 for PCM
                randomAccessWriter.writeInt(Integer.reverseBytes(16));
                // AudioFormat, 1 为 PCM
                randomAccessWriter.writeShort(Short.reverseBytes((short) 1));
                // 数字为声道, 1 为 mono, 2 为 stereo
                randomAccessWriter.writeShort(Short.reverseBytes(mNumOfChannels));
                // 采样率
                randomAccessWriter.writeInt(Integer.reverseBytes(sampleRate));
                // 采样率, SampleRate * NumberOfChannels * BitsPerSample/8
                randomAccessWriter.writeInt(Integer.reverseBytes(sampleRate * mBitsPerSample * mNumOfChannels / 8));
                // Block align, NumberOfChannels*BitsPerSample/8
                randomAccessWriter.writeShort(Short.reverseBytes((short) (mNumOfChannels * mBitsPerSample / 8)));
                // Bits per sample
                randomAccessWriter.writeShort(Short.reverseBytes(mBitsPerSample));
                randomAccessWriter.writeBytes("data");
                // Data chunk size not known yet, write 0
                randomAccessWriter.writeInt(0);

                payloadSize = 0;

            } catch (Exception e) {

            }
        }
    }

    /**
     * 写文件
     *
     * @param bsBuffer
     * @param len
     */
    private void saveWaveData(byte[] bsBuffer, int len) {
        if (len > 0) {
            if (null != randomAccessWriter) {
                try {
                    randomAccessWriter.write(bsBuffer, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                payloadSize += len;
            }
        }
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        if (mState == INITIALIZED) {
            Observable.create(new Observable.OnSubscribe<String>() {
                @Override
                public void call(Subscriber<? super String> subscriber) {

                    // 初始化WAV文件头
                    initWaveHeader();

                    // 开始录制
                    audioRecord.startRecording();
                    isRecording = true;
                    mState = State.RECORDING;

                    // 定义循环，根据isRecording的值来判断是否继续录制
                    while (isRecording) {

                        // 从bufferSize中读取字节，返回读取的byte个数
                        int bufferReadResult = audioRecord.read(buffer, 0, buffer.length);
                        switch (bufferReadResult) {
                            case android.media.AudioRecord.ERROR_INVALID_OPERATION:
                                break;
                            case android.media.AudioRecord.ERROR_BAD_VALUE:
                                break;
                            case android.media.AudioRecord.ERROR_DEAD_OBJECT:
                                break;
                            case android.media.AudioRecord.ERROR:
                                break;
                        }
                        // 写文件
                        saveWaveData(buffer, bufferReadResult);

                    }

                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mState == State.RECORDING) {
            mState = State.INITIALIZED;

            // 处理录音
            isRecording = false;
            audioRecord.stop();

            // 处理文件
            if (null != randomAccessWriter) {
                try {
                    randomAccessWriter.seek(4); // Write size to RIFF header
                    randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));
                    // Write size to Subchunk2Size
                    randomAccessWriter.seek(40);
                    // field
                    randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));
                    randomAccessWriter.close();
                    randomAccessWriter = null;
                } catch (IOException e) {

                }
            }
        }
    }

    /**
     * 释放与这个类相关的资源，和移除不必要的文件，在必要的时候
     */
    public void release() {

        // 处理录音
        stopRecord();
        if (audioRecord != null) {
            audioRecord.release();
        }

        // 处理文件
        if (null != randomAccessWriter) {
            try {
                randomAccessWriter.close();
                randomAccessWriter = null;

                if (!StringUtils.isEmpty(mFilePath)) {
                    new File(mFilePath).delete();
                }
            } catch (IOException e) {

            }
        }
    }

    /**
     * 重置录音，并设置 state 为 {@link State#INITIALIZED}，
     * 如果当前状态为 {@link State#RECORDING}，将会停止录音。
     * 这个方法不会抛出异常，但是会设置状态为 {@link State#ERROR}
     */
    public void reset() {
        try {
            if (mState != State.ERROR) {
                release();
                init();
            }
        } catch (Exception e) {
            mState = State.ERROR;
        }
    }

    /**
     * 录音的状态
     */
    public enum State {
        /**
         * 录音初始化
         */
        INITIALIZED,
        /**
         * 录音中
         */
        RECORDING,
        /**
         * 录音生了错误
         */
        ERROR,
    }
}
