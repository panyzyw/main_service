package com.yongyida.robot.voice.robot;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.yongyida.robot.voice.base.BaseMessage;
import com.yongyida.robot.voice.data.GeneralData;
import com.yongyida.robot.voice.data.IntentData;
import com.yongyida.robot.voice.data.VoiceData;
import com.yongyida.robot.voice.frame.iflytek.VoiceUnderstand;
import com.yongyida.robot.voice.utils.SendBroadcastUtils;

/**
 * 摸头.
 *
 * @author Administrator
 */
public class TouchHead extends BaseMessage {

    public static int i = 0;

    private static MediaPlayer player = new MediaPlayer();

    private OnPreparedListener prepared = new OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            player.start();
        }
    };

    private OnCompletionListener listener = new OnCompletionListener() {

        @Override
        public void onCompletion(MediaPlayer mp) {

            mainServiceInfo.setComplete(i);
            if (factory != null) {
                factory.setFactory(VoiceUnderstand.getInstance(context));

                VoiceUnderstand.setFirst();

                factory.parseStart(null);
            }
        }
    };


    @Override
    public void execute() {
        Log.d("jlog", "run");
        try {
            i++;
            mainServiceInfo.setBeforComplete(i);

            Map<String, String> map2;
            map2 = new HashMap<String, String>();
            map2.put(GeneralData.ACTION, IntentData.INTENT_STOP);
            map2.put(GeneralData.RESULT, GeneralData.TOUCHHEAD);
            map2.put(GeneralData.FROM, GeneralData.INTENT_TOUCH_HEAD);
            SendBroadcastUtils.sendBroad(context, map2);
            map2 = null;
            String[] headVoice = VoiceData.headVoice;
            mainServiceInfo.setTouchHead(true);
            mainServiceInfo.setFirstRecord(true);
            int randTouchHead = random.nextInt(headVoice.length);
            if (factory != null) {
                factory.setFactory(VoiceUnderstand.getInstance(context));
                factory.parseStop();
            }
            if (!player.isPlaying()) {
                if (mPlayer.isPlaying()) {
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
                //Log.d("jlog", "path:" + path);
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
