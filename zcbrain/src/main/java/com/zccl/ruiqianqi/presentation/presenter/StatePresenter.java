package com.zccl.ruiqianqi.presentation.presenter;

import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.zccl.ruiqianqi.config.MyConfig.STATE_LOGIN_DEFAULT;

/**
 * Created by ruiqianqi on 2017/3/9 0009.
 *
 * 运行持久
 */

public class StatePresenter extends BasePresenter {

    // 登录后下行的信息，运行时要保存的
    private Robot mRobot;

    /**
     * 机器人处于：默认状态，无任何动作
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_LOGIN_DEFAULT}
     * 机器人处于：帐号没有读取到
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_ACCOUNT_EXCEPTION}
     * 机器人处于：帐号读取正常，但没有登录
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_ACCOUNT_OKAY}
     * 连接上不服务器【平行】
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_CONNECT_EXCEPTION}
     * 连接上服务器了，断开了【平行】
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_CONNECT_OFF}
     * 连接成功，正在登录
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_LOGIN_ING}
     * 机器人处于：登录成功【平行】
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_LOGIN_SUCCESS}
     * 机器人处于：登录失败【平行】
     * {@link com.zccl.ruiqianqi.config.MyConfig#STATE_LOGIN_FAILURE}
     */
    private String mRobotState = STATE_LOGIN_DEFAULT;

    // 是不是在手机的控制之下
    private boolean isInControl;
    // 控制者ID
    private String mControlId;
    // 是否在通话中
    private boolean isCalling;
    // 是否在等待提醒结果
    private boolean isWaitingRemindResult;
    // 网络是否已连接
    private boolean isNetConnected;
    // 是否正在进行视频
    private boolean isVideoing;
    // 是否正在工厂模式
    private boolean isFactory;
    // 屏幕是否锁屏
    private boolean isScreenOff;

    // 场景集合
    private LinkedList<SceneInfo> sceneFifo;

    private StatePresenter() {
        sceneFifo = new LinkedList<>();
    }

    public Robot getRobot() {
        return mRobot;
    }

    public void setRobot(Robot robot) {
        this.mRobot = robot;
    }

    public boolean isInControl() {
        return isInControl;
    }

    public void setInControl(boolean inControl) {
        isInControl = inControl;
    }

    public String getControlId() {
        return mControlId;
    }

    public void setControlId(String mControlId) {
        this.mControlId = mControlId;
    }

    public String getRobotState() {
        return mRobotState;
    }

    public void setRobotState(String mRobotState) {
        this.mRobotState = mRobotState;
    }

    public boolean isCalling() {
        return isCalling;
    }

    public void setCalling(boolean calling) {
        isCalling = calling;
    }

    public boolean isWaitingRemindResult() {
        return isWaitingRemindResult;
    }

    public void setWaitingRemindResult(boolean waitingRemindResult) {
        isWaitingRemindResult = waitingRemindResult;
    }

    public boolean isNetConnected() {
        return isNetConnected;
    }

    public void setNetConnected(boolean netConnected) {
        isNetConnected = netConnected;
    }

    public boolean isVideoing() {
        return isVideoing;
    }

    public void setVideoing(boolean videoing) {
        isVideoing = videoing;
    }

    public boolean isFactory() {
        return isFactory;
    }

    public void setFactory(boolean factory) {
        isFactory = factory;
    }

    public boolean isScreenOff() {
        return isScreenOff;
    }

    public void setScreenOff(boolean screenOff) {
        isScreenOff = screenOff;
    }

    /**
     * 【这个方法就是使用类级内部类了】
     *
     * @return
     */
    public static StatePresenter getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
     */
    private static class SingletonHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        private static StatePresenter instance = new StatePresenter();
    }

    /**********************************************************************************************/

    /**
     * 场景类
     */
    private static class SceneInfo{
        // 场景名字
        String sceneName;
        // 场景状态
        boolean sceneStatus;

        public SceneInfo(String sceneName, boolean sceneStatus) {
            this.sceneName = sceneName;
            this.sceneStatus = sceneStatus;
        }
    }

    /**
     * 处理场景信息
     *
     * offer方法在添加元素时，如果发现队列已满无法添加的话，会直接返回false。
     * add方法在添加元素的时候，若超出了度列的长度会直接抛出异常：
     * put方法，若向队尾添加元素的时候发现队列已经满了会发生阻塞一直等待空间，以加入元素。
     * poll: 若队列为空，返回null。
     * remove:若队列为空，抛出NoSuchElementException异常。
     * take:若队列为空，发生阻塞，等待有元素
     *
     * @param sceneName
     * @param sceneStatus true就是添加场景，false就是移除场景
     */
    public void handleScene(String sceneName, boolean sceneStatus){
        if(StringUtils.isEmpty(sceneName))
            return;
        if(sceneStatus){
            sceneFifo.offer(new SceneInfo(sceneName, sceneStatus));
        }else {
            Iterator<SceneInfo> it = sceneFifo.iterator();
            while (it.hasNext()){
                SceneInfo sceneInfo = it.next();
                if(sceneName.equals(sceneInfo.sceneName)){
                    it.remove();
                }
            }
        }
    }

    /**
     * 返回当前场景
     * @return
     */
    public String getScene(){
        try {
            SceneInfo sceneInfo = sceneFifo.getFirst();
            if(null != sceneInfo) {
                return sceneInfo.sceneName;
            }
        }catch (NoSuchElementException e){

        }
        return null;
    }
}
