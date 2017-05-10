package com.yongyida.robot.voice.utils;

import java.io.FileDescriptor;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.yongyida.robot.voice.app.MyApp;

/**
 * 媒体播放类.
 * @author Administrator
 *
 */
public class MediaPlayUtils {
	private MediaPlayer mPlayer;
		
	private static MediaPlayUtils mMediaPlay;
	
	private CompleteListener listener;
	
	public OnCompletionListener compListener = new OnCompletionListener() {
		
		@Override
		public void onCompletion(MediaPlayer mp) {
			if(listener != null){
				listener.complete();
			}
			sendLedBroad(false);
		}
	};
	
	public MediaPlayUtils(){
		if(mPlayer == null){
			mPlayer = new MediaPlayer();
		}
		
		mPlayer.setOnCompletionListener(compListener);
		mPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				mPlayer.start();
				sendLedBroad(true);
			}
		});
	}
	
	private void sendLedBroad(boolean onOff) {
		Intent intentSpeakOpen = new Intent("com.yongyida.robot.change.BREATH_LED");
		intentSpeakOpen.putExtra("on_off", onOff);
		intentSpeakOpen.putExtra("place", 3);
		intentSpeakOpen.putExtra("colour", 3);
		intentSpeakOpen.putExtra("frequency", 3);
		intentSpeakOpen.putExtra("Permanent", "speak");
		intentSpeakOpen.putExtra("priority", 6);
		MyApp.getContext().sendBroadcast(intentSpeakOpen);
	}

	/**
	 * 播放音乐.
	 * 
	 * @param url
	 */
	public void playMusic(String url) {
		if(mPlayer != null){
			
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
			try {
				mPlayer.setDataSource(url);
				mPlayer.prepareAsync();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				
			} catch (SecurityException e) {
				e.printStackTrace();
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
	}
	
	public void setOnCompleteListener(CompleteListener listener){
		this.listener = listener;
	}

	public void clearOnCompleteListener() {
		this.listener = null;
	}
	
	/*public void playMusic(Context context, String path, CompleteListener listener) {
		try {
			if (mPlayer != null) {
				AssetFileDescriptor fileDescriptor = context.getResources().getAssets().openFd(path);
				
				if (mPlayer.isPlaying()) {
					mPlayer.stop();
				}
				mPlayer.reset();
				mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				mPlayer.prepareAsync();
				
				
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}*/
	
	public void playMusic(Context context, String path) {
		try {
			if (mPlayer != null) {
				AssetFileDescriptor fileDescriptor = context.getResources().getAssets().openFd(path);
				if (mPlayer.isPlaying()) {
					mPlayer.stop();
				}
				mPlayer.reset();
				mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
						fileDescriptor.getStartOffset(),
						fileDescriptor.getLength());
				mPlayer.prepareAsync();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 播放音乐.
	 * 
	 * @param url
	 */
	public void playMusic(int id) {
		if(mPlayer != null){
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
			try {
				mPlayer.setAudioSessionId(id);
				mPlayer.prepareAsync();
				//mPlayer.start();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				
			} catch (SecurityException e) {
				e.printStackTrace();
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
				
			}
			
		}
	}
	/**
	 * 播放音乐.
	 * 
	 * @param url
	 */
	public void playMusic(FileDescriptor fd) {
		if(mPlayer != null){
			if (mPlayer.isPlaying()) {
				mPlayer.stop();
			}
			mPlayer.reset();
			try {
				mPlayer.setDataSource(fd);
				mPlayer.prepareAsync();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				
			} catch (SecurityException e) {
				e.printStackTrace();
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
				
			} catch (IOException e) {
				e.printStackTrace();
				
			}
		}
	}
	
	/**
	 * 暂停.
	 */
	public void pauseMusic(){
		if(mPlayer != null){
			mPlayer.pause();
		}
	}
	
	
	public void stopMusic(){
		if(mPlayer != null){
			mPlayer.stop();
		}
	}
	
	/**
	 * 是否在播放.
	 * @return
	 */
	public boolean isPlaying(){
		if(mPlayer != null){
			return mPlayer.isPlaying();
		}
		return false;
	}
	/**
	 * 获取实例.
	 * @param view
	 * @return
	 */
	public static MediaPlayUtils getInstance(){
		
		if(mMediaPlay == null){
			
			synchronized(MediaPlayUtils.class){
				
				if(mMediaPlay == null){
					
					mMediaPlay = new MediaPlayUtils();
					
				}
			}
		}
		
		return mMediaPlay;
	}
	
	public interface CompleteListener{
		public void complete();
	}

}
