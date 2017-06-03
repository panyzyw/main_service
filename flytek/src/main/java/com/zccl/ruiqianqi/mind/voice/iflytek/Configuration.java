package com.zccl.ruiqianqi.mind.voice.iflytek;

import android.content.Context;
import android.content.DialogInterface;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zccl.ruiqianqi.mind.voice.R;
import com.zccl.ruiqianqi.mind.voice.iflytek.beans.asr.PlusResult;
import com.zccl.ruiqianqi.tools.Download;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.JsonUtils;
import com.zccl.ruiqianqi.tools.MyAppUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.dialog.MyAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import static com.zccl.ruiqianqi.mind.voice.iflytek.Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_MSC;
import static com.zccl.ruiqianqi.mind.voice.iflytek.Configuration.E_ASR_TYPE.TYPE_ASR_OFFLINE_PLUS;
import static com.zccl.ruiqianqi.mind.voice.iflytek.Configuration.E_SPEAKER_TYPE.TYPE_SPEAKER_OFFLINE_MSC;

/**
 * Created by ruiqianqi on 2016/7/28 0028.
 */
public class Configuration {

    // 类标志
    private static String TAG = Configuration.class.getSimpleName();

    /**
     * 是不是使用英文
     * 中文为zh，
     * 英文为en，
     * 日文为ko；
     * */
    public static String Language = MyConfigure.getLanguage();

    /**
     * 语音听写
     * 在线命令词
     * 离线命令词【MSC】
     * 离线命令词【语记】
     */
    public enum E_ASR_TYPE {
        TYPE_ASR_LISTEN,
        TYPE_ASR_ONLINE,
        TYPE_ASR_OFFLINE_MSC,
        TYPE_ASR_OFFLINE_PLUS,
    }

    /**
     * 在线发音人
     * 离线发音人【MSC】
     * 离线发音人【语记】
     */
    public enum E_SPEAKER_TYPE {
        TYPE_SPEAKER_ONLINE,
        TYPE_SPEAKER_OFFLINE_MSC,
        TYPE_SPEAKER_OFFLINE_PLUS,
    }

    /**
     * 语音功能选择
     */
    public static E_ASR_TYPE ASR_TYPE = TYPE_ASR_OFFLINE_MSC;
    /**
     * 配置发音人
     * 为什么要想有切换功能，必须以语记开始呢
     */
    public static E_SPEAKER_TYPE SPEAKER_TYPE = TYPE_SPEAKER_OFFLINE_MSC;
    /**
     * 云端发音人 aisxa
     * 本地发音人 jiajia xiaofeng xiaoyan
     * 语记发音人 ""
     */
    public static final String SPEAKER_NAME = "jiajia";

    // 是否使用VIP通道
    private static final boolean isUseVip = false;

    /**
     * 初始化语音服务
     * @param context
     */
    protected static void initSpeech(Context context){

        // 应用的ID
        String appId = context.getString(R.string.app_id);


        // 离线：语记引擎
        if(TYPE_ASR_OFFLINE_PLUS == Configuration.ASR_TYPE ||
                E_SPEAKER_TYPE.TYPE_SPEAKER_OFFLINE_PLUS == Configuration.SPEAKER_TYPE){
            /*
            auto：表示云端优先使用MSC，本地优先使用语记；
            msc： 只使用MSC；
            plus：只使用语记。
            */
            StringBuffer param = new StringBuffer();
            param.append(SpeechConstant.APPID + "=" + appId);
            param.append(",");
            param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_PLUS);
            if(isUseVip) {
                param.append(",");
                param.append("http://dz-yydrobot.xf-yun.com/msp.do");
            }
            SpeechUtility.createUtility(context, param.toString());
        }

        // 离线：MSC引擎
        else if(E_ASR_TYPE.TYPE_ASR_OFFLINE_MSC == Configuration.ASR_TYPE ||
                E_SPEAKER_TYPE.TYPE_SPEAKER_OFFLINE_MSC == Configuration.SPEAKER_TYPE){
            /*
            auto：表示云端优先使用MSC，本地优先使用语记；
            msc： 只使用MSC；
            plus：只使用语记。
            */
            StringBuffer param = new StringBuffer();
            param.append(SpeechConstant.APPID + "=" + appId);
            param.append(",");
            param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
            if(isUseVip) {
                param.append(",");
                param.append("http://dz-yydrobot.xf-yun.com/msp.do");
            }
            SpeechUtility.createUtility(context, param.toString());
        }
        // 在线：MSC引擎
        else {

            // 初始化语音设置
            //SpeechUtility.createUtility(context, SpeechConstant.APPID + "=" + appId);

            StringBuffer param = new StringBuffer();
            param.append(SpeechConstant.APPID + "=" + appId);
            if(isUseVip) {
                param.append(",");
                param.append("http://dz-yydrobot.xf-yun.com/msp.do");
            }
            SpeechUtility.createUtility(context, param.toString());

            // auto：表示云端优先使用MSC，本地优先使用语记；
        }

