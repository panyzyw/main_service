package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.RobotStateData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.utils.LogUtils;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;


/**
 * 停止循环录音.
 *
 * @author Administrator
 */
public class StateRecycleMute extends BaseMessage {

    public static int i = 0;

    private static MediaPlayer player = new MediaPlayer();

    private OnPreparedListener prepared = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            player.start();
			sendLedBroad(true);
        }
    };

    private OnCompletionListener listener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {
			sendLedBroad(false);
        }
    };

	private void sendLedBroad(boolean onOff) {
        Intent intentSpeakOpen = new Intent("com.yongyida.robot.change.BREATH_LED");
        intentSpeakOpen.putExtra("on_off", onOff);
        intentSpeakOpen.putExtra("place", 3);
        intentSpeakOpen.putExtra("colour", 3);
        intentSpeakOpen.putExtra("frequency", 2);
        intentSpeakOpen.putExtra("Permanent", "speak");
        intentSpeakOpen.putExtra("priority", 6);
        context.sendBroadcast(intentSpeakOpen);
    }

    @Override
    public void execute() {

        try {

            Map<String, String> map2;
            map2 = new HashMap<String, String>();
            map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
            map2.put(GeneralData.RESULT, GeneralData.TOUCHHEAD);
            SendBroadcastUtils.sendBroad(context, map2);
            map2 = null;

            String[] headVoice = VoiceData.muteVoice;

            int randTouchHead = random.nextInt(headVoice.length);
            if (factory != null) {
                factory.setFactory(VoiceUnderstand.getInstance(context));
                factory.parseStop();
            }
            if (!player.isPlaying()) {
                if (mPlayer.isPlaying()) {
					sendLedBroad(false);
                    mPlayer.stopMusic();
                }
                playMusic(context, headVoice[randTouchHead]);

            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void playMusic(Context context, String path) {
        try {
            if (player != null) {
                AssetFileDescriptor fileDescriptor = context.getResources().getAssets().openFd(path);
                player.setOnCompletionListener(listener);
                player.setOnPreparedListener(prepared);
                player.reset();
                player.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                player.prepareAsync();

            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
