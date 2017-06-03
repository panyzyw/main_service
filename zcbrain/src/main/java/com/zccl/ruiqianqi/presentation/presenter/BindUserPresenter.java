package com.zccl.ruiqianqi.presentation.presenter;

import android.os.Bundle;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.dataup.DeleteBindUser;
import com.zccl.ruiqianqi.domain.model.dataup.QueryBindUser;
import com.zccl.ruiqianqi.domain.model.dataup.QueryPhotosBack;
import com.zccl.ruiqianqi.plugin.voice.AbstractVoice;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.presenter.impl.MindPresenter;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_BINDER_USER_DELETE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.A_BINDER_USER_QUERY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_QUERY_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_FAILURE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.RET_SUCCESS;

/**
 * Created by ruiqianqi on 2017/5/5 0005.
 */

public class BindUserPresenter extends BasePresenter {

    // 类标志
    private static String TAG = BindUserPresenter.class.getSimpleName();

    // 查询绑定用户列表，【发给二维码应用的广播】
    public static final String BINDER_USER_QUERY_RESULT = "com.yydrobot.qrcode.RESLUT";
    // 携带着返回的数据
    public static final String KEY_RESULT = "result";

    // 删除绑定用户是否成功，【发给二维码应用的广播】
    public static final String BINDER_USER_DELETE_RESULT = "com.yydrobot.qrcode.DRESLUT";

    // 声音处理类
    private AbstractVoice voice;

    public BindUserPresenter() {
        voice = MindPresenter.getInstance().getVoiceDevice();
    }

    /**
     * 查询绑定用户列表
     */
    public void queryBindUser(){
        MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
        forwardSocketEvent.setCmd(A_BINDER_USER_QUERY);
        EventBus.getDefault().post(forwardSocketEvent);
    }

    /**
     * 查询绑定用户列表的返回结果处理
     */
    public void queryBindUserResult(String result){
        try {
            JSONObject jsonObj = new JSONObject(result);
            String ret = jsonObj.optString("ret", RET_FAILURE);

            // 查询成功，不做任何解析，直接发给了二维码
            if(RET_SUCCESS.equals(ret)){
                Bundle bundle = new Bundle();
                bundle.putString(KEY_RESULT, result);
                MyAppUtils.sendBroadcast(mContext, BINDER_USER_QUERY_RESULT, bundle);
            }
            // 失败了，要提示一下
            else {
                voice.startTTS(mContext.getString(R.string.query_bind_user_error), null, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除绑定用户
     */
    public void deleteBindUser(String id){
        if(StringUtils.isEmpty(id))
            return;

        StatePresenter sp = StatePresenter.getInstance();
        Robot robot = sp.getRobot();
        if(null == robot)
            return;

        String rid = robot.getRid();
        if(StringUtils.isEmpty(rid))
            return;

        DeleteBindUser deleteBindUser = new DeleteBindUser();
        deleteBindUser.setId(id);
        deleteBindUser.setRobot_id(rid);

        MindBusEvent.ForwardSocketEvent forwardSocketEvent = new MindBusEvent.ForwardSocketEvent();
        forwardSocketEvent.setCmd(A_BINDER_USER_DELETE);
        forwardSocketEvent.setText(new Gson().toJson(deleteBindUser));
        EventBus.getDefault().post(forwardSocketEvent);
    }

    /**
     * 删除绑定用户返回结果的处理
     * @param result
     */
    public void deleteBindUserResult(String result){
        try {
            JSONObject jsonObj = new JSONObject(result);
            String ret = jsonObj.optString("ret", RET_FAILURE);
            // 删除成功，啥也不做
            if(RET_SUCCESS.equals(ret)){
                MyAppUtils.sendBroadcast(mContext, BINDER_USER_DELETE_RESULT, null);
            }
            // 失败了，要提示一下
            else {
                voice.startTTS(mContext.getString(R.string.delete_bind_user_error), null, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
