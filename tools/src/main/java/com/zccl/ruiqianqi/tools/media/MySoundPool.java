package com.zccl.ruiqianqi.tools.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.FileDescriptor;

/**
 * Created by ruiqianqi on 2016/12/29 0029.
 * 第一个参数指定支持多少个声音；
 * 第二个参数指定声音类型；
 * 第三个参数指定声音品质。
 * SoundPool(int maxStreams, int streamType, int srcQuality)：
 */
public class MySoundPool {

    // 全局上下文
    private Context mContext;
    // 声音池
    private SoundPool mPool;

    private int currentPlayId;

    public MySoundPool(Context context){
        this.mContext = context.getApplicationContext();
        mPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
    }

    /*
    从 resld 所对应的资源加载声音。
    int load(Context context, int resld, int priority)：
    加载 fd 所对应的文件的offset开始、长度为length的声音。
    int load(FileDescriptor fd, long offset, long length, int priority)：
    从afd 所对应的文件中加载声音。
    int load(AssetFileDescriptor afd, int priority)：
    从path 对应的文件去加载声音。
    int load(String path, int priority)：
    */

    /**
     * 加载音频
     * @param id
     * @return a sound ID. This value can be used to play or unload the sound.
     */
    public void load(int id){
        mPool.load(mContext, id, 1);
    }

    /**
     * 加载音频
     * @param filePath
     * @return a sound ID. This value can be used to play or unload the sound.
     */
    public void load(String filePath){
        mPool.load(filePath, 1);
    }

    /**
     * 加载音频
     * @param fd
     * @param offset
     * @param length
     * @return a sound ID. This value can be used to play or unload the sound.
     */
    public void load(FileDescriptor fd, long offset, long length){
        mPool.load(fd, offset, length, 1);
    }

    /**
     * 加载音频
     * @param afd
     * @return a sound ID. This value can be used to play or unload the sound.
     */
    public void load(AssetFileDescriptor afd){
        mPool.load(afd, 1);
    }

    /**
     * 播放音频，
     * 第二个参数为左声道音量;
     * 第三个参数为右声道音量;
     * 第四个参数为优先级；
     * 第五个参数为循环次数，0不循环，-1循环;
     * 第六个参数为速率，速率最低0.5，最高为2，1代表正常速度
     */
    public void play(int soundID, float leftVolume, float rightVolume,
                     int priority, int loop, float rate){
        currentPlayId = mPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
    }

    /**
     *  暂停播放
     * @param streamID a streamID returned by the play() function
     */
    public void pause(int streamID){
        mPool.pause(streamID);
    }

    /**
     * 停止播放
     * @param streamID a streamID returned by the play() function
     */
    public void stop(int streamID){
        mPool.stop(streamID);
    }

}
