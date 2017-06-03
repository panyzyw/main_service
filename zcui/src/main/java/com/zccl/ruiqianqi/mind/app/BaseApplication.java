package com.zccl.ruiqianqi.mind.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.tencent.bugly.Bugly;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.LogUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by ruiqianqi on 2016/7/19 0019.
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // 多DEX，这行代码必须加上
        MultiDex.install(this);

        //参数1：上下文对象
        //参数2：注册时申请的APPID
        //参数3：是否开启debug模式，true表示打开debug模式，false表示关闭调试模式
        //Bugly.init(getApplicationContext(), "5c3ac0f156", false);

        //未捕获的错误处理机制
        CrashHandler.getInstance().init(this);

        //初始化，检查SD卡
        FileUtils.existSDCard(this);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LogUtils.e(this.getClass().getSimpleName(), "onConfigurationChanged: "+ newConfig);

        // RxBus
        /*
        Bundle bundle = new Bundle();
        bundle.putString("type", "language");
        bundle.putString("language", newConfig.locale.getLanguage());
        MyRxBus.getDefault().post(bundle);
        */

        // EventBus
        MainBusEvent.LanguageEvent languageEvent = new MainBusEvent.LanguageEvent();
        languageEvent.setLanguage(newConfig.locale.getLanguage());
        EventBus.getDefault().post(languageEvent);

    }

    /**
     * 返回应用上下文
     * @return
     */
    public static Context getContext(){
        return instance.getApplicationContext();
    }

}
