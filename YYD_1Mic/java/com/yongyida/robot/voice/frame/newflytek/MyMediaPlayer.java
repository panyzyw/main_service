package com.yongyida.robot.voice.frame.newflytek;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by ruiqianqi on 2016/7/20 0020.
 */
public class MyMediaPlayer {
    /** 类标识 */
    private static String TAG = MyMediaPlayer.class.getSimpleName();

    private MediaPlayer mediaPlayer;
    /** 生命周期最长的上下文 */
    private Context context;

    public MyMediaPlayer(Context context){
        this.context = context;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    /**
     * 播放Assets音乐文件
     * @param path
     * @param playerLisenter
     */
    public void playMusic(String path, final IPlayerLisenter playerLisenter) {
        try {
            if (mediaPlayer != null) {
                AssetFileDescriptor fileDescriptor = context.getResources().getAssets().openFd(path);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        if(playerLisenter!=null){
                            playerLisenter.startPlay(mp);
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if(playerLisenter!=null){
                            playerLisenter.endPlay(mp);
                        }
                    }
                });

                if (isPlaying()) {
                    stopMusic();
                }

                mediaPlayer.reset();
                mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                mediaPlayer.prepareAsync();
                //mediaPlayer.setLooping(true);

                //mediaPlayer.prepare();
                //mediaPlayer.start();

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停音乐
     */
    public void pauseMusic(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
        }
    }

    /**
     * 停止音乐
     */
    public void stopMusic(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
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
     * 播放监听接口
     */
    public interface IPlayerLisenter{
        void startPlay(MediaPlayer mp);
        void endPlay(MediaPlayer mp);
    }
}
