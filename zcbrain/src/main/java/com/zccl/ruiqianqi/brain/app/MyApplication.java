package com.zccl.ruiqianqi.brain.app;

import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.zccl.ruiqianqi.brain.service.MainService;
import com.zccl.ruiqianqi.brain.voice.RobotVoice;
import com.zccl.ruiqianqi.socket.localsocket.LocalServer;
import com.zccl.ruiqianqi.storage.db.MyDbFlow;
import com.zccl.ruiqianqi.mind.app.BaseApplication;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;

/**
 * Created by ruiqianqi on 2017/3/2 0002.
 *
 * 本地socket，服务器监听线程
 * {@link com.zccl.ruiqianqi.presentation.presenter.LocalPresenter#initSome}
 *
 * 本地socket，新连接处理线程
 * {@link LocalServer#run}
 *
 * 监听端点异常，语音重新输入理解线程
 * {@link RobotVoice#init}
 */

public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // 内存分析工具
        LeakCanary.install(this);

        // 参数1：上下文对象
        // 参数2：注册时申请的APPID
        // 参数3：是否开启debug模式，true表示打开debug模式，false表示关闭调试模式
        Bugly.init(getApplicationContext(), "5c3ac0f156", false);

        // 初始化语音服务
        MindPresenter.getInstance().initSpeech(new RobotVoice(this));

        // 初始化DBFlow数据库操作
        MyDbFlow.initDbFlow(this);

        // 初始化一些配置
        MyConfigure.init(this);

        // 开启主服务
        MainService.startMyService(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtils.f("mainservice", System.currentTimeMillis() + "：onTerminate\n");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtils.f("mainservice", System.currentTimeMillis() + "：onLowMemory\n");
    }
}
