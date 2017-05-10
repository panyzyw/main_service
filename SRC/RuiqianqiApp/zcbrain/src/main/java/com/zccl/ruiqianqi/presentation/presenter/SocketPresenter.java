package com.zccl.ruiqianqi.presentation.presenter;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.domain.model.ServerAddr;
import com.zccl.ruiqianqi.storage.SocketRepository;
import com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent;
import com.zccl.ruiqianqi.domain.tasks.remotetask.BaseTask;
import com.zccl.ruiqianqi.domain.tasks.remotetask.ControlOffTask;
import com.zccl.ruiqianqi.domain.tasks.remotetask.ControlOnTask;
import com.zccl.ruiqianqi.domain.tasks.remotetask.FlushTask;
import com.zccl.ruiqianqi.domain.tasks.remotetask.ForwardTask;
import com.zccl.ruiqianqi.domain.tasks.remotetask.LoginTask;
import com.zccl.ruiqianqi.domain.tasks.remotetask.PushTask;
import com.zccl.ruiqianqi.domain.interactor.ISocketInteractor;
import com.zccl.ruiqianqi.domain.interactor.socket.SocketInteractor;
import com.zccl.ruiqianqi.mind.eventbus.MainBusEvent;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.storage.db.MyDbFlow;
import com.zccl.ruiqianqi.storage.db.ServerBean;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.PhoneUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_FAILURE;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_ING;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_SUCCESS;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.IdleEvent.REQUEST_HEART;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_DATA;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_HEART;
import static com.zccl.ruiqianqi.socket.eventbus.SocketBusEvent.SocketCarrier.RESPONSE_MSG;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_CONTROL_OFF;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_CONTROL_ON;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_FLUSH;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_LOGIN;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_MEDIA_FORWARD_2_VIDEO;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_ORDER_PUSH;

/**
 * Created by ruiqianqi on 2017/1/14 0014.
 * JAVA会默认调用父类的无参数的构造函数
 */
public class SocketPresenter extends BasePresenter {
    // 类标志
    private static String TAG = SocketPresenter.class.getSimpleName();
    // 单例引用
    private static SocketPresenter instance;
    // 网络连接USE CASE
    private ISocketInteractor mSocketInteractor;
    // 机器人电量
    private String battery = null;
    // 机器人名字
    private String robotName = null;
    // 网络任务集合
    private Map<String, Class<? extends BaseTask>> taskMap;


