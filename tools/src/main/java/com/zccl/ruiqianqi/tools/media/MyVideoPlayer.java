package com.zccl.ruiqianqi.tools.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

/**
 * Created by ruiqianqi on 2016/11/2 0002.
 */

public class MyVideoPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener{

    /** 全局上下文 */
    private Context context;
    /** 视频播放类 */
    private VideoView videoView;
    /** 多媒体体控制 */
    private MediaController mediaController;
    /** 暂停点 */
    private int mPositionWhenPaused = -1;


    public MyVideoPlayer(VideoView videoView){
        this.videoView = videoView;
        this.context = videoView.getContext().getApplicationContext();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mediaController = new MediaController(videoView.getContext());
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);
    }

    /**
     * 通过ID播放媒体资源
     * R.raw.xxx
     * @param id
     */
    public void playRawId(int id){
        videoView.requestFocus();
        videoView.setVideoURI(Uri.parse("android.resource://"+context.getPackageName()+"/"+id));
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        videoView.start();
    }

    /**
     * 通过路径播放媒体资源
     * 绝对路径 或 网络地址
     * @param videoPath
     */
    public void playPath(String videoPath){
        File file = new File(videoPath);
        if(file.exists()){
            videoView.requestFocus();
            videoView.setVideoPath(file.getAbsolutePath());
            videoView.setOnCompletionListener(this);
            videoView.setOnErrorListener(this);
            videoView.start();
        }
    }

    /**
     * file:///sdcard/msc/100.gif ---------------- 不行
     * file:///android_asset/run.gif ------------- 不行
     *
     * Android的Uri由以下三部分组成："content://"、数据的路径、标示ID(可选)
     * 通过【Uri路径】播放媒体资源
     *
     * @param uriPath
     */
    public void playUriPath(String uriPath){
        Uri uri = Uri.parse(uriPath);
        videoView.requestFocus();
        videoView.setVideoURI(uri);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);
        videoView.start();
    }

    /**
     * 暂停
     */
    public void pause(){
        if(videoView.isPlaying()) {
            mPositionWhenPaused = videoView.getCurrentPosition();
            videoView.pause();
        }
    }

    /**
     * 恢复
     */
    public void resume(){
        if(mPositionWhenPaused >= 0) {
            videoView.seekTo(mPositionWhenPaused);
            videoView.resume();
            mPositionWhenPaused = -1;
        }
    }

    /**
     * 停止
     */
    public void stop(){
        videoView.stopPlayback();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
