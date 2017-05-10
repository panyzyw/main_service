package com.yongyida.robot.voice.frame.newflytek;

import android.text.TextUtils;

import com.yongyida.robot.voice.frame.newflytek.bean.OpenQA;
import com.yongyida.robot.voice.utils.LogUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiqianqi on 2016/10/23 0023.
 */

public class MyBNFCreator implements Runnable{

    private static final String TAG = MyBNFCreator.class.getSimpleName();

    /** 数据库里面的字符串分隔符 */
    private static final String SPLIT_STR = "\\|";
    private static String BNF_HEAD = "#BNF+IAT 1.0 UTF-8;\n" + "!grammar dynamic;\n";
    private static String SLOT1 = "!slot ";
    private static String SLOT2 = "<sb";
    private static String SLOT3 = ">;\n";
    private static String SLOT4 = "!start <xStart>;\n\n";

    private static String ASR1 = "<xStart>:\n";
    private static String ASR2 = "<sb";
    private static String ASR3 = ">|\n";
    private static String ASR4 = ">;\n\n";

    private static String WORD1 = "<sb";
    private static String WORD2 = ">:";
    private static String WORD3 = "!id(";
    private static String WORD4 = ");\n";

    /** 全局上下文 */
    private VoiceRecognizer mVoiceRecognizer;
    /** 离线问题列表 */
    private String[] mQuestions;
    /** 离线答案列表 */
    private String[] mAnswers;

    private List<OpenQA> openQAList;
    private String grammarPath;
    public MyBNFCreator(VoiceRecognizer voiceRecognizer, String[] mQuestions, String[] mAnswers, String mPath){
        this.mVoiceRecognizer = voiceRecognizer;
        this.mQuestions = mQuestions;
        this.mAnswers = mAnswers;
        this.grammarPath = mPath;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        openQAList = new ArrayList<>();
        for (int i = 0; i < mQuestions.length; i++) {
            OpenQA openQA = new OpenQA();
            openQA.question = mQuestions[i];
            openQA.answer = mAnswers[i];
            openQAList.add(openQA);
        }
    }

    @Override
    public void run() {
        try {
            FileOutputStream output = new FileOutputStream(grammarPath);
            StringBuffer sb = new StringBuffer();
            // 写头
            output.write(BNF_HEAD.getBytes());

            // 声明变量
            int countIndex = 0;
            for (int i = 0; i < openQAList.size(); i++) {

                if(TextUtils.isEmpty(openQAList.get(i).question)){
                    continue;
                }
                String[] questions = openQAList.get(i).question.split(SPLIT_STR);
                if(questions != null ){
                    for (int j = 0; j < questions.length ; j++) {
                        sb.setLength(0);
                        sb.append(SLOT1);
                        sb.append(SLOT2);
                        sb.append(++countIndex);
                        sb.append(SLOT3);
                        output.write(sb.toString().getBytes());
                    }
                }
            }
            output.write(SLOT4.getBytes());

            // 组合语句
            output.write(ASR1.getBytes());

            countIndex = 0;
            for (int i = 0; i < openQAList.size(); i++) {
                if(TextUtils.isEmpty(openQAList.get(i).question)){
                    continue;
                }
                String[] questions = openQAList.get(i).question.split(SPLIT_STR);
                if(questions != null ){
                    for (int j = 0; j < questions.length; j++) {
                        sb.setLength(0);
                        sb.append(ASR2);
                        sb.append(++countIndex);
                        if(!(i==openQAList.size()-1 && j==questions.length-1)){
                            sb.append(ASR3);
                        }else {
                            sb.append(ASR4);
                        }
                        output.write(sb.toString().getBytes());
                    }
                }

            }

            // 定义变量
            countIndex = 0;
            for (int i = 0; i < openQAList.size(); i++) {
                if(TextUtils.isEmpty(openQAList.get(i).question)){
                    continue;
                }
                String[] questions = openQAList.get(i).question.split(SPLIT_STR);
                if(questions != null ){
                    for (int j = 0; j < questions.length; j++) {
                        sb.setLength(0);
                        sb.append(WORD1);
                        sb.append(++countIndex);
                        sb.append(WORD2);
                        sb.append(questions[j]);
                        sb.append(WORD3);
                        sb.append(i+1);
                        sb.append(WORD4);
                        output.write(sb.toString().getBytes());
                    }
                }
            }
            output.close();

            //mVoiceRecognizer.loadOfflineGrammar(grammarPath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
