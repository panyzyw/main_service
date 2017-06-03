package com.zccl.ruiqianqi.tools.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by ruiqianqi on 2017/1/19 0019.
 *
 * 播放声音可以用MediaPlayer和AudioTrack，两者都提供了java API供应用开发者使用。虽然都可以播放声音，但两者还是有很大的区别的。
 * 其中最大的区别是MediaPlayer可以播放多种格式的声音文件，例如MP3，AAC，WAV，OGG，MIDI等。
 * MediaPlayer会在framework层创建对应的音频解码器。
 * 而AudioTrack只能播放已经解码的PCM流，如果是文件的话只支持wav格式的音频文件，因为wav格式的音频文件大部分都是PCM流。
 * AudioTrack不创建解码器，所以只能播放不需要解码的wav文件。
 *
 * 注：PCM和WAV
 * 以下摘自：http://www.erji.net/read.php?tid=227570
 * 简单来说，pcm是一种数据编码格式，CD唱盘上刻录的就直接用pcm格式编码的数据文件；
 * wav是一种声音文件格式，wav里面包含的声音数据可以是采用pcm格式编码的声音数据，也可以是采用其它格式编码的声音数据，但目前一般采用pcm编码的声音数据
 * 两者区别就是这些
 * pcm是一个通信上的概念，脉冲编码调制。wav是媒体概念，体现的是封装。wav文件可以封装pcm编码信息，也可以封装其他编码格式，例如mp3等。
 */
public class MyAudioTrack {

    private String TAG = MyAudioTrack.class.getSimpleName();
    /**
     * AudioTrack播放声音时不能直接把wav文件传递给AudioTrack进行播放，必须传递buffer，
     * 通过write函数把需要播放的缓冲区buffer传递给AudioTrack，然后才能播放。
     */
    private AudioTrack mAudioTrack = null;

    // 采样率
    private int sampleRate = 24000;
    /**
     * 声道，输入的单声道
     * private int channelConfig = AudioFormat.CHANNEL_OUT_DEFAULT;
     * 设置音频的录制的声道
     * CHANNEL_OUT_MONO   为单声道
     * CHANNEL_OUT_STEREO 为双声道，
     */
    private int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
    /**
     * 位长16bit，每次采样用多少字节来存
     */
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

    public MyAudioTrack(){
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        // 最小的播放缓存
        int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        // 构造对象
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, channelConfig, audioFormat,
                minBufferSize, AudioTrack.MODE_STREAM);
        // 设置当前音量大小
        mAudioTrack.setStereoVolume(1.0f, 1.0f);
        // 开启播放
        mAudioTrack.play();
    }


    /**
     * 写数据，要用异步
     * @param data
     */
    public void writeData(byte[] data){
        if(null != mAudioTrack) {
            mAudioTrack.write(data, 0, data.length);
        }
    }

    /**
     * 开始播放
     */
    public void play(){
        if(null != mAudioTrack) {

            // 播放回调
            mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack track) {
                    // 标记性的回调
                }

                @Override
                public void onPeriodicNotification(AudioTrack track) {
                    // 周期性的回调
                }
            });

            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
                mAudioTrack.play();

            } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                mAudioTrack.play();

            }else {
                mAudioTrack.play();

            }

        }
    }

    /**
     * 暂停播放
     */
    public void pause(){
        if(null != mAudioTrack) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.pause();
            }
        }
    }

    /**
     * 停止播放
     */
    public void stop(){
        if(null != mAudioTrack) {
            mAudioTrack.stop();
        }
    }

    public void release(){
        if(null != mAudioTrack) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }
}
