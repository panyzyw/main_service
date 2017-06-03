package com.zccl.ruiqianqi.mind.receiver.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.greenrobot.eventbus.EventBus;

public class BatteryReceiver extends BroadcastReceiver {
	private static String TAG = BatteryReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if(null == intent)
			return;
		if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
			int status = intent.getIntExtra("status", BatteryManager.BATTERY_STATUS_UNKNOWN);

			// 电已充满
			if(BatteryManager.BATTERY_STATUS_FULL == status){
				LogUtils.e(TAG, "电已充满");
			}
			// 充电中
			else if(BatteryManager.BATTERY_STATUS_CHARGING == status){
				LogUtils.e(TAG, "充电中");
			}
			// 未充电
			else if(BatteryManager.BATTERY_STATUS_NOT_CHARGING == status){
				LogUtils.e(TAG, "未充电");
			}
			// 放电中
			else if(BatteryManager.BATTERY_STATUS_DISCHARGING == status){
				LogUtils.e(TAG, "放电中");
			}
			// 断电了
			else{
				LogUtils.e(TAG, "断电了");
			}

			// 获取当前电量
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			// 电量的总刻度
			int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
			// 把它转成百分比
			int battery = level * 100 / scale;

			// 怎么充
			int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
			boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
			boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

			// 通知电池电量变化了
			MainBusEvent.BatteryEvent batteryEvent = new MainBusEvent.BatteryEvent();
			batteryEvent.setPowerUsed(false);
			batteryEvent.setBattery(battery);
			batteryEvent.setConn(usbCharge || acCharge);
			EventBus.getDefault().post(batteryEvent);

		}else if(Intent.ACTION_BATTERY_LOW.equals(intent.getAction())){
			LogUtils.e(TAG, "ACTION_BATTERY_LOW");

		}else if(Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())){
			LogUtils.e(TAG, "ACTION_BATTERY_OKAY");

		}
		// 电源连接
		else if(Intent.ACTION_POWER_CONNECTED.equals(intent.getAction())){
			LogUtils.e(TAG, "ACTION_POWER_CONNECTED");

			// 通知电池电源连接
			MainBusEvent.BatteryEvent batteryEvent = new MainBusEvent.BatteryEvent();
			batteryEvent.setPowerUsed(true);
			batteryEvent.setPower(true);
			EventBus.getDefault().post(batteryEvent);
		}
		// 电源断开
		else if(Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())){
			LogUtils.e(TAG, "ACTION_POWER_DISCONNECTED");

			// 通知电池电源断开
			MainBusEvent.BatteryEvent batteryEvent = new MainBusEvent.BatteryEvent();
			batteryEvent.setPowerUsed(true);
			batteryEvent.setPower(false);
			EventBus.getDefault().post(batteryEvent);
		}
	}
}
