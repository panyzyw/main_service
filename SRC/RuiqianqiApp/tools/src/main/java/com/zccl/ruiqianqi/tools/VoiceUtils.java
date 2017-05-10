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
     * {@link AudioManager#STREAM_VOICE_CALL},
     * {@link AudioManager#STREAM_SYSTEM},
     * {@link AudioManager#STREAM_RING},
     * {@link AudioManager#STREAM_MUSIC} or
     * {@link AudioManager#STREAM_ALARM}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeUp(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        /**
         * streamType
         * STREAM_ALARM      警报
         * STREAM_MUSIC      音乐回放即媒体音量
         * STREAM_VOICE_CALL 通话
         * STREAM_SYSTEM     系统
         * STREAM_RING       铃声
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
        // 两个参数，音量类型使用默认的
        am.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 渐进式降低音量
     * @param context
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_VOICE_CALL},
     * {@link AudioManager#STREAM_SYSTEM},
     * {@link AudioManager#STREAM_RING},
     * {@link AudioManager#STREAM_MUSIC} or
     * {@link AudioManager#STREAM_ALARM}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeDown(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        // 可以明确指定音量设置类型
        am.adjustStreamVolume(streamType, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        // 两个参数，音量类型使用默认的
        am.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 音量调到最大
     * @param context
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_VOICE_CALL},
     * {@link AudioManager#STREAM_SYSTEM},
     * {@link AudioManager#STREAM_RING},
     * {@link AudioManager#STREAM_MUSIC} or
     * {@link AudioManager#STREAM_ALARM}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void setMaxVolume(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        am.setStreamVolume(type, am.getStreamMaxVolume(type), AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 音量调到最小
     * @param context
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_VOICE_CALL},
     * {@link AudioManager#STREAM_SYSTEM},
     * {@link AudioManager#STREAM_RING},
     * {@link AudioManager#STREAM_MUSIC} or
     * {@link AudioManager#STREAM_ALARM}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void setMinVolume(Context context, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        am.setStreamVolume(type, 0, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 音量增加几格
     * @param context
     * @param step
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_VOICE_CALL},
     * {@link AudioManager#STREAM_SYSTEM},
     * {@link AudioManager#STREAM_RING},
     * {@link AudioManager#STREAM_MUSIC} or
     * {@link AudioManager#STREAM_ALARM}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeUpByStep(Context context, int step, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        int max = am.getStreamMaxVolume(type);
        int cur = am.getStreamVolume(type);
        am.setStreamVolume(type, cur + step < max ? cur + step : max, AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 音量减小几格
     * @param context
     * @param step
     * @param streamType
     * One of
     * {@link AudioManager#STREAM_VOICE_CALL},
     * {@link AudioManager#STREAM_SYSTEM},
     * {@link AudioManager#STREAM_RING},
     * {@link AudioManager#STREAM_MUSIC} or
     * {@link AudioManager#STREAM_ALARM}
     *
     * {@link AudioManager#USE_DEFAULT_STREAM_TYPE}
     */
    public static void volumeDownByStep(Context context, int step, int streamType){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        //AudioManager.USE_DEFAULT_STREAM_TYPE
        int type = streamType;
        int cur = am.getStreamVolume(type);
        am.setStreamVolume(type, cur-step > 0 ? cur - step : 0, AudioManager.FLAG_PLAY_SOUND);
    }

}
