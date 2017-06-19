package com.zccl.ruiqianqi.mind.voice.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by ruiqianqi on 2017/6/7 0007.
 */

public class GSpeechConfig {
    /**
     * 根据标志选择语言
     * en     --------- 英文
     * en-US  --------- 美式英文
     * en-GB  --------- 英式英文
     * zh-CN  --------- 简体中文
     * zh-HK  --------- 香港繁体
     * zh-TW  --------- 台湾繁体
     * @param languageTag
     * @return
     */
    public static String Language_USE = "en-US";
    // 地址
    public static final String HOST = "speech.googleapis.com";
    // 端口
    public static final Integer PORT = 443;
    // 验证
    public static final List<String> OAUTH2_SCOPES = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");
    // 验证
    public static final List<String> SCOPE = Collections.singletonList("https://www.googleapis.com/auth/cloud-platform");

}
