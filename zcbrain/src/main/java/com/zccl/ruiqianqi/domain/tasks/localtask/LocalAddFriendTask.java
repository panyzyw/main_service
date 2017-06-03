package com.zccl.ruiqianqi.domain.tasks.localtask;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.eventbus.MindBusEvent;
import com.zccl.ruiqianqi.presentation.presenter.PersistPresenter;
import com.zccl.ruiqianqi.presentation.presenter.ReportPresenter;
import com.zccl.ruiqianqi.presentation.presenter.StatePresenter;
import com.zccl.ruiqianqi.config.RemoteProtocol;
import com.zccl.ruiqianqi.domain.model.Robot;
import com.zccl.ruiqianqi.domain.model.localget.LocalAddFriendGet;
import com.zccl.ruiqianqi.domain.model.localpush.LocalAddFriendPush;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.http.myhttp.HttpFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import static com.zccl.ruiqianqi.config.RemoteProtocol.ADD_PHONE_ARGS;
import static com.zccl.ruiqianqi.config.RemoteProtocol.ADD_ROBOT_ARGS;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class LocalAddFriendTask extends BaseLocalTask {

    @Override
    public void run() {
        LocalAddFriendGet localAddFriendGet = JsonUtils.parseJson(result, LocalAddFriendGet.class);
        if(null != localAddFriendGet){
            String[] args = ADD_PHONE_ARGS;
            if(RemoteProtocol.ROBOT_TYPE.equals(localAddFriendGet.getType())){
                args = ADD_ROBOT_ARGS;
            }

            StatePresenter sp = StatePresenter.getInstance();
            Robot robot = sp.getRobot();
            if(null != robot){

                Map<String, String> params = new HashMap<>();
                params.put(args[1], robot.getId());
                params.put(args[2], robot.getSerial());
                params.put(args[3], localAddFriendGet.getNumber());

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
                            parseResult(ret, result);
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
     */
    private void parseResult(int ret, String result){
        switch (ret){
            // 缺少参数
            case -1:
                ReportPresenter.report("");
                break;

            case 0:
                // 返回的数据要添加到本地数据库

                // 本地通信的数据格式
                LocalAddFriendPush localAddFriendPush = new LocalAddFriendPush();
                // 回复【视频APK】
                MindBusEvent.ForwardLocalEvent forwardLocalEvent = new MindBusEvent.ForwardLocalEvent();
                forwardLocalEvent.setText(new Gson().toJson(localAddFriendPush));
                EventBus.getDefault().post(forwardLocalEvent);
                break;

            // 用户信息为空
            case 1:
                ReportPresenter.report("");
                break;
            // 机器人信息为空
            case 2:
                ReportPresenter.report("");
                break;
            // 机器人id或序列号不存在
            case 3:
                ReportPresenter.report("");
                break;
            // 该好友已被添加
            case 4:
                ReportPresenter.report("");
                break;
            // 超过最大绑定数
            case 5:
                ReportPresenter.report("");
                break;
            default:
                break;
        }
    }
}
