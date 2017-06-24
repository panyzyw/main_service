package com.zccl.ruiqianqi.brain.handler;

import android.content.Context;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.brain.R;
import com.zccl.ruiqianqi.brain.semantic.flytek.BaseInfo;
import com.zccl.ruiqianqi.brain.semantic.flytek.ExpressionBean;
import com.zccl.ruiqianqi.brain.service.MainService;
import com.zccl.ruiqianqi.domain.model.dataup.LogCollectBack;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_APP;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_CALL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_CHAT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_DICT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_DISPLAY;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_EMOTION_CHAT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_FACE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_GAME;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_GENERIC;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_HABIT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_HEALTH;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MOVE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MOVIE_INFO;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MUSIC;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MUSIC_CTRL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_MUTE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_OPERA;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SMART_HOME;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SMS;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SOUND;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SQUARE_DANCE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_STORY;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_SWITCH;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_TRANSLATE;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_TRANSLATE_;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_TV_CONTROL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_VIDEO;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_VIDEO_CTRL;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_WATCH_TV;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_YYD_CAHT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.FUNC_YYD_CHAT;
import static com.zccl.ruiqianqi.brain.semantic.flytek.FuncType.OP_EMOTION_CHAT;

/**
 * Created by ruiqianqi on 2017/6/24 0024.
 */

public class LogHandler {

    // 需要在主服务处理日志上传的
    private static List<String> logList;
    static {
        logList = new ArrayList<>();
        logList.add(FUNC_CALL);
        //logList.add(FUNC_MAP);
        logList.add(FUNC_TRANSLATE_);
        logList.add(FUNC_TV_CONTROL);
        logList.add(FUNC_SMS);
        logList.add(FUNC_TRANSLATE);
        //logList.add(FUNC_SCHEDULE);
        //logList.add(FUNC_COOKBOOK);
        //logList.add(FUNC_WEATHER);
        //logList.add(FUNC_STOCK);
        logList.add(FUNC_MUSIC);
        logList.add(FUNC_MUSIC_CTRL);
        logList.add(FUNC_VIDEO);
        logList.add(FUNC_VIDEO_CTRL);
        logList.add(FUNC_SQUARE_DANCE);
        //logList.add(FUNC_STUDY);
        //logList.add(FUNC_SINOLOGY);

        logList.add(FUNC_MOVIE_INFO);
        logList.add(FUNC_OPERA);
        logList.add(FUNC_HEALTH);
        logList.add(FUNC_SOUND);
        logList.add(FUNC_STORY);
        //logList.add(FUNC_DANCE);
        logList.add(FUNC_HABIT);
        //logList.add(FUNC_BATTERY);
        logList.add(FUNC_DICT);

        //logList.add(FUNC_ARITHMETIC);
        //logList.add(FUNC_BAI_KE);
        //logList.add(FUNC_DATETIME);
        //logList.add(FUNC_FAQ);
        //logList.add(FUNC_OPEN_QA);

        //logList.add(FUNC_CHAT);
        logList.add(FUNC_EMOTION_CHAT);
        logList.add(FUNC_GENERIC);

        logList.add(FUNC_SMART_HOME);
        //logList.add(FUNC_SHUTDOWN);
        logList.add(FUNC_FACE);
        //logList.add(FUNC_QUSETION);
        logList.add(FUNC_MOVE);
        logList.add(FUNC_SWITCH);
        logList.add(FUNC_DISPLAY);
        logList.add(FUNC_MUTE);
        logList.add(FUNC_WATCH_TV);
        logList.add(FUNC_APP);
        logList.add(FUNC_GAME);
        logList.add(FUNC_YYD_CHAT);
        logList.add(FUNC_YYD_CAHT);
    }

    /**
     * 上传用户操作日志
     * @param json
     */
    public static void logUpdate(Context context, String json){
        // 日志上传处理
        BaseInfo baseInfo = JsonUtils.parseJson(json, BaseInfo.class);
        if (null == baseInfo) {
            return;
        }

        String funcType = baseInfo.getServiceType();
        // 如果是聊天
        if(FUNC_CHAT.equals(funcType)) {
            // 如果是表情聊天
            ExpressionBean expressionBean = JsonUtils.parseJson(json, ExpressionBean.class);
            if (null != expressionBean) {
                if (OP_EMOTION_CHAT.equals(expressionBean.getOperation())) {
                    funcType = FUNC_EMOTION_CHAT;
                }
            }
        }

        if(logList.contains(funcType)){
            LogCollectBack.LogCollect logCollect = new LogCollectBack.LogCollect();
            logCollect.setService(baseInfo.getServiceType());
            logCollect.setOperation(baseInfo.getOperation());
            logCollect.setText(baseInfo.getText());
            JSONObject semantic = null;
            try {
                JSONObject jsonObject = new JSONObject(json);
                semantic = jsonObject.getJSONObject("semantic");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(null != semantic){
                logCollect.setSemantic(semantic.toString());
            }

            if(FUNC_MOVE.equals(funcType)){
                logCollect.setAnswer(context.getString(R.string.answer_move));
            }
            else if(FUNC_TRANSLATE.equals(funcType)){
                logCollect.setAnswer(context.getString(R.string.answer_trans));
            }
            else {

            }
            AppUtils.logCollectUp2Server(new Gson().toJson(logCollect), MainService.TAG, null);
        }

    }
}
