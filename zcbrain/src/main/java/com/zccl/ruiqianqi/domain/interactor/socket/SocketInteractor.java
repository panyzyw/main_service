package com.zccl.ruiqianqi.domain.interactor.socket;

import android.content.Context;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.config.MyConfig;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.ServerAddr;
import com.zccl.ruiqianqi.domain.model.dataup.LogCollectBack;
import com.zccl.ruiqianqi.domain.model.dataup.QueryBindUser;
import com.zccl.ruiqianqi.domain.model.dataup.RobotMediaBack;
import com.zccl.ruiqianqi.domain.repository.ISocketRepository;
import com.zccl.ruiqianqi.domain.interactor.base.BaseInteractor;
import com.zccl.ruiqianqi.domain.interactor.ISocketInteractor;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.config.RemoteProtocol;
import com.zccl.ruiqianqi.domain.model.dataup.QueryPhotosBack;
import com.zccl.ruiqianqi.domain.model.dataup.RemindBack;
import com.zccl.ruiqianqi.domain.model.robotup.FlushUpBattery;
import com.zccl.ruiqianqi.domain.model.robotup.FlushUpName;
import com.zccl.ruiqianqi.domain.model.robotup.LoginUp;
import com.zccl.ruiqianqi.domain.model.robotup.MediaForwardUp;
import com.zccl.ruiqianqi.socket.remotesocket.NetworkTask;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_CONNECT_EXCEPTION;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_CONNECT_OFF;
import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_ING;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_BINDER_USER_DELETE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_BINDER_USER_QUERY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_LOG_COLLECT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_ROBOT_MEDIA_CONTROL;
import static com.zccl.ruiqianqi.presentation.presenter.PersistPresenter.KEY_ID;
import static com.zccl.ruiqianqi.presentation.presenter.PersistPresenter.KEY_SID;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_MEDIA_FORWARD_2_SERVER;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_NAMES_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_QUERY_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_REMIND_RESULT;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.HEART_BIT_TIME;
import static com.zccl.ruiqianqi.socket.remotesocket.NetworkTask.HEART_BIT_WAY;

/**
 * Created by ruiqianqi on 2017/1/16 0016.
 */

public class SocketInteractor extends BaseInteractor implements ISocketInteractor {
    // 类标志
    private static String TAG = SocketInteractor.class.getSimpleName();
    // 资源库模式下数据的来源是不确定的
    private ISocketRepository mSocketRepository;
    // 切换服务的回调
    private ISocketInteractor.SwitchCallback2P mSwitchCallback2P;
    // 网络连接操作类
    private NetworkTask mNetworkTask;
    // 网络连接成功与否的回调
    private MyConnectListener mConnectListener;
    // 心跳调度线程
    private ScheduledFuture<?> mHeartScheduled;

    public SocketInteractor(Context context, ISocketRepository socketRepository) {
        super(context);

        this.mSocketRepository = socketRepository;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        mNetworkTask = new NetworkTask(mContext);
        mConnectListener = new MyConnectListener();
    }

    /********************************【对外提供的方法】********************************************/
    /**
     * 切换服务器
     * @param flagVersion 服务器地址版本
     */
    @Override
    public void switchToServer(final String flagVersion, CONN_TYPE connType){
        ServerAddr serverAddr = mSocketRepository.queryServerAddr(flagVersion);
        if(null == serverAddr){
            mSwitchCallback2P.OnSwitchFailure(new Throwable("serverBean is null"));

        }else {

            // 回调控制中心，备份数据
            mSwitchCallback2P.OnSwitchSuccess(flagVersion, serverAddr);

            if(CONN_TYPE.INIT_CONNECT == connType) {
                // 连接服务器
                connectToServer(serverAddr.getTcpRequest(), serverAddr.getTcpPort(), 0);
            }else {
                // 切换服务器
                connectToServer(serverAddr.getTcpRequest(), serverAddr.getTcpPort(), -1);
            }
        }
    }