        // 开启科大讯飞日志调试功能
        // debugMSC(context);
    }

    /**
     * 打开语记设置界面
     */
    public static void openSetting(){
        // 打开语记
        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_TTS);
        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_ASR);
        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_IVW);
    }

    /**
     * 获取语记是否包含离线听写资源，如未包含跳转至资源下载页面
     * 1.PLUS_LOCAL_ALL: 本地所有资源
     * 2.PLUS_LOCAL_ASR: 本地识别资源
     * 3.PLUS_LOCAL_TTS: 本地合成资源
     */
    public static PlusResult checkVoiceResource(){
        String resource = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_ALL);
        if(!StringUtils.isEmpty(resource)){
            PlusResult plusResult = JsonUtils.parseJson(resource, PlusResult.class);
            if(plusResult.getRet()==ErrorCode.SUCCESS){
                return plusResult;
            }
        }
        return null;
    }

    /**
     * 获取语记是否包含离线听写资源，如未包含跳转至资源下载页面
     * 1.PLUS_LOCAL_ALL: 本地所有资源
     * 2.PLUS_LOCAL_ASR: 本地识别资源
     * 3.PLUS_LOCAL_TTS: 本地合成资源
     *
     * @param context 对话框所依附的上下文
     */
    public static PlusResult checkVoiceResource(Context context){
        String resource = SpeechUtility.getUtility().getParameter(SpeechConstant.PLUS_LOCAL_ALL);
        if(!StringUtils.isEmpty(resource)){
            try {
                JSONObject result = new JSONObject(resource);
                int ret = result.getInt(SpeechUtility.TAG_RESOURCE_RET);
                switch (ret) {
                    case ErrorCode.SUCCESS:
                        PlusResult plusResult = JsonUtils.parseJson(resource, PlusResult.class);
                        if(null == plusResult){
                            showDialog(context, "离线包下载提示", "请安装离线包", ret);
                        }
                        return plusResult;

                    case ErrorCode.ERROR_COMPONENT_NOT_INSTALLED:
                        showDialog(context, "下载提示", "没有安装语记，是否安装", ret);
                        break;

                    case ErrorCode.ERROR_VERSION_LOWER:
                        showDialog(context, "更新提示", "语记版本过低，是否更新", ret);
                        break;

                    case ErrorCode.ERROR_INVALID_RESULT:
                        showDialog(context, "运行提示", "语记没有运行，是否运行", ret);
                        break;

                    case ErrorCode.ERROR_SYSTEM_PREINSTALL:

                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                showDialog(context, "出错提示", "语记出错了，是否重新下载", ErrorCode.ERROR_COMPONENT_NOT_INSTALLED);
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 弹出对话框，提示用户是否需要安装语记【只有在activity可见的时候才能弹出来】
     * Can not perform this action after onSaveInstanceState
     *
     * @param context  对话框所依附的上下文
     * @param title     对话框标题
     * @param content   对话框内容
     * @param operation 对应的操作结果
     */
    public static void showDialog(final Context context, String title, String content, final int operation){

        DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 没有安装或版本太低
                if(operation==ErrorCode.ERROR_COMPONENT_NOT_INSTALLED ||
                        operation==ErrorCode.ERROR_VERSION_LOWER){
                    Download.downloadInstall(context, SpeechUtility.getUtility().getComponentUrl());
                }
                // 已安装，但没有启动
                else if(operation==ErrorCode.ERROR_INVALID_RESULT){
                    MyAppUtils.openApp(context, "com.iflytek.vflynote");
                }
                dialog.dismiss();
            }
        };
        MyAlertDialog.showConfirm(context, title, content, positiveListener);
    }

    /**
     * 开启科大讯飞日志调试功能
     * @param context
     */
    public static void debugMSC(Context context){
        InputStream is = FileUtils.getAssetsFileStream(context, "debug/msc.cfg");
        FileUtils.writeStreamToFile(is, "/sdcard/msc/msc.cfg", false);
    }
}
