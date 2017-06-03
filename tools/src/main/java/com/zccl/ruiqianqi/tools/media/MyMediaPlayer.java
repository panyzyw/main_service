package com.zccl.ruiqianqi.tools.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.media.MediaPlayer.MEDIA_ERROR_SERVER_DIED;

/**
 * Created by ruiqianqi on 2016/7/20 0020.
 * 播放声音可以用MediaPlayer和AudioTrack，两者都提供了java API供应用开发者使用。虽然都可以播放声音，但两者还是有很大的区别的。
 * 其中最大的区别是MediaPlayer可以播放多种格式的声音文件，例如MP3，AAC，WAV，OGG，MIDI等。
 * MediaPlayer会在framework层创建对应的音频解码器。
 * 而AudioTrack只能播放已经解码的PCM流，如果是文件的话只支持wav格式的音频文件，因为wav格式的音频文件大部分都是PCM流。
 * AudioTrack不创建解码器，所以只能播放不需要解码的wav文件。
 */
public class MyMediaPlayer {

    /** 类标识 */
    private static String TAG = MyMediaPlayer.class.getSimpleName();
    /** 生命周期最长的上下文 */
    private Context mContext;
    /** 媒体播放类 */
    private MediaPlayer mediaPlayer;
    // 回调播放进度
    private Handler mHandler;
    // 播放回调接口
    private IPlayerListener mPlayerListener;

