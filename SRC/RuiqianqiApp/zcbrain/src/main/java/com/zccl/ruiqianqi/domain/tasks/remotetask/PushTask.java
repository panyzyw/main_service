package com.zccl.ruiqianqi.domain.tasks.remotetask;

import com.zccl.ruiqianqi.presentation.presenter.MovePresenter;
import com.zccl.ruiqianqi.presentation.presenter.PhotoPresenter;
import com.zccl.ruiqianqi.presentation.presenter.PushPresenter;
import com.zccl.ruiqianqi.presentation.presenter.RemindPresenter;
import com.zccl.ruiqianqi.domain.model.pushdown.PushContent;
import com.zccl.ruiqianqi.domain.model.pushdown.PushRemind;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zccl.ruiqianqi.config.RemoteProtocol.B_CONTENT_PUSH;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_INSERT_UPDATE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_MOVE;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_QUERY;
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
            }

        }catch (JSONException e){
            LogUtils.e(TAG, "", e);
        }
    }
}
