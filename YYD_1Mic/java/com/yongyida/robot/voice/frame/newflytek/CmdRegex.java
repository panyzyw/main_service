package com.yongyida.robot.voice.frame.newflytek;

import android.content.Context;
import android.text.TextUtils;

import com.yongyida.robot.voice.utils.FileUtil;
import com.yongyida.robot.voice.utils.LogUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ruiqianqi on 2016/8/18 0018.
 */
public class CmdRegex {

    /** 单例引用 */
    private static CmdRegex instance;
    /** 全局上下文 */
    private Context context;

    /** 正则表达式加载器 */
    private Properties properties;
    /** 当前的指令 */
    private String currentAction;

    /**
     * 数据解析方式
     */
    enum AnalysisWay{
        REGEX_WAY,
        HASH_TABLE_WAY
    }
    /** 数据解析方式 */
    private AnalysisWay analysisWay = AnalysisWay.REGEX_WAY;

    private CmdRegex(Context context){
        this.context = context.getApplicationContext();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     * @return
     */
    public static CmdRegex getInstance(Context context) {
        if(instance == null) {
            synchronized(CmdRegex.class) {
                CmdRegex temp = instance;
                if(temp == null) {
                    temp = new CmdRegex(context);
                    instance = temp;
                }
            }
        }
        return instance;
    }

    /**
     * 初始化，加载正则匹配
     */
    private void init(){
        if(analysisWay== AnalysisWay.REGEX_WAY) {

        }
    }

    /**
     * 过滤出指令
     * @param words
     * @return
     */
    public String filter(String words){
        if(analysisWay== AnalysisWay.REGEX_WAY) {
            currentAction = filterRegex(words);
            return currentAction;
        }
        return null;
    }

    /**
     * 捕获打电话的名字
     * 012
     * 去掉整体捕获，还有两个捕获槽可用
     * @param words
     * @return
     */
    public String filterCatch(String words){
        if(!TextUtils.isEmpty(currentAction)){
            Pattern pat = Pattern.compile(properties.getProperty(currentAction));
            Matcher mat = pat.matcher(words);
            if(mat.find()){
                try {
                    String name = mat.group(1);
                    if(TextUtils.isEmpty(name)){
                        name = mat.group(2);
                    }
                    return name;
                }catch (Exception e){

                }
            }
        }
        return null;
    }

    /**
     * 加载正则表达式，使用正则表达式来匹配
     */
    public void loadRegex(String regex){
        properties = new Properties();
        try {
            InputStream is = FileUtil.getFileStream(context, "offline/"+regex+".regex");
            if(is!=null) {
                properties.load(is);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每个都匹配所有正则，没有就返回null
     * @param words
     * @return
     */
    private String filterRegex(String words){
        Iterator<Map.Entry<Object, Object>> it = properties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = it.next();
            Object key = entry.getKey();
            Object regex = entry.getValue();
            Pattern pat = Pattern.compile(regex+"");
            if(pat.matcher(words).matches()){
                LogUtils.showLogError("VoiceRegex", regex+"");
                return key+"";
            }
        }
        return null;
    }

}