    /**
     * 设置切换服务器时回调 P 的接口
     * @param switchCallback2P
     */
    @Override
    public void setSwitchCallback2P(SwitchCallback2P switchCallback2P) {
        this.mSwitchCallback2P = switchCallback2P;
    }

    /**
     * 登录服务器
     */
    @Override
    public void login(){
        scheduleTask.execute(new Runnable() {
            @Override
            public void run() {
                LoginUp loginUp = new LoginUp();
                loginUp.setId(ShareUtils.getP(mContext).getString(KEY_ID, MyConfig.STATE_DEFAULT_ID));
                loginUp.setSerial(ShareUtils.getP(mContext).getString(KEY_SID, MyConfig.STATE_DEFAULT_SID));
                loginUp.setVersion(android.os.Build.DISPLAY);
                loginUp.setBattery("");
                ByteBuffer byteBuf = RemoteProtocol.buildLoginUp2(loginUp);
                mNetworkTask.sendTcpData(byteBuf.array(), byteBuf.limit());
            }
        });
    }

    /**
     * 发起心跳包
     */
    @Override
    public void heartBeat(){
        if(0 == HEART_BIT_WAY){
            ByteBuffer byteBuf = RemoteProtocol.buildHeart2();
            mNetworkTask.sendTcpData(byteBuf.array(), byteBuf.limit());
        } else {
            mHeartScheduled = scheduleTask.executeAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    ByteBuffer byteBuf = RemoteProtocol.buildHeart2();
                    mNetworkTask.sendTcpData(byteBuf.array(), byteBuf.limit());
                }
            }, 0, HEART_BIT_TIME, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 向服务器更新机器人电量
     * @param battery
     */
    @Override
    public void flushBattery(final String battery){
        // 线程池里发送
        scheduleTask.execute(new Runnable() {
            @Override
            public void run() {
                FlushUpBattery flushUpBattery = new FlushUpBattery();
                flushUpBattery.setBattery(battery);
                ByteBuffer byteBuf = RemoteProtocol.buildFlushUpBattery2(flushUpBattery);
                mNetworkTask.sendTcpData(byteBuf.array(), byteBuf.limit());
            }
        });
    }

    /**
     * 向服务器更新机器人名字
     * @param robotName
     */
    @Override
    public void flushRobotName(final String robotName){
        // 线程池里发送
        scheduleTask.execute(new Runnable() {
            @Override
            public void run() {
                FlushUpName flushUpName = new FlushUpName();
                flushUpName.setRname(robotName);
                ByteBuffer byteBuf = RemoteProtocol.buildFlushUpName2(flushUpName);
                mNetworkTask.sendTcpData(byteBuf.array(), byteBuf.limit());
            }
        });
    }

