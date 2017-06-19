package com.zccl.ruiqianqi.socket.localsocket;

import android.net.LocalServerSocket;
import android.net.LocalSocket;

import com.zccl.ruiqianqi.tools.CheckUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.executor.rxutils.MyRxUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruiqianqi on 2017/2/6 0006.
 */

public class LocalServer implements Runnable{

    // 登录本地服务
    public static final String LOCAL_LOGIN = "/localserver/login";
    // 视频客户端
    public static final String VIDEO_CLIENT = "video";
    // SDK客户端
    public static final String SDK_CLIENT = "sdk";

    // 类标志
    private static String TAG = LocalServer.class.getSimpleName();
    // 本地SOCKET所用字符
    private static String NAME = "com.yongyida.robot.mainservice.tcp.server";


    // 本地LocalServerSocket
    private LocalServerSocket mLocalServerSocket = null;
    // 对应的客户端ID
    private Map<String, LocalSocket> mClientSocketMap = null;
    // 对应的客户端回调接口
    private Map<String, LocalSocketCallback> mLocalSocketCallbackMap = null;


    // 是不是结束了
    private boolean isOver = false;
    // 临时变量
    private LocalSocket tmpLocalSocket;

    // 线程局部变量，之客户端是否连接
    private ThreadLocal<Boolean> localConnected = new ThreadLocal<>();
    // 线程局部变量，之客户端ID
    private ThreadLocal<LocalSocket> localSocketFD = new ThreadLocal<>();
    // 线程局部变量，之客户端来自哪里
    private ThreadLocal<String> localSocketFrom = new ThreadLocal<>();

    public LocalServer(){
        try {
            mLocalServerSocket = new LocalServerSocket(NAME);
            mClientSocketMap = new HashMap<>();
            mLocalSocketCallbackMap = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!isOver){
            try {
                tmpLocalSocket = mLocalServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 新开线程，处理新客户端的连接请求
            MyRxUtils.doNewThreadRun(new Runnable() {
                @Override
                public void run() {

                    localConnected.set(true);
                    localSocketFD.set(tmpLocalSocket);

                    if(null != localSocketFD.get()){

                        DataInputStream is = null;
                        try {
                            is = new DataInputStream(localSocketFD.get().getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                            try {
                                localSocketFD.get().close();
                                localSocketFD.remove();
                                localConnected.set(false);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }

                        if(null != is && null != localSocketFD){
                            while (localConnected.get() ){
                                try {

                                    if(is.available() < 0){
                                        LogUtils.e(TAG, "is not available");
                                        continue;
                                    }

                                    // 过滤8个字节
                                    is.skip(8);

                                    // 协议总长度
                                    int allLength = is.readInt();
                                    // 头JSON信息
                                    int headerLength = is.readInt();

                                    byte[] headerBuf = null;
                                    if (headerLength > 0) {
                                        headerBuf = new byte[headerLength];
                                        is.readFully(headerBuf, 0, headerLength);
                                    }

                                    // 身体数据
                                    int bodyLength = is.readInt();
                                    byte[] bodyBuf = null;
                                    if (bodyLength > 0) {
                                        bodyBuf = new byte[bodyLength];
                                        is.readFully(bodyBuf, 0, bodyLength);
                                    }

                                    // 处理数据
                                    if(null != headerBuf) {
                                        String json = new String(headerBuf);

                                        LogUtils.e(TAG, "allLength = " + allLength);
                                        LogUtils.e(TAG, "headerLength = " + headerLength);
                                        LogUtils.e(TAG, "json = " + json);

                                        parseData(json, bodyBuf);
                                    }

                                } catch (IOException e) {
                                    LogUtils.e(TAG, "", e);

                                    try {
                                        is.close();
                                    } catch (IOException error) {
                                        error.printStackTrace();
                                    }

                                    localConnected.set(false);
                                }
                            }
                        }

                    }
                }
            });

        }
    }

    /**
     * 解析客户端传过来的数据
     * @param json
     */
    private void parseData(String json, byte[] data){
        try {
            JSONObject jsonObj = new JSONObject(json);
            String cmd = jsonObj.optString("cmd", null);

            if(LOCAL_LOGIN.equals(cmd)){
                String source = jsonObj.optString("source", null);
                if(StringUtils.isEmpty(source)){
                    return;
                }

                /*
                if (mClientSocketMap.get(source) != null) {
                    mClientSocketMap.get(source).close();
                    mClientSocketMap.remove(source);
                }
                */

                mClientSocketMap.put(source, localSocketFD.get());
                localSocketFrom.set(source);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 回调客户端，做对应的业务处理
        for(Map.Entry<String, LocalSocketCallback> entry : mLocalSocketCallbackMap.entrySet()){
            if(entry.getValue() != null){
                entry.getValue().OnResult(json, data);
            }
        }

    }

    /**
     * 发送数据.
     * @param source 对应的客户端标志
     * @param data   要发送的数据
     */
    public void sendData(String source, String data) {
        try {
            if(StringUtils.isEmpty(data))
                return;
            // 这里要是用data.length()就会出错了
            int dataLen = data.getBytes().length;
            int bufferLen = dataLen + 20;
            byte[] sendBuffer = new byte[bufferLen];

            byte[] allLen = CheckUtils.intToBigBytes(dataLen + 8);
            byte[] jsonLen = CheckUtils.intToBigBytes(dataLen);

            // 头，MSG_TYPE0，0、1、2
            sendBuffer[0] = 2;

            // 7个保留位
            sendBuffer[1] = 0;
            sendBuffer[2] = 0;
            sendBuffer[3] = 0;

            sendBuffer[4] = 0;
            sendBuffer[5] = 0;
            sendBuffer[6] = 0;
            sendBuffer[7] = 0;

            // 协议长度【从这里开始到尾的长度】
            sendBuffer[8] = allLen[0];
            sendBuffer[9] = allLen[1];
            sendBuffer[10] = allLen[2];
            sendBuffer[11] = allLen[3];

            // 数据长度
            sendBuffer[12] = jsonLen[0];
            sendBuffer[13] = jsonLen[1];
            sendBuffer[14] = jsonLen[2];
            sendBuffer[15] = jsonLen[3];

            // 数据内容
            System.arraycopy(data.getBytes(), 0, sendBuffer, 16, dataLen);

            LocalSocket channel = mClientSocketMap.get(source);
            if(null == channel)
                return;
            OutputStream os = channel.getOutputStream();
            os.write(sendBuffer, 0, sendBuffer.length);
            os.flush();

            LogUtils.e(TAG, data + "");

            // 这里不要关闭
            //os.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭服务端
     */
    public void close(){
        try {
            // 关闭循环
            isOver = true;
            // 关闭服务器
            mLocalServerSocket.close();

            // 关闭对应客户端
            for(Map.Entry<String, LocalSocket> entry : mClientSocketMap.entrySet()){
                if(entry.getValue() != null){
                    entry.getValue().close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置视频客户端对应的回调
     * @param localSocketCallback
     */
    public void setVideoClientCallback(LocalSocketCallback localSocketCallback){
        if(null != localSocketCallback) {
            mLocalSocketCallbackMap.put(VIDEO_CLIENT, localSocketCallback);
        }
    }

    public interface LocalSocketCallback {
        void OnResult(String json, byte[] data);
    }

}
