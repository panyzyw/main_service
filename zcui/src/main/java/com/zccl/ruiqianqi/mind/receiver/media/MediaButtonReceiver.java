package com.zccl.ruiqianqi.mind.receiver.media;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.view.KeyEvent;

import com.zccl.ruiqianqi.tools.LogUtils;

/**
 * Created by ruiqianqi on 2017/3/6 0006.
 */

public class MediaButtonReceiver extends BroadcastReceiver {

    // 类标志
    private static String TAG = MediaButtonReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null == intent)
            return;
        LogUtils.e(TAG, "intent is " + intent.getAction());

        String action = intent.getAction();

        // 远程多媒体控制，耳机线，蓝牙，控制音量大小等
        if(Intent.ACTION_MEDIA_BUTTON.equals(action)){
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
                // Handle key press.
                // 获得按键字节码
                int keyCode = event.getKeyCode();
                // 按下 / 松开 按钮
                int keyAction = event.getAction();
                // 获得事件的时间
                long downtime = event.getEventTime();

                // 这些都是可能的按键码，打印出来用户按下的键
                if(KeyEvent.KEYCODE_MEDIA_NEXT == keyCode){

                }
                else if(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == keyCode){

                }
                else if(KeyEvent.KEYCODE_HEADSETHOOK == keyCode){

                }
                else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode){

                }
                else if(KeyEvent.KEYCODE_MEDIA_STOP == keyCode){

                }
            }
        }

        // 耳机设备的插入和拨出
        else if(Intent.ACTION_HEADSET_PLUG.equals(action)){
            if (intent.hasExtra("state")){
                // 未接入耳麦设备
                if (intent.getIntExtra("state", 0) == 0){

                }
                // 接入耳麦设备
                else{

                }
            }
        }

        // 对于蓝牙耳机，监听BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED系统广播
        else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            // Bluetooth headset is now disconnected
            if(BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {

            }
            // 蓝牙耳机连接中
            else if(BluetoothProfile.STATE_CONNECTING == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {

            }
            // 蓝牙耳机连接上
            else if(BluetoothProfile.STATE_CONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {

            }
        }

        // 监听Android的系统广播AudioManager.ACTION_AUDIO_BECOMING_NOISY，但是这个广播只是针对有线耳机，
        // 或者无线耳机的手机断开连接的事件，监听不到有线耳机和蓝牙耳机的接入
        else if(AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)){

        }

    }
}