    /**
     * 构造方法
     */
    private SocketPresenter(){
        init();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static SocketPresenter getInstance() {
        if (instance == null) {
            synchronized (SocketPresenter.class) {
                SocketPresenter temp = instance;
                if (temp == null) {
                    temp = new SocketPresenter();
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
        mSocketInteractor = new SocketInteractor(mContext, new SocketRepository());
        mSocketInteractor.setSwitchCallback2P(new MySwitchListener());

        taskMap = new HashMap<>();
        taskMap.put(A_LOGIN, LoginTask.class);
        taskMap.put(A_FLUSH, FlushTask.class);
        taskMap.put(A_CONTROL_ON, ControlOnTask.class);
        taskMap.put(A_CONTROL_OFF, ControlOffTask.class);
        taskMap.put(A_ORDER_PUSH, PushTask.class);
        // 转发的指令
        taskMap.put(A_MEDIA_FORWARD_2_VIDEO, ForwardTask.class);

        // 注册事件总线，电池电量变化、机器人名字改变；
        EventBus.getDefault().register(this);
    }


    /**************************************【对外提供的方法】**************************************/
    /**
     * 切换到服务器类型，并连接到服务器
     * @param flagVersion  切换到指定版本
     * {@link PersistPresenter#FORMAL_SERVER }
     * {@link PersistPresenter#DEBUG_SERVER }
     * {@link PersistPresenter#DEV_SERVER }
     */
    public void connectToServer(String flagVersion){
        // 设置当前网络状态
        StatePresenter sp = StatePresenter.getInstance();
        sp.setNetConnected(PhoneUtils.isNetConnected(mContext));
        mSocketInteractor.switchToServer(flagVersion, ISocketInteractor.CONN_TYPE.INIT_CONNECT);
    }

    /**
     * 切换到服务器类型，并连接到服务器
     * @param flagVersion  切换到指定版本
     * {@link PersistPresenter#FORMAL_SERVER }
     * {@link PersistPresenter#DEBUG_SERVER }
     * {@link PersistPresenter#DEV_SERVER }
     */
    public void switchToServer(String flagVersion){
        // 设置当前网络状态
        StatePresenter sp = StatePresenter.getInstance();
        sp.setNetConnected(PhoneUtils.isNetConnected(mContext));
        mSocketInteractor.switchToServer(flagVersion, ISocketInteractor.CONN_TYPE.SWITCH_CONNECT);
    }

    /**********************************【事件总线的处理】******************************************/
    /**
     * 接收到电池电量变化事件，从 BatteryReceiver 的 onReceive 发过来的
     * @param batteryEvent
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 10)
    public void OnBatteryEvent(MainBusEvent.BatteryEvent batteryEvent){
        if(StringUtils.isEmpty(batteryEvent.getText()))
            return;
        StatePresenter sp = StatePresenter.getInstance();
        // 登录成功了，才进行电量重复上传的判断
        if(STATE_LOGIN_SUCCESS.equals(sp.getRobotState())) {
            /*
            if (batteryEvent.getText().equals(battery)) {
                return;
            }
            */
            LogUtils.e(TAG, "battery = " + battery);
            battery = batteryEvent.getText();
            mSocketInteractor.flushBattery(battery);
        }
    }

    /**
     * 接收到机器人名字变化事件，从 NameObserver 的 onChange 发过来的
     * @param nameEvent
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 10)
    public void OnNameEvent(MindBusEvent.NameEvent nameEvent){
        robotName = nameEvent.getText();
        mSocketInteractor.flushRobotName(robotName);
    }

    /**
     * 接收到登录成功与否的消息
     * @param loginEvent
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND, priority = 10)
    public void OnLoginEvent(MindBusEvent.LoginEvent loginEvent){
        // 登录成功
        if(STATE_LOGIN_SUCCESS.equals(loginEvent.getText())){
            // 也没什么事可做，发一个心跳包吧
            mSocketInteractor.heartBeat();

            // 登录成功，快来陪我玩
            ReportPresenter.report(mContext.getString(R.string.login_server_success));


            ServerBean serverBean = MyDbFlow.queryServerBean(PersistPresenter.getInstance().getServerAddr());
            serverBean.rid = loginEvent.getRid();
            //serverBean.update();
            LogUtils.e(TAG, "rid = " + serverBean.rid);

            MyDbFlow.operateServerBean(MyDbFlow.OP.UPDATE, MyDbFlow.ASYNC, serverBean, new MyDbFlow.DbCallback() {
                @Override
                public void OnSuccess() {
                    LogUtils.e(TAG, "update rid success");
                }

                @Override
                public void OnFailure(Throwable error) {
                    LogUtils.e(TAG, "update rid failure", error);
                }
            });

        }
        // 登录失败
        else if(STATE_LOGIN_FAILURE.equals(loginEvent.getText())){
            mSocketInteractor.login();
        }
    }

    /**
     * 对接收到的数据进行往服务器的转发
     * @param forwardSocketEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 10)
    public void OnForwardSocketEvent(MindBusEvent.ForwardSocketEvent forwardSocketEvent){
        mSocketInteractor.forwardToServer(forwardSocketEvent);
    }

    /**
     * 接收服务器的响应
     * @param socketCarrier
     */
    @Subscribe(threadMode = ThreadMode.ASYNC, priority = 10)
    public void OnServerResponse(SocketBusEvent.SocketCarrier socketCarrier){
        int type = socketCarrier.getType();
        switch (type){
            case RESPONSE_HEART:
                LogUtils.e(TAG, "心跳应答");
                break;

            case RESPONSE_MSG:
                byte[] header = socketCarrier.getHeader();
                if(null != header){
                    String headerStr = new String(header, Charset.defaultCharset());
                    parseCase1(headerStr);
                }
                break;

            case RESPONSE_DATA:
                header = socketCarrier.getHeader();
                String headerStr = null;
                if(null != header){
                    headerStr = new String(header, Charset.defaultCharset());
                }
                byte[] body = socketCarrier.getBody();
                parseCase2(headerStr, body);
                break;

            default:
                break;
        }
        // 释放ByteBuf资源【在netty的IO线程中手动创建的、非池ByteBuf，可以在这里释放】
    }

    /**
     * 空闲事件，由NETTY之IO线程发送，就在IO线程执行
     * @param idleEvent
     */
    @Subscribe(threadMode = ThreadMode.POSTING, priority = 10)
    public void OnIdleEvent(SocketBusEvent.IdleEvent idleEvent){
        int cmd = idleEvent.getCmd();
        if(REQUEST_HEART == cmd){
            mSocketInteractor.heartBeat();

            // 都在发心跳包了，还没登录成功，就登录
            StatePresenter sp = StatePresenter.getInstance();
            if(!STATE_LOGIN_SUCCESS.equals(sp.getRobotState())){
                LogUtils.e(TAG, "OnIdleEvent login");
                mSocketInteractor.login();
            }

        }
    }

    /**
     * 解析Case1
     * @param result
     */
    private void parseCase1(String result){
        LogUtils.e(TAG, "parseCase1 = " + result);
        try {
            JSONObject jsonObj = new JSONObject(result);
            String cmd = jsonObj.optString("cmd", null);
            if(!StringUtils.isEmpty(cmd)){
                Class<? extends BaseTask> taskClass = taskMap.get(cmd);
                if(null != taskClass){
                    /*
                    Class[] parameterTypes = { String.class };
                    Constructor constructor = taskClass.getConstructor(parameterTypes);
                    BaseTask baseTask = (BaseTask) constructor.newInstance(new String[]{result});
                    baseTask.run();
                    */
                    BaseTask baseTask = taskClass.newInstance();
                    baseTask.setResult(result);
                    baseTask.setContext(mContext);
                    baseTask.run();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析Case2
     * @param header
     * @param body
     */
    private void parseCase2(String header, byte[] body){
        LogUtils.e(TAG, "parseCase2 = " + header);
    }

    /********************************【动作的回调处理】********************************************/
    /**
     * 【连接服务器的入口，一旦开始就会自动重连】
     */
    private class MySwitchListener implements ISocketInteractor.SwitchCallback2P{

        @Override
        public void OnSwitchSuccess(String flagVersion, ServerAddr serverAddr) {
            // 查询成功，设置为当前服务器类型
            PersistPresenter.getInstance().setServerAddr(flagVersion, serverAddr.getHttpRequest(), serverAddr.getHttpResource());
        }

        @Override
        public void OnSwitchFailure(Throwable error) {
            LogUtils.e(TAG, "OnSwitchFailure", error);
        }
    }
}
