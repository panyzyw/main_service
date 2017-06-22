package com.zccl.ruiqianqi.domain.tasks.remotetask;

import android.os.Bundle;

import com.zccl.ruiqianqi.domain.model.datadown.PushContent;
import com.zccl.ruiqianqi.domain.model.datadown.PushRemind;
import com.zccl.ruiqianqi.presentation.presenter.MovePresenter;
import com.zccl.ruiqianqi.presentation.presenter.PhotoPresenter;
import com.zccl.ruiqianqi.presentation.presenter.PushPresenter;
import com.zccl.ruiqianqi.presentation.presenter.RemindPresenter;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zccl.ruiqianqi.config.LocalProtocol.ACTION_BATTERY_RECV;
import static com.zccl.ruiqianqi.config.LocalProtocol.ACTION_MEDIA_RECV;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MAIN_RECV_FROM;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MAIN_RECV_FUNCTION;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MAIN_RECV_RESULT;
import static com.zccl.ruiqianqi.config.LocalProtocol.KEY_MEDIA_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_CONTENT_PUSH;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_INSERT_UPDATE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_MOVE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_QUERY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PUSH_MUSIC;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PUSH_MUSIC_CTRL;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PUSH_TIMED_SHUTDOWN;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_REMIND_DELETE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_REMIND_INSERT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_REMIND_QUERY;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_TEXT_QUESTION;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_TEXT_TALK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.TYPE_CONTENT_MUSIC;
import static com.zccl.ruiqianqi.config.RemoteProtocol.TYPE_CONTENT_TEXT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.TYPE_CONTENT_VIDEO;

/**
 * Created by ruiqianqi on 2017/3/15 0015.
 */

public class PushTask extends BaseTask {

    private static String TAG = PushTask.class.getSimpleName();

    @Override
    public void run() {
        try {
            JSONObject jsonObj = new JSONObject(result);
            String command = jsonObj.optString("command", null);

            if (!StringUtils.isEmpty(command)) {
                jsonObj = new JSONObject(command);
                String cmd = jsonObj.optString("cmd", null);
                if (StringUtils.isEmpty(cmd)){
                    return;
                }
                // 机器人手机控制问答，即文本理解
                if(B_TEXT_QUESTION.equals(cmd)){
                    String question = jsonObj.optString("type", null);
                    PushPresenter pp = new PushPresenter();
                    pp.chat(question);
                }
                // 同声说话，机器人说出收到的文字
                else if(B_TEXT_TALK.equals(cmd)){
                    String tts_text = jsonObj.optString("type", null);
                    PushPresenter pp = new PushPresenter();
                    pp.talk(tts_text);
                }
                // 手机查看机器人照片
                else if(B_PHOTO_QUERY.equals(cmd)){
                    PhotoPresenter pp = new PhotoPresenter();
                    pp.parseData(result);
                }
                // 处理手机控制移动
                else if(B_MOVE.equals(cmd)){
                    MovePresenter.getInstance().parseSocketData(result);
                }
                // 处理内容推送功能，音乐、视频或文字
                else if(B_CONTENT_PUSH.equals(cmd)){
                    String type = jsonObj.optString("type", null);
                    PushPresenter pp = new PushPresenter();
                    if(TYPE_CONTENT_MUSIC.equals(type)){
                        PushContent.CommandMedia media = JsonUtils.parseJson(command, PushContent.CommandMedia.class);
                        if(null == media)
                            return;
                        pp.pushMusic(media.getName(), media.getUrl());
                    }
                    else if(TYPE_CONTENT_VIDEO.equals(type)){
                        PushContent.CommandMedia media = JsonUtils.parseJson(command, PushContent.CommandMedia.class);
                        if(null == media)
                            return;
                        pp.pushVideo(media.getName(), media.getUrl());
                    }
                    else if(TYPE_CONTENT_TEXT.equals(type)){
                        PushContent.CommandTxt txt = JsonUtils.parseJson(command, PushContent.CommandTxt.class);
                        if(null == txt)
                            return;
                        pp.pushText(txt.getContent());
                    }
                }
                // 处理手机操作提醒
                else if(cmd.startsWith("remind_")){
                    PushRemind pushRemind = JsonUtils.parseJson(result, PushRemind.class);
                    if(null != pushRemind){
                        PushRemind.CommandRemind commandRemind = JsonUtils.parseJson(command, PushRemind.CommandRemind.class);
                        if(null != commandRemind){
                            RemindPresenter remindPresenter = new RemindPresenter();
                            if(B_REMIND_INSERT.equals(cmd)){
                                remindPresenter.addRemind(commandRemind);

                            }else if(B_REMIND_DELETE.equals(cmd)){
                                remindPresenter.delRemind(commandRemind);

                            }else if(B_INSERT_UPDATE.equals(cmd)){
                                remindPresenter.updateRemind(commandRemind);

                            }else if(B_REMIND_QUERY.equals(cmd)){
                                remindPresenter.queryRemind(commandRemind);

                            }
                        }
                    }
                }

                // 手机端发送播放列表的命令
                else if(B_PUSH_MUSIC.equals(cmd)){
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_MEDIA_RESULT, command);
                    MyAppUtils.sendBroadcast(mContext, ACTION_MEDIA_RECV, bundle);
                }
                // 手机端发送控制播放命令
                else if(B_PUSH_MUSIC_CTRL.equals(cmd)){
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_MEDIA_RESULT, command);
                    MyAppUtils.sendBroadcast(mContext, ACTION_MEDIA_RECV, bundle);
                }

                // 定时关机
                else if(B_PUSH_TIMED_SHUTDOWN.equals(cmd)){

                    int countdownTime = jsonObj.optInt("countdownTime");
                    if(countdownTime < 0){
                        PushPresenter pp = new PushPresenter();
                        int type = jsonObj.optInt("type");
                        // 查询关机状态
                        if(-2 == countdownTime){
                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_MAIN_RECV_FROM, mContext.getPackageName());
                            bundle.putString(KEY_MAIN_RECV_FUNCTION, "shutdown");
                            bundle.putString(KEY_MAIN_RECV_RESULT, "-2");
                            MyAppUtils.sendBroadcast(mContext, ACTION_BATTERY_RECV, bundle);
                        }
                        // 取消关机
                        else if(-1 == countdownTime){
                            if (0 == type) {
                                pp.chat("取消关机");
                            } else {
                                pp.chat("取消重启");
                            }
                        }
                    }
                    // 执行关机
                    else {
                        int hh = countdownTime / 3600;
                        int mm = (countdownTime % 3600) / 60;
                        int ss = (countdownTime % 3600) % 60;
                        StringBuffer time = new StringBuffer();
                        if (hh > 0) {
                            time.append(hh + "小时");
                        }
                        if (mm > 0) {
                            time.append(mm + "分");
                        }
                        if (ss > 0) {
                            time.append(ss + "秒");
                        }
                        if (time.length() > 0) {
                            time.append("后");
                        }

                        PushPresenter pp = new PushPresenter();
                        int type = jsonObj.optInt("type");
                        if (0 == type) {
                            pp.chat(time + "关机");
                        } else {
                            pp.chat(time + "重启");
                        }
                    }
                }

            }

        }catch (JSONException e){
            LogUtils.e(TAG, "", e);
        }
    }
}
