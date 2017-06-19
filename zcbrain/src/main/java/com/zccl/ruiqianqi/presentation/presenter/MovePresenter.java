package com.zccl.ruiqianqi.presentation.presenter;

import android.os.RemoteException;

import com.zccl.ruiqianqi.mind.voice.impl.slotsvalue.MoveSlots;
import com.zccl.ruiqianqi.move.MoveAction;
import com.zccl.ruiqianqi.move.MoveException;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.config.RemoteProtocol;
import com.zccl.ruiqianqi.domain.model.datadown.PushMove;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.utils.AppUtils;

/**
 * Created by ruiqianqi on 2017/3/20 0020.
 */

public class MovePresenter extends BasePresenter {

    // 类日志标志
    private static String TAG = MovePresenter.class.getSimpleName();
    // 单例引用
    private static MovePresenter instance;
    // 运动服务
    private MoveAction moveAction;

    /**
     * 构造子
     */
    private MovePresenter() {
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        moveAction = MoveAction.getInstance(mContext);
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static MovePresenter getInstance() {
        if (instance == null) {
            synchronized (MovePresenter.class) {
                MovePresenter temp = instance;
                if (temp == null) {
                    temp = new MovePresenter();
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 解析来自服务器下发的数据
     * @param json
     */
    public void parseSocketData(String json){
        PushMove pushMove = JsonUtils.parseJson(json, PushMove.class);
        if(null != pushMove){
            String command = pushMove.getCommand();
            PushMove.CommandMove commandMove = JsonUtils.parseJson(command, PushMove.CommandMove.class);
            if(null != commandMove){
                pushMove.setCommandMove(commandMove);

                try {
                    moveAction.setDriveType(MoveAction.DRIVE_BY_TIME);

                    if(RemoteProtocol.TYPE_MOVE_FORWARD.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.forward(1500);

                    }else if(RemoteProtocol.TYPE_MOVE_BACK.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.back(1500);

                    }else if(RemoteProtocol.TYPE_MOVE_TURN_LEFT.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.left(1500);

                    }else if(RemoteProtocol.TYPE_MOVE_TURN_RIGHT.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.right(1500);

                    }else if(RemoteProtocol.TYPE_MOVE_STOP.equals(commandMove.getType())){
                        moveAction.stop();
                        release();

                    }else if(RemoteProtocol.TYPE_HEAD_UP.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.headUp(1500);

                    }else if(RemoteProtocol.TYPE_HEAD_DOWN.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.headDown(1500);

                    }else if(RemoteProtocol.TYPE_HEAD_LEFT.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.headLeft(1500);

                    }else if(RemoteProtocol.TYPE_HEAD_RIGHT.equals(commandMove.getType())){
                        moveAction.setSpeed(30);
                        moveAction.headRight(1500);

                    }else if(RemoteProtocol.TYPE_HEAD_STOP.equals(commandMove.getType())){
                        moveAction.headStop();
                        release();

                    }

                } catch (InterruptedException e) {
                    LogUtils.e(TAG, "", e);
                } catch (MoveException e) {
                    LogUtils.e(TAG, "", e);
                } catch (RemoteException e) {
                    LogUtils.e(TAG, "", e);
                }
            }
        }
    }

    /**
     * 解析来自科大讯飞的数据
     * @param direct
     */
    public void parseFlytekData(String direct){
        long delay = 0;
        try {
            moveAction.setDriveType(MoveAction.DRIVE_BY_TIME);

            if(MoveSlots.TYPE_MOVE_FORWARD.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.forward(3300);
                delay = 3300 + 500;
            }else if(MoveSlots.TYPE_MOVE_BACK.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.back(3300);
                delay = 3300 + 500;
            }else if(MoveSlots.TYPE_MOVE_TURN_LEFT.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.left(1000);
                delay = 1000 + 500;
            }else if(MoveSlots.TYPE_MOVE_TURN_RIGHT.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.right(1000);
                delay = 1000 + 500;
            }else if(MoveSlots.TYPE_HEAD_UP.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.headUp(1000);
                delay = 1000 + 500;
            }else if(MoveSlots.TYPE_HEAD_DOWN.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.headDown(1000);
                delay = 1000 + 500;
            }else if(MoveSlots.TYPE_HEAD_LEFT.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.headLeft(1000);
                delay = 1000 + 500;
            }else if(MoveSlots.TYPE_HEAD_RIGHT.equals(direct)){
                moveAction.setSpeed(30);
                moveAction.headRight(1000);
                delay = 1000 + 500;
            }else if(MoveSlots.TYPE_HEAD_MIDDLE.equals(direct)){
                moveAction.setSpeed(40);
                moveAction.headLeftTurnMid();
                delay = 1000 + 500;
            }
        } catch (InterruptedException e) {
            LogUtils.e(TAG, "", e);
        } catch (MoveException e) {
            LogUtils.e(TAG, "", e);
        } catch (RemoteException e) {
            LogUtils.e(TAG, "", e);
        }
        if(0 != delay){
            startListen(delay);
        }
    }

    /**
     * 动作做完之后，继续监听
     * @param delay
     */
    private void startListen(long delay){
        mainThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                AppUtils.startListen(mContext, TAG, true, true);
            }
        }, delay);
    }

    /**
     * 释放单例
     */
    public static void release(){
        instance = null;
    }

}
