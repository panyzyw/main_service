package com.zccl.ruiqianqi.presentation.presenter;

import com.zccl.ruiqianqi.config.MyConfig;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.utils.AppUtils;
import com.zccl.ruiqianqi.utils.LedUtils;

import static com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter.BatteryState.DEFAULT_POWER;
import static com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter.BatteryState.FULL_POWER;
import static com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter.BatteryState.FULL_POWER_CHARGE;
import static com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter.BatteryState.LOW_POWER;
import static com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter.BatteryState.LOW_POWER_CHARGE;
import static com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter.BatteryState.NORMAL_POWER;
import static com.zccl.ruiqianqi.presentation.presenter.BatteryPresenter.BatteryState.NORMAL_POWER_CHARGE;

/**
 * Created by ruiqianqi on 2017/3/24 0024.
 */

public class BatteryPresenter extends BasePresenter {

    // 类标志
    private static String TAG = BatteryPresenter.class.getSimpleName();
    // 单例引用
    private static BatteryPresenter instance;

    protected enum BatteryState {
        DEFAULT_POWER,
        LOW_POWER,
        LOW_POWER_CHARGE,
        NORMAL_POWER,
        NORMAL_POWER_CHARGE,
        FULL_POWER,
        FULL_POWER_CHARGE,
    };

    // 电池状态记录
    private BatteryState batteryState = DEFAULT_POWER;

    /**
     * 构造方法
     */
    private BatteryPresenter(){
        init();
    }

    /**
     * 用这个的话，instance不需要用volatile修饰
     *
     * @return
     */
    public static BatteryPresenter getInstance() {
        if (instance == null) {
            synchronized (BatteryPresenter.class) {
                BatteryPresenter temp = instance;
                if (temp == null) {
                    temp = new BatteryPresenter();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    private void init(){

    }

    /**
     * 处理电量相关信息
     * @param batteryEvent
     */
    public void dealWithBattery(MainBusEvent.BatteryEvent batteryEvent){
        /*
        LogUtils.e(TAG, "battery = " + batteryEvent.getBattery());
        LogUtils.e(TAG, "isConn = " + batteryEvent.isConn());
        LogUtils.e(TAG, "isPower = " + batteryEvent.isPower());
        */

        // 工厂模式中不处理信号灯
        StatePresenter sp = StatePresenter.getInstance();
        if(sp.isFactory())
            return;

        if(batteryEvent.isPowerUsed()){
            // 电源连接
            if(batteryEvent.isPower()){
                AppUtils.endEmotion(mContext);
            }
            // 电源断开
            else {

            }
        }else {
            if(batteryEvent.getBattery() >= 100){
                // USB连接
                if(batteryEvent.isConn()){
                    if(FULL_POWER_CHARGE != batteryState) {

                        batteryState = FULL_POWER_CHARGE;
                    }
                    LedUtils.startFullPowerChargeLed(mContext);
                }
                // USB断开
                else {
                    if(FULL_POWER != batteryState) {

                        batteryState = FULL_POWER;
                    }
                    LedUtils.startFullPowerLed(mContext);
                }
            }else if(batteryEvent.getBattery() > 15){
                // USB连接
                if(batteryEvent.isConn()){
                    if(NORMAL_POWER_CHARGE != batteryState) {

                        batteryState = NORMAL_POWER_CHARGE;
                    }
                    LedUtils.startNormalPowerChargeLed(mContext);
                }
                // USB断开
                else {
                    if(NORMAL_POWER != batteryState) {

                        batteryState = NORMAL_POWER;
                    }
                    LedUtils.startNormalPowerLed(mContext);
                }
            }else {
                // USB连接
                if(batteryEvent.isConn()){
                    if(LOW_POWER_CHARGE != batteryState) {

                        batteryState = LOW_POWER_CHARGE;
                    }
                    LedUtils.startLowPowerChargeLed(mContext);
                }
                // USB断开
                else {
                    if(LOW_POWER != batteryState) {

                        // 没插电源，发送饿了表情
                        AppUtils.sendEmotionHungry(mContext);
                        batteryState = LOW_POWER;
                    }
                    LedUtils.startLowPowerLed(mContext);
                }
            }
        }
    }
}
