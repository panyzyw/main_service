package com.zccl.ruiqianqi.tools;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by ruiqianqi on 2016/9/24 0024.
 */

public class VoiceUtils {

    /**
     * 渐进式增加音量
     * @param context
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_MUSIC}
     * {@link AudioManager#STREAM_SYSTEM}
     * {@link AudioManager#STREAM_ALARM}
     * {@link AudioManager#STREAM_RING}
     * {@link AudioManager#STREAM_VOICE_CALL}
     * {@link AudioManager#STREAM_NOTIFICATION}
     * {@link AudioManager#STREAM_DTMF}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeUp(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        /**
         * streamType
         * STREAM_MUSIC         手机音乐的声音
         * STREAM_SYSTEM        手机系统的声音
         * STREAM_ALARM         手机闹铃声音
         * STREAM_RING          电话铃声的声音
         * STREAM_VOICE_CALL    语音电话的声音
         * STREAM_NOTIFICATION  系统提示的声音
         * STREAM_DTMF          DTMF音调的声音
         *
         * ADJUST_LOWER 降低音量
         * ADJUST_RAISE 升高音量
         * ADJUST_SAME 保持不变,这个主要用于向用户展示当前的音量
         *
         * FLAG_PLAY_SOUND 调整音量时播放声音
         * FLAG_SHOW_UI 调整时显示音量条,就是按音量键出现的那个
         * 0表示什么也没有
         */
        am.adjustStreamVolume(streamType, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_RAISE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        // 两个参数，音量类型使用默认的
        //am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 渐进式降低音量
     * @param context
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_MUSIC}
     * {@link AudioManager#STREAM_SYSTEM}
     * {@link AudioManager#STREAM_ALARM}
     * {@link AudioManager#STREAM_RING}
     * {@link AudioManager#STREAM_VOICE_CALL}
     * {@link AudioManager#STREAM_NOTIFICATION}
     * {@link AudioManager#STREAM_DTMF}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeDown(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        // 可以明确指定音量设置类型
        am.adjustStreamVolume(streamType, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        am.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        am.adjustStreamVolume(AudioManager.STREAM_DTMF, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        // 两个参数，音量类型使用默认的
        //am.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 音量调到最大
     * @param context
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_MUSIC}
     * {@link AudioManager#STREAM_SYSTEM}
     * {@link AudioManager#STREAM_ALARM}
     * {@link AudioManager#STREAM_RING}
     * {@link AudioManager#STREAM_VOICE_CALL}
     * {@link AudioManager#STREAM_NOTIFICATION}
     * {@link AudioManager#STREAM_DTMF}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void setMaxVolume(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        am.setStreamVolume(type, am.getStreamMaxVolume(type), AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 音量调到最小
     * @param context
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_MUSIC}
     * {@link AudioManager#STREAM_SYSTEM}
     * {@link AudioManager#STREAM_ALARM}
     * {@link AudioManager#STREAM_RING}
     * {@link AudioManager#STREAM_VOICE_CALL}
     * {@link AudioManager#STREAM_NOTIFICATION}
     * {@link AudioManager#STREAM_DTMF}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void setMinVolume(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        am.setStreamVolume(type, 1, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 音量增加几格
     * @param context
     * @param step
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_MUSIC}
     * {@link AudioManager#STREAM_SYSTEM}
     * {@link AudioManager#STREAM_ALARM}
     * {@link AudioManager#STREAM_RING}
     * {@link AudioManager#STREAM_VOICE_CALL}
     * {@link AudioManager#STREAM_NOTIFICATION}
     * {@link AudioManager#STREAM_DTMF}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeUpByStep(Context context, int step, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        int max = am.getStreamMaxVolume(type);
        int cur = am.getStreamVolume(type);
        am.setStreamVolume(type, cur + step < max ? cur + step : max, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 音量减小几格
     * @param context
     * @param step
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_MUSIC}
     * {@link AudioManager#STREAM_SYSTEM}
     * {@link AudioManager#STREAM_ALARM}
     * {@link AudioManager#STREAM_RING}
     * {@link AudioManager#STREAM_VOICE_CALL}
     * {@link AudioManager#STREAM_NOTIFICATION}
     * {@link AudioManager#STREAM_DTMF}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeDownByStep(Context context, int step, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        int cur = am.getStreamVolume(type);
        am.setStreamVolume(type, cur-step > 0 ? cur - step : 0, AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
    }

}
