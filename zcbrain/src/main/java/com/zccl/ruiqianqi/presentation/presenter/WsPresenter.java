package com.zccl.ruiqianqi.presentation.presenter;

import com.zccl.ruiqianqi.presenter.base.BasePresenter;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.ByteString;

/**
 * Created by ruiqianqi on 2017/7/31 0031.
 */

public class WsPresenter extends BasePresenter {
    // 类标志
    private static String TAG = WsPresenter.class.getSimpleName();
    // 单例引用
    private static WsPresenter instance;
    // 单线程，队列
    //private ExecutorService writeExecutor;
    /**
     * 私有化构造方法
     */
    private WsPresenter() {
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)
                .readTimeout(5000, TimeUnit.MILLISECONDS).build();
        //writeExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * 用这个用话，instance不需要用volatile修饰
     *
     * @return
     */
    public static WsPresenter getInstance() {
        if (instance == null) {
            synchronized (WsPresenter.class) {
                WsPresenter temp = instance;
                if (temp == null) {
                    temp = new WsPresenter();
                    instance = temp;
                }
            }
        }
        return instance;
    }



    /********************************【模拟服务器端】**********************************************/
    // WebSocket服务端
    private MockWebServer mMockWebServer;
    // WebSocket监听器
    private WebSocketListener mWebSocketServerListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // Sec-WebSocket-Key: bOeemu2y2L5dA55zlJMYnQ==;
            LogUtils.e(TAG, "server request header: " + response.request().headers());
            LogUtils.e(TAG, "server response header: " + response.headers());
            LogUtils.e(TAG, "server response: " + response);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            LogUtils.e(TAG, "onMessageRecv1: " + text);

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            LogUtils.e(TAG, "onMessageRecv2: " + bytes.string(Charset.defaultCharset()));
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            LogUtils.e(TAG, "onClosing = " + code + "-" + reason);
            addServerListener();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            LogUtils.e(TAG, "onClosed = " + code + "-" + reason);
            addServerListener();
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            LogUtils.e(TAG, "onFailure = " + response, t);
            addServerListener();
        }
    };

    /**
     * 创建WebSocket服务端
     * @param port 监听的端口
     */
    public void createWsServer(int port){
        try {
            mMockWebServer = new MockWebServer();
            mMockWebServer.start(port);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        addServerListener();
    }

    /**
     * 添加监听
     */
    private void addServerListener(){
        mMockWebServer.enqueue(new MockResponse().withWebSocketUpgrade(mWebSocketServerListener));
    }

    /**
     * 关闭服务端
     */
    public void closeServer(){
        try {
            if(null != mMockWebServer) {
                mMockWebServer.close();
                mMockWebServer = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**********************************【客户端】**************************************************/
    /**
     1000 indicates a normal closure, meaning that the purpose for
     which the connection was established has been fulfilled.

     1001 indicates that an endpoint is "going away", such as a server
     going down or a browser having navigated away from a page.

     1002 indicates that an endpoint is terminating the connection due
     to a protocol error.

     1003 indicates that an endpoint is terminating the connection
     because it has received a type of data it cannot accept (e.g., an
     endpoint that understands only text data MAY send this if it
     receives a binary message).
     */
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    // 客户端
    private OkHttpClient client;
    // 客户端请求
    private Request mWebClientRequest;
    // WebSocket对象
    private WebSocket mWebClientSocket;
    // WebSocket监听器
    private WebSocketListener mWebSocketClientListener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // Sec-WebSocket-Key: bOeemu2y2L5dA55zlJMYnQ==;
            LogUtils.e(TAG, "client request header: " + response.request().headers());
            LogUtils.e(TAG, "client response header: " + response.headers());
            LogUtils.e(TAG, "client response: " + response);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            LogUtils.e(TAG, "onClientRecv1: " + text);

        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            LogUtils.e(TAG, "onClientRecv2: " + bytes.string(Charset.defaultCharset()));

        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            LogUtils.e(TAG, "onClientClosing = " + code + "-" + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            LogUtils.e(TAG, "onClientClosed = " + code + "-" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            LogUtils.e(TAG, "onClientFailure = " + response, t);
        }
    };


    /**
     * 连接到WebSocket服务端
     * @param wsAddr ws://127.0.0.1:9001/
     */
    public void connectWebSocket(String wsAddr){
        LogUtils.e(TAG, "开始建立WebSocket连接");
        close("init");
        mWebClientRequest = new Request.Builder().url(wsAddr).build();
        // new 一个WebSocket调用对象并建立连接
        client.newWebSocket(mWebClientRequest, mWebSocketClientListener);
    }

    /**
     * 发送数据
     * @param msg
     */
    public void sendMsg(String msg){
        MyRxUtils.doAsyncRun(new Runnable() {
            @Override
            public void run() {
                if(null != mWebClientSocket){
                    mWebClientSocket.send(msg);
                }
            }
        });
    }

    /**
     * 关闭连接
     * @param reason
     */
    public void close(String reason){
        if (null != mWebClientSocket) {
            mWebClientSocket.close(NORMAL_CLOSURE_STATUS, reason);
            mWebClientSocket = null;
        }
    }

    /**
     * 销毁客户端
     */
    public void destroy() {
        if (null != client) {
            client.dispatcher().executorService().shutdown();
            client = null;
        }
    }

}