    // 播放进度回调接口
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(mPlayerListener!=null){
                mPlayerListener.OnProgress(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration());

                // 计算出下次更新进度条的时间间隔(1秒以内)
                //long remaining = 1000 - (mediaPlayer.getCurrentPosition() % 1000);
                mHandler.postDelayed(runnable, 300);
            }
        }
    };

    // 记录播放出错日志
    private MediaTest mediaTest;

    public MyMediaPlayer(Context context){
        this.mContext = context.getApplicationContext();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.setLooping(false);
        mHandler = new Handler(Looper.getMainLooper());

        mediaTest = new MediaTest(mContext);
        mediaTest.create();
    }

    /**
     * 同步播放Assets音乐文件
     * @param afd
     * @return
     */
    public boolean playAssetsSound(AssetFileDescriptor afd) {
        boolean result = true;
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 注册回调接口
     * @param playerListener
     */
    private void setPlayerListener(IPlayerListener playerListener){
        this.mPlayerListener = playerListener;

        if (mediaPlayer != null) {

            // 资源缓存
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, final int percent) {
                    LogUtils.e(TAG, "percent = " + percent);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(null != mPlayerListener){
                                mPlayerListener.OnBufferUpdate(percent);
                            }
                        }
                    });
                }
            });

            // 开始播放
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mHandler.post(runnable);

                    if (mPlayerListener != null) {
                        mPlayerListener.OnPlaying(mp);
                    }
                }
            });

            // 播放完成
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mPlayerListener != null) {
                        mPlayerListener.OnPlayEnd(mp);
                    }
                    mHandler.removeCallbacks(runnable);
                }
            });

            // 播放出错
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    String str = System.currentTimeMillis() + "\t" + what + "\t" + extra;
                    mediaTest.write(str);
                    mediaTest.close();

                    // Media server died. In this case, the application must release the
                    // MediaPlayer object and instantiate a new one.
                    if(MEDIA_ERROR_SERVER_DIED == what){
                        /*
                        release();
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setScreenOnWhilePlaying(true);
                        */

                        mediaPlayer.reset();

                    }

                    if (mPlayerListener != null) {
                        mPlayerListener.OnPlayError(null, what, extra);
                    }
                    mHandler.removeCallbacks(runnable);
                    return true;
                }
            });
        }
    }
    /**
     * 异步播放Assets音乐文件
     * @param path
     * @param playerListener
     */
    public void playAssetsMusic(String path, IPlayerListener playerListener) {
        try {
            if (mediaPlayer != null) {
                AssetFileDescriptor fileDescriptor = mContext.getResources().getAssets().openFd(path);

                // 注册回调接口
                setPlayerListener(playerListener);

                if (isPlaying()) {
                    stopMusic();
                }

                mediaPlayer.reset();
                mediaPlayer.setDataSource(
                        fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mediaPlayer.prepareAsync();
                //mediaPlayer.setLooping(true);

                //mediaPlayer.prepare();
                //mediaPlayer.start();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放网络与本地路径资源
     * @param path
     * @param playerListener
     */
    public void playPath(String path, IPlayerListener playerListener){
        //File file = new File(path);
        //if(file.exists())
        {
            try {
                // 注册回调接口
                setPlayerListener(playerListener);

                if (isPlaying()) {
                    stopMusic();
                }

                mediaPlayer.reset();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过【Uri路径】播放媒体资源
     * Android的Uri由以下三部分组成："content://"、数据的路径、标示ID(可选)
     *
     * @param uriPath
     * @param playerListener
     */
    public void playUriPath(String uriPath, IPlayerListener playerListener){
        try {
            Uri uri = Uri.parse(uriPath);

            // 注册回调接口
            setPlayerListener(playerListener);

            if (isPlaying()) {
                stopMusic();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mContext, uri);
            mediaPlayer.prepareAsync();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放资源ID
     * @param id
     * @param playerListener
     */
    public void playRawId(int id, IPlayerListener playerListener){

        // 注册回调接口
        setPlayerListener(playerListener);

        if (isPlaying()) {
            stopMusic();
        }

        mediaPlayer.reset();
        // 重新设置要播放的音频
        mediaPlayer = MediaPlayer.create(mContext, id);
        mediaPlayer.prepareAsync();
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isPlaying(){
        if(mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 返回进度百分比
     * @return
     */
    public int getProgress() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration() * 100;
        }
        return 0;
    }

    /**
     * 百分比，0~100
     * @param percent
     */
    public void setProgress(int percent) {
        if (mediaPlayer == null) {
            return;
        }
        int seek = (int)((float)percent / 100 * mediaPlayer.getDuration());
        // the offset in milliseconds from the start to seek to
        seekTo(seek);
    }

    /**
     * 得到当前音乐时长
     * @return
     */
    public int getMusicLength() {
        if(mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 指定位置开始播放
     * @param seek
     */
    public void seekTo(int seek){
        if(mediaPlayer != null){
            mediaPlayer.seekTo(seek);
        }
    }

    /**
     * 开始音乐
     */
    public void startMusic(){
        if(mediaPlayer != null){
            mediaPlayer.start();
            mHandler.post(runnable);
        }
    }

    /**
     * 暂停音乐
     */
    public void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            mHandler.removeCallbacks(runnable);
        }

    }

    /**
     * 停止音乐
     */
    public void stopMusic(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mHandler.removeCallbacks(runnable);
        }

    }

    /**
     * 播放资源
     */
    public void release(){
        mHandler.removeCallbacks(runnable);
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 播放监听接口
     */
    public interface IPlayerListener{
        // 1.无音乐播放监听接口
        int NO_LISTENER = 1;
        // 2.没有播放器
        int NO_PLAYER = 2;
        // 3.音乐列表为空
        int NO_MUSIC_LIST = 3;

        void OnBufferUpdate(int percent);
        void OnPlayLoad(MusicPlayLoad musicPlayLoad);
        void OnPlaying(MediaPlayer mp);
        void OnProgress(int curTimePos, int duration);
        void OnPlayEnd(MediaPlayer mp);
        void OnPlayError(Throwable e, int what, int extra);
    }

    /**
     * 播放加载类
     */
    public static class MusicPlayLoad{
        public boolean isOneSong;
        public int mIndex;
        public String mTitle;
        public String mPlayUrl;
    }

}