    /**
     * 把收到的数据往服务器进行转发
     * @param forwardSocketEvent
     */
    @Override
    public void forwardToServer(MindBusEvent.ForwardSocketEvent forwardSocketEvent){

        ByteBuffer byteBuf = null;

        // 将视频APP协议转发给服务器
        if(A_MEDIA_FORWARD_2_SERVER.equals(forwardSocketEvent.getCmd())){
            MediaForwardUp mediaForwardUp = new MediaForwardUp();
            mediaForwardUp.setCommand(forwardSocketEvent.getText());
            byteBuf = RemoteProtocol.buildMediaForwardUp2(mediaForwardUp);
        }
        // 查询照片名字的响应
        else if(B_PHOTO_NAMES_RESULT.equals(forwardSocketEvent.getCmd())){
            QueryPhotosBack queryPhotosBack = new QueryPhotosBack();
            queryPhotosBack.setCommand(forwardSocketEvent.getText());
            byteBuf = RemoteProtocol.buildPhotoNamesBack2(queryPhotosBack);
        }
        // 获取照片数据的响应
        else if(B_PHOTO_QUERY_RESULT.equals(forwardSocketEvent.getCmd())){
            QueryPhotosBack queryPhotosBack = new QueryPhotosBack();
            queryPhotosBack.setCommand(forwardSocketEvent.getText());
            byteBuf = RemoteProtocol.buildPhotoDataBack2(queryPhotosBack, forwardSocketEvent.getData());
        }
        // 将提醒通过广播返回的结果，发给服务器
        else if(B_REMIND_RESULT.equals(forwardSocketEvent.getCmd())){
            RemindBack remindBack = new RemindBack();
            remindBack.setCommand(forwardSocketEvent.getText());
            byteBuf = RemoteProtocol.buildRemindBack2(remindBack);
        }
        // 机器人向手机推送的指令
        else if(B_ROBOT_MEDIA_CONTROL.equals(forwardSocketEvent.getCmd())){
            try {
                JSONObject jsonObj = new JSONObject(forwardSocketEvent.getText());
                String cmd = jsonObj.optString("cmd", null);
                if (B_ROBOT_MEDIA_CONTROL.equals(cmd)){
                    RobotMediaBack robotMediaBack = new RobotMediaBack();
                    robotMediaBack.setCommand(forwardSocketEvent.getText());
                    byteBuf = RemoteProtocol.buildRobotMediaBack2(robotMediaBack);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 用户操作日志收集
        else if(B_LOG_COLLECT.equals(forwardSocketEvent.getCmd())){
            byteBuf = RemoteProtocol.buildMsg2(forwardSocketEvent.getText());
        }

        // 查询绑定用户列表，发给服务器
        else if(A_BINDER_USER_QUERY.equals(forwardSocketEvent.getCmd())){
            QueryBindUser queryBindUser = new QueryBindUser();
            byteBuf = RemoteProtocol.buildQueryBindUser2(queryBindUser);
        }
        // 删除绑定用户
        else if(A_BINDER_USER_DELETE.equals(forwardSocketEvent.getCmd())){
            byteBuf = RemoteProtocol.buildMsg2(forwardSocketEvent.getText());
        }
        // 其他的直接发送给服务器
        else {
            byteBuf = RemoteProtocol.buildMsg2(forwardSocketEvent.getText());
        }

        if(null != byteBuf) {
            mNetworkTask.sendTcpData(byteBuf.array(), byteBuf.limit());
        }

    }


    /***********************************【私有方法】***********************************************/
    /**
     * 连接服务器
     * @param ip
     * @param port
     * @param delay
     */
    private void connectToServer(final String ip, final String port, long delay){
        mNetworkTask.connectToServer(ip, Integer.valueOf(port), mConnectListener, delay);
    }

    /**
     * 取消心跳
     */
    private void cancelHeartBit(){
        if(0 != HEART_BIT_WAY){
            if(null != mHeartScheduled && !mHeartScheduled.isCancelled()){
                mHeartScheduled.cancel(true);
                mHeartScheduled = null;
            }
        }
    }
    /**
     * 连接服务器失败，网络不一定是断开的
     * @param state 网络断开、网络正常连接异常
     */
    private void failure(String state){
        StatePresenter sp = StatePresenter.getInstance();
        sp.setRobotState(state);
        sp.setInControl(false);
        sp.setControlId(null);

        cancelHeartBit();
    }

    /*********************************【连接服务器的回调】*****************************************/
    /**
     * 网络连接成功与否的回调
     */
    private class MyConnectListener implements NetworkTask.IConnectCallback{

        /**
         * 【服务器已连接上】，但是连接断开了之后的回调
         */
        @Override
        public void OnNetInActive() {
            LogUtils.e(TAG, "OnNetInActive");
            failure(STATE_CONNECT_OFF);
        }

        /**
         * 服务器连接成功
         */
        @Override
        public void OnSuccess() {
            LogUtils.e(TAG, "OnSuccess");
            StatePresenter sp = StatePresenter.getInstance();
            sp.setRobotState(STATE_LOGIN_ING);
            sp.setNetConnected(true);
            login();
        }

        /**
         * 【服务器没有连接上】，连接失败的回调
         * @param error
         */
        @Override
        public void OnFailure(Throwable error) {
            LogUtils.e(TAG, "OnFailure");
            failure(STATE_CONNECT_EXCEPTION);
        }
    }
}
