package com.zccl.ruiqianqi.presentation.presenter;

import android.content.Intent;

import com.zccl.ruiqianqi.mind.voice.impl.beans.MusicBean;
import com.zccl.ruiqianqi.mind.voice.impl.beans.VideoBean;
import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.utils.LedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zccl.ruiqianqi.mind.voice.impl.function.FuncType.FUNC_MUSIC;
import static com.zccl.ruiqianqi.mind.voice.impl.function.FuncType.FUNC_MUSIC_CTRL;
import static com.zccl.ruiqianqi.mind.voice.impl.function.FuncType.FUNC_VIDEO;

/**
 * Created by ruiqianqi on 2017/5/24 0024.
 */

public class XiriPresenter extends BasePresenter {
    // 类标志
    public static String TAG = XiriPresenter.class.getSimpleName();

    // 搜索音乐播放
    public static final String SEARCH = "play";
    // 播放
    public static final String PLAY = "continue";
    // 暂停
    public static final String PAUSE = "pause";
    // 停止
    public static final String STOP = "stop";
    // 退出
    public static final String QUIT = "quit";
    // 上一首
    public static final String PREVIOUS = "previous";
    // 下一首
    public static final String NEXT = "next";
    // 换一首
    public static final String ANOTHER = "another";
    // 单曲循环
    public static final String CYCLE = "cycle";
    // 列表循环
    public static final String LIST_CYCLE = "list_cycle";
    // 顺序播放
    public static final String ORDER = "order";
    // 随机播放
    public static final String RANDOM = "random";
    // 收藏
    public static final String MARK = "mark";
    // 取消收藏
    public static final String DELETE = "delete";
    // 快进
    public static final String FAST = "fast";
    // 后退
    public static final String BACK = "back";
    
    /**
     * 识别以文字的形式传给语点
     * @param json
     */
    public void flyTekYuDian(String json, String funcType){
        if(FUNC_MUSIC.equals(funcType)){
            LogUtils.e(TAG, "flyTekYuDian_music");
            handleMusic(json);
        }
        else if(FUNC_VIDEO.equals(funcType)){
            LogUtils.e(TAG, "flyTekYuDian_video");
            handleVideo(json);
        }
    }

