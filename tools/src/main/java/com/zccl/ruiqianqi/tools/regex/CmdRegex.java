package com.zccl.ruiqianqi.tools.regex;

import android.content.Context;

import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

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

    private static String TAG = CmdRegex.class.getSimpleName();
    /** 单例引用 */
    private static CmdRegex instance;
    /** 全局上下文 */
    private Context mContext;

    /** 正则表达式加载器 */
    private Properties mProperties;
    /** 当前的指令 */
    private String currentKeyCmd;

    private CmdRegex(Context context){
        this.mContext = context.getApplicationContext();
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
     * 加载正则表达匹配的文本名
     * 加载哈希表的命令表文本名
     * @param command
     */
    public void load(String command){
        loadRegex(command);
    }

    /**
     * 加载正则表达式，使用正则表达式来匹配
     * @param regex     匹配命令的正则表达式文本
     */
    private void loadRegex(String regex){
        if(mProperties != null){
            mProperties.clear();
        }else {
            mProperties = new Properties();
        }
        try {
            InputStream is = FileUtils.getFileStream(mContext, regex + ".reg", MyConfigure.ONE_ASSETS);
            if(null != is) {
                mProperties.load(is);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 过滤出指令
     * @param words
     * @return
     */
    public String filter(String words){
        currentKeyCmd = filterRegex(words);
        return currentKeyCmd;
    }

    /**
     * 每个都匹配所有正则，没有就返回null
     * @param words
     * @return
     */
    private String filterRegex(String words){
        if(null == mProperties)
            return null;
        Iterator<Map.Entry<Object, Object>> it = mProperties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = it.next();
            Object keyCmd = entry.getKey();
            Object regexValue = entry.getValue();
            Pattern pat = Pattern.compile(regexValue + "");
            LogUtils.e(TAG, keyCmd + "--" + regexValue);
            if(pat.matcher(words).matches()){
                //LogUtils.e(TAG, keyCmd + "--" + regexValue);
                return keyCmd + "";
            }
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
        if(null == mProperties)
            return null;
        if(!StringUtils.isEmpty(currentKeyCmd)){
            String regexValue = mProperties.getProperty(currentKeyCmd);
            Pattern pat = Pattern.compile(regexValue);
            Matcher mat = pat.matcher(words);
            if(mat.find()){
                try {
                    String name = mat.group(1);
                    if(StringUtils.isEmpty(name)){
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
     * 构造命令表
     * @param hash 根据命令表，构造哈希表
     */
    /*
    private void loadHash(String hash){
        tree = new Tree();
        InputStream is = FileUtils.getFileStream(mContext, hash + ".hash", MyConfigure.ONE_ASSETS);
        String hashRule = FileUtils.readStreamToStr(is);

        String[] allRules = hashRule.split(";");
        if(allRules==null)
            return;

        //保存每次循环操作的节点
        List<TreeNode<Element>> saveNodeList = new ArrayList<>();
        //保存上次循环操作的节点
        List<TreeNode<Element>> tempNodeList = new ArrayList<>();

        for (int x = 0; x < allRules.length; x++) {
            tempNodeList.clear();
            saveNodeList.clear();

            String[] cmdRules = allRules[x].split("\\|");
            for (int i = 0; i<cmdRules.length; i++) {
                String tt = cmdRules[i].replace(",","").trim();
                char[] cmdChars = tt.toCharArray();
                for (int k = 0; k < cmdChars.length; k++) {
                    TreeNode<Element> temp = new TreeNode<>();
                    temp.setElement(new Element(String.valueOf(cmdChars[k]), k+1+""));
                    saveNodeList.add(temp);

                    if(tempNodeList.size()==0) {
                        tree.addNode(null, temp);
                    }else {
                        for (int h = 0; h <tempNodeList.size(); h++) {
                            tempNodeList.get(h).addChildNode(temp);
                        }
                    }
                }
                tempNodeList.clear();
                tempNodeList.addAll(saveNodeList);
                saveNodeList.clear();
            }
        }
    }
    */

    /**
     * 每个都匹配所有正则，没有就返回null
     * @param words
     * @return
     */
    /*
    private String filterHash(String words){
        List<TreeNode<Element>> treeNodes = tree.searchVague(words);
        if(treeNodes==null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i <treeNodes.size(); i++) {
            sb.append(treeNodes.get(i).getElement().getValue());
        }
        String result= sb.toString();
        if(result.length()!=words.length()){
            return null;
        }
        return result;
    }
    */

}
