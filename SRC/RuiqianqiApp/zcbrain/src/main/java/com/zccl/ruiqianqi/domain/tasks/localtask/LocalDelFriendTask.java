package com.zccl.ruiqianqi.domain.tasks.localtask;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.config.RemoteProtocol;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.localget.LocalDelFriendGet;
import com.zccl.ruiqianqi.domain.model.localpush.LocalDelFriendPush;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.http.myhttp.HttpFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static com.zccl.ruiqianqi.config.RemoteProtocol.DEL_PHONE_ARGS;
import static com.zccl.ruiqianqi.config.RemoteProtocol.DEL_ROBOT_ARGS;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class LocalDelFriendTask extends BaseLocalTask {
    @Override
    public void run() {
        LocalDelFriendGet localDelFriendGet = JsonUtils.parseJson(result, LocalDelFriendGet.class);
        if(null != localDelFriendGet){
            String[] args = DEL_PHONE_ARGS;
            if(RemoteProtocol.ROBOT_TYPE.equals(localDelFriendGet.getType())){
                args = DEL_ROBOT_ARGS;
            }

            StatePresenter sp = StatePresenter.getInstance();
            Robot robot = sp.getRobot();
            if(null != robot){

                Map<String, String> params = new HashMap<>();
                params.put(args[1], robot.getId());
                params.put(args[2], robot.getSerial());
                params.put(args[3], localDelFriendGet.getNumber());

                PersistPresenter cp = PersistPresenter.getInstance();
                String url = "http://" + cp.getHttpRequest() + args[0];
                HttpURLConnection connection = HttpFactory.createHttpConn(url, HttpFactory.POST);
                if(null != connection){
                    connection.setRequestProperty("Content-Type", "text/plain;charset=UTF-8");
                    String result = HttpFactory.postHttpData(connection, params);
                    if(!StringUtils.isEmpty(result)){
                        try {
                            JSONObject obj = new JSONObject(result);
                            int ret = Integer.parseInt(obj.getString("ret"));
                            parseResult(ret, result, localDelFriendGet.getNumber(), localDelFriendGet.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 解析返回的结果
     * @param ret
     * @param result
     * @param number  要删除的号码或PHONE
     * @param type    根据机器人RID或PHONE删除
     */
    private void parseResult(int ret, String result, String number, String type){
        switch (ret){
            // 缺少参数
            case -1:
                ReportPresenter.report("");
                break;

            case 0:
                // 返回成功，根据号码或PHONE删除本地数据库信息


                // 本地通信的数据格式
                LocalDelFriendPush localDelFriendPush = new LocalDelFriendPush();
                // 回复【视频APK】
                MindBusEvent.ForwardLocalEvent forwardLocalEvent = new MindBusEvent.ForwardLocalEvent();
                forwardLocalEvent.setText(new Gson().toJson(localDelFriendPush));
                EventBus.getDefault().post(forwardLocalEvent);
                break;

            case 1:
                if(RemoteProtocol.ROBOT_TYPE.equals(type)){
                    // 机器人信息为空
                    ReportPresenter.report("");
                }else {
                    // 用户信息为空
                    ReportPresenter.report("");
                }
                break;
            case 2:
                if(RemoteProtocol.ROBOT_TYPE.equals(type)){
                    // 机器人id或序列号不存在
                    ReportPresenter.report("");
                }else {
                    // 机器人信息为空
                    ReportPresenter.report("");
                }
                break;
            case 3:
                if(RemoteProtocol.ROBOT_TYPE.equals(type)){
                    // 机器人要删除的好友信息为空
                    ReportPresenter.report("");
                }else {
                    // 机器人id或序列号不存在
                    ReportPresenter.report("");
                }
                break;
            case 4:
                if(RemoteProtocol.ROBOT_TYPE.equals(type)){
                    // 机器人要删除的好友id或序列号不存在
                    ReportPresenter.report("");
                }else {
                    // 机器人要删除的好友信息不在好友列表
                    ReportPresenter.report("");
                }
                break;
            case 5:
                if(RemoteProtocol.ROBOT_TYPE.equals(type)){
                    // 机器人要删除的好友信息不在好友列表
                    ReportPresenter.report("");
                }else {
                    //
                    ReportPresenter.report("");
                }
                break;
            default:
                break;
        }
    }
}