    /**
     * 处理讯飞音乐
     * @param json
     */
    private void handleMusic(String json){
        JSONObject jsonObject = null;
        JSONObject semantic = null;
        try {
            jsonObject = new JSONObject(json);
            semantic = jsonObject.getJSONObject("semantic");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(null == jsonObject || null == semantic)
            return;

        // 要执行的动作
        String action = null;
        // 携带的值
        String value = null;
        // 音乐实体
        MusicBean musicBean = null;

        String service = jsonObject.optString("service");

        /*
        // 解析智能家居
        if(FUNC_SMART_HOME.equals(service)){
            try {
                JSONObject slots = semantic.getJSONObject("slots");
                action = slots.optString("action");
                if("PREVIOUS".equals(action)){
                    action = PREVIOUS;
                }
                else if("NEXTS".equals(action)){
                    action = NEXT;
                }
                else if("KUAITUI".equals(action)){
                    action = BACK;
                }
                else if("KUAIJIN".equals(action)){
                    action = FAST;
                }
                else if("PLAY".equals(action)){
                    action = PLAY;
                }
                else if("STOPPED".equals(action)){
                    action = STOP;
                }
                else if("PAUSE".equals(action)){
                    action = PAUSE;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // 电视控制
        else if(FUNC_TV_CONTROL.equals(service)){
            String text = jsonObject.optString("text");
            if(!StringUtils.isEmpty(text)) {
                sendCommand(text);
            }
            return;
        }
        */

        // 解析音乐播放
        if(FUNC_MUSIC.equals(service) || FUNC_MUSIC_CTRL.equals(service)){
            musicBean = JsonUtils.parseJson(json, MusicBean.class);
            if(null == musicBean){
                return;
            }
            if(null == musicBean.semantic){
                return;
            }
            if(null != musicBean.semantic.slots){
                action = musicBean.semantic.slots.action;
                value = musicBean.semantic.slots.value;
            }
        }

        LogUtils.e(TAG, action + "");

        if(SEARCH.equals(action) || StringUtils.isEmpty(action)){
            search(musicBean);
        }
        else if(PAUSE.equals(action)){
            pause();
        }
        else if(PLAY.equals(action)){
            play();
        }
        else if(QUIT.equals(action)){
            quit();
        }
        else if(PREVIOUS.equals(action)){
            previous();
        }
        else if(NEXT.equals(action)){
            next();
        }
        else if(ANOTHER.equals(action)){
            another();
        }
        else if(CYCLE.equals(action)){
            cycle();
        }
        else if(LIST_CYCLE.equals(action)){
            listCycle();
        }
        else if(ORDER.equals(action)){
            order();
        }
        else if(RANDOM.equals(action)){
            random();
        }
        else if(MARK.equals(action)){
            mark();
        }
        else if(DELETE.equals(action)){
            delete();
        }
        else if(FAST.equals(action)){
            fast(value);
        }
        else if(BACK.equals(action)){
            back(value);
        }

    }

    /**
     * 讯飞音乐已经启动后的语料
     * @param action
     * @param text
     */
    public void xfMusicAction(String action, String text){
        if(PAUSE.equals(action)){
            pause();
        }
        else if(PLAY.equals(action)){
            play();
        }
        else if(QUIT.equals(action)){
            quit();
        }
        else if(PREVIOUS.equals(action)){
            previous();
        }
        else if(NEXT.equals(action)){
            next();
        }
        else if(ANOTHER.equals(action)){
            another();
        }
        else if(CYCLE.equals(action)){
            cycle();
        }
        else if(LIST_CYCLE.equals(action)){
            listCycle();
        }
        else if(ORDER.equals(action)){
            order();
        }
        else if(RANDOM.equals(action)){
            random();
        }
        else if(MARK.equals(action)){
            mark();
        }
        else if(DELETE.equals(action)){
            delete();
        }
        else if(FAST.equals(action)){
            sendCommand(text);
        }
        else if(BACK.equals(action)){
            sendCommand(text);
        }
    }


    /**
     * 处理讯飞视频
     * @param json
     */
    private void handleVideo(String json){
        VideoBean videoBean = JsonUtils.parseJson(json, VideoBean.class);
        if(null == videoBean){
            return;
        }
        if(null == videoBean.semantic){
            return;
        }
        if(null != videoBean.semantic.slots){
            String action = videoBean.semantic.slots.action;
            String value = videoBean.semantic.slots.value;
            sendCommand("点播" + value);
        }
    }

    /**
     * 搜索音乐并进行播放
     * @param musicBean
     */
    private void search(MusicBean musicBean){
        if(null == musicBean)
            return;
        String text = "我要听";
        MusicBean.Slots slots = musicBean.semantic.slots;
        if(null != slots) {
            if (!StringUtils.isEmpty(slots.category)) {
                text += slots.category /*+ "歌曲"*/;
            } else {
                if (!StringUtils.isEmpty(slots.artist)) {
                    text += slots.artist + "的";
                }
                if (!StringUtils.isEmpty(slots.song)) {
                    text += slots.song;
                } else {
                    text += "歌曲";
                }
            }
        }else {
            text += "歌曲";
        }
        sendCommand(text);
        // 开始播放音乐
        LedUtils.startSpeakLed(mContext);
    }

    /**
     * 暂停
     */
    private void pause(){
        sendCommand("暂停");
        // 暂停播放音乐
        LedUtils.endSpeakLed(mContext);
    }

    /**
     * 播放
     */
    private void play(){
        sendCommand("播放");
        // 继续播放音乐
        LedUtils.startSpeakLed(mContext);
    }

    /**
     * 退出播放，返回
     */
    private void quit(){
        sendCommand("退出");
        // 继续播放音乐
        LedUtils.endSpeakLed(mContext);
    }

    /**
     * 上一首
     */
    private void previous(){
        sendCommand("上一首");
    }

    /**
     * 下一首
     */
    private void next(){
        sendCommand("下一首");
    }

    /**
     * 换一首，随便一搜
     */
    private void another(){
        sendCommand("我要听音乐");
        // 继续播放音乐
        LedUtils.startSpeakLed(mContext);
    }

    /**
     * 单曲循环
     */
    private void cycle(){
        sendCommand("单曲循环");
    }

    /**
     * 循环播放
     */
    private void listCycle(){
        sendCommand("循环播放");
    }

    /**
     * 顺序播放
     */
    private void order(){
        sendCommand("顺序播放");
    }

    /**
     * 随机播放
     */
    private void random(){
        sendCommand("随机播放");
    }

    /**
     * 收藏
     */
    private void mark(){
        sendCommand("收藏");
    }

    /**
     * 取消收藏
     */
    private void delete(){
        sendCommand("取消收藏");
    }

    /**
     * 快进
     * @param value
     */
    private void fast(String value){
        String text = "快进";
        if(!StringUtils.isEmpty(value)) {
            text += value;
        }
        sendCommand(text);
    }

    /**
     * 后退
     * @param value
     */
    private void back(String value){
        String text = "后退";
        if(!StringUtils.isEmpty(value)) {
            text += value;
        }
        sendCommand(text);
    }


    /**
     * 发送命令给讯飞语点
     * @param text
     */
    public void sendCommand(String text){
        LogUtils.e(TAG, text + "");
        // 把语义识别原文传给讯飞语点
        // 然后再按讯飞语点的逻辑走流程
        Intent intent = new Intent("com.iflytek.xiri2.START");
        // 讯飞语点包名
        intent.setPackage("com.iflytek.xiri");
        intent.putExtra("text", text);
        intent.putExtra("startmode", "text");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startService(intent);
    }

}
