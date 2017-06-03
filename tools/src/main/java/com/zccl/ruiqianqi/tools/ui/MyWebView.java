package com.zccl.ruiqianqi.tools.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

/**
 * Created by ruiqianqi on 2016/11/2 0002.
 */

public class MyWebView extends WebView{

    /** 加载中使用SD卡路径资源 */
    private String pathSdcard="file:///sdcard/msc/100.gif";

    /** 加载中使用ASSETS路径资源 */
    private String pathAssets="file:///android_asset/run.gif";

    public MyWebView(Context context) {
        super(context);

        init();
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    /**
     * 初始化
     */
    @SuppressLint("JavascriptInterface")
    private void init(){
        // 设置编码
        getSettings().setDefaultTextEncodingName("utf-8");
        // 支持JS
        getSettings().setJavaScriptEnabled(true);
        // 设置可以访问文件
        getSettings().setAllowFileAccess(true);
        // 支持通过JS打开新窗口
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //不使用缓存
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //优先使用缓存
        getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 设置本地调用对象及其接口
        addJavascriptInterface(new JavaScript(getContext()), "JsObj");
        // 载入js
        loadUrl("file:///android_asset/test.html");
    }

    /**
     * 调用JS方法
     */
    public void callJsMethod(){
        loadUrl("javascript:funFromJs()");
    }

    /**
     * file:///android_asset/test.html
     * http://baidu.com
     * @param url
     */
    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);

        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);

                /*
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                MyWebView.this.getContext().startActivity(intent);
                */

                return true;
            }
        });
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        super.loadUrl(url, additionalHttpHeaders);
    }

    @Override
    public void loadData(String data, String mimeType, String encoding) {
        super.loadData(data, mimeType, encoding);
    }

    @Override
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        super.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }


}
