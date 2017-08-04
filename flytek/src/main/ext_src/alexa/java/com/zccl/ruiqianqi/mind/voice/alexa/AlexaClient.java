package com.zccl.ruiqianqi.mind.voice.alexa;

import android.content.Context;

import com.google.gson.Gson;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.directive.Directive;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.event.Event;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.event.RecognizeEvent;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.event.RecognizeMsg;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.state.AlertsState;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.state.PlaybackState;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.state.SpeechState;
import com.zccl.ruiqianqi.mind.voice.alexa.beans.state.VolumeState;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.LogUtils;
import com.zccl.ruiqianqi.tools.ShareUtils;
import com.zccl.ruiqianqi.tools.StringUtils;
import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.http.http2.MyHttp2Client;
import com.zccl.ruiqianqi.tools.media.MyAudioRecorder;
import com.zccl.ruiqianqi.tools.media.MyMediaPlayer;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

import static com.zccl.ruiqianqi.mind.voice.alexa.Configuration.DIRECTIVE_URL;
import static com.zccl.ruiqianqi.mind.voice.alexa.Configuration.EVENT_URL;

/**
 * Created by ruiqianqi on 2017/2/14 0014.
 */

public class AlexaClient {

    private static String TAG = AlexaClient.class.getSimpleName();

    /**
     * Alexa currently returns speech as an mp3 audio file with the following characteristics:
     * Audio file with ID3 version 2.4.0,
     * contains: MPEG ADTS, layer III, v2, 48 kbps, 24 kHz, Monaural
     * audio/mpeg-L1
     * audio/mpeg-L2
     * audio/mpeg
     */
    private MyMediaPlayer mMyMediaPlayer;

    // 录音类
    private MyAudioRecorder mMyAudioRecorder;

    // 0表示录音 1表示音频数组
    private int mDataWay = 0;

    // 单麦录音
    private volatile boolean isRecording = false;
    // 五麦监听
    private volatile boolean isListening = false;

    // 全局上下文
    private Context mContext;
    // 识别上行请求
    private RecognizeEvent mRecognizeEvent = null;
    // 请求的TOKEN
    private String mAccessToken = null;


    // 分隔符
    private String mBoundary = "*#*#";

    public AlexaClient(Context context){
        this.mContext = context;
        init();

    }

    /**
     * 初始化
     */
    private void init(){
        // 创建播放器，直接播放音频
        mMyMediaPlayer = new MyMediaPlayer(mContext);
        // 创建录音器
        mMyAudioRecorder = new MyAudioRecorder(mContext);
        // TOKEN
        mAccessToken = ShareUtils.getP(mContext).getString("TOKEN", null);

        //initDownChannel();
        initProtocol();
    }

    /**
     * Establishing the downchannel stream
     */
    private void initDownChannel(){
        // AVS特有的头消息
        Request.Builder builder = Configuration.addGetHeaders(mAccessToken);
        MyHttp2Client.getAsync(builder, DIRECTIVE_URL);
    }

    /**
     * 初始化协议相关内容
     */
    private void initProtocol(){
        mRecognizeEvent = new RecognizeEvent();
        RecognizeMsg recognizeMsg = new RecognizeMsg();

        AlertsState alertsState = new AlertsState();

        PlaybackState playbackState = new PlaybackState();
        playbackState.payload.token = "";
        playbackState.payload.offsetInMilliseconds = 0;
        playbackState.payload.playerActivity = "IDLE";

        SpeechState speechState = new SpeechState();
        speechState.payload.token = "";
        speechState.payload.offsetInMilliseconds = 0;
        speechState.payload.playerActivity = "FINISHED";

        VolumeState volumeState = new VolumeState();
        volumeState.payload.volume = 50;
        volumeState.payload.muted = false;

        mRecognizeEvent.context.add(alertsState);
        mRecognizeEvent.context.add(playbackState);
        mRecognizeEvent.context.add(speechState);
        mRecognizeEvent.context.add(volumeState);
        mRecognizeEvent.event = recognizeMsg;

    }

    /**
     * Send streaming recognize requests to server.
     * 开始进行语音识别
     */
    public void startRecord(int dataWay){

        // 录音正在进行中，请稍候再试
        if(isRecording || isListening){
            return;
        }

        this.mDataWay = dataWay;

        // 单麦录音
        if(mDataWay == BaseVoice.DATA_SOURCE_TYPE.TYPE_RECORD.ordinal()){
            isRecording = true;
            try {
                mMyAudioRecorder.startRecord(new MyAudioRecorder.OnAudioCallback() {
                    @Override
                    public void OnAudio(byte[] data, int len) {
                        // 每次消息的ID
                        mRecognizeEvent.event.header.messageId = UUID.randomUUID().toString();
                        // AVS特有的头消息
                        Request.Builder builder = Configuration.addPostHeaders(mAccessToken, mBoundary);

                        byte[] newData = new byte[len];
                        System.arraycopy(data, 0, newData, 0, len);
                        MyHttp2Client.postAsync(builder, EVENT_URL, Event.getSpeechRecognizerEvent(), newData, new MyResponseCallback());
                    }
                });

            }catch (Exception e){

            }
            isRecording = false;
        }
        // 五麦音频数据
        else if(mDataWay == BaseVoice.DATA_SOURCE_TYPE.TYPE_RAW_DATA.ordinal()){
            isListening = true;

        }
    }

    /**
     * 直接加载语法识别语音数据
     * @param dataS
     */
    public void writeRecognizer(byte[] dataS){
        if(isListening) {
            // AVS特有的头消息
            Request.Builder builder = Configuration.addPostHeaders(mAccessToken, mBoundary);
            MyHttp2Client.postAsync(builder, EVENT_URL, Event.getSpeechRecognizerEvent(), dataS, new MyResponseCallback());
        }
    }

    /**
     * 停止录音，主动结束
     */
    public void stopRecord(){
        // 单麦录音
        if(mDataWay == BaseVoice.DATA_SOURCE_TYPE.TYPE_RECORD.ordinal()){
            mMyAudioRecorder.stopRecord();
        }
        // 五麦录音
        else {
            isListening = false;
        }
    }




    /**
     * 处理服务器的响应
     */
    private class MyResponseCallback implements MyHttp2Client.ResponseListener {

        private final Pattern PATTERN = Pattern.compile("<(.*?)>");

        // 下行指令集合
        private List<Directive> directives = new ArrayList<>();
        // 下行音频集合
        private HashMap<String, ByteArrayInputStream> audio = new HashMap<>();

        /**
         * 获得分隔线
         * @param response
         * @return
         */
        protected String getBoundary(Response response) {
            Headers headers = response.headers();
            String contentType = headers.get("content-type");
            String boundary = "";
            if (!StringUtils.isEmpty(contentType)) {
                Pattern pattern = Pattern.compile("boundary=(.*?);");
                Matcher matcher = pattern.matcher(contentType);
                if (matcher.find()) {
                    matcher.reset();
                    boundary = matcher.group(1);
                }
            }
            return boundary;
        }

        /**
         * Check if the response is JSON (a validity check)
         * @param headers the return headers from the AVS server
         * @return true if headers state the response is JSON, false otherwise
         */
        private boolean isJson(String headers) {
            if (headers.contains("application/json")) {
                return true;
            }
            return false;
        }

        /**
         * Get the content id from the return headers from the AVS server
         * @param headers the return headers from the AVS server
         * @return a string form of our content id
         */
        private String getCID(String headers) throws IOException {
            final String contentString = "Content-ID:";
            BufferedReader reader = new BufferedReader(new StringReader(headers));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                if (line.startsWith(contentString)) {
                    return line.substring(contentString.length()).trim();
                }
            }
            return null;
        }

        /**
         * Parse our directive using Gson into an object
         * @param directive the string representation of our JSON object
         * @return the reflected directive
         */
        private Directive getDirective(String directive) {
            Gson gson = new Gson();
            Directive.DirectiveWrapper wrapper = gson.fromJson(directive, Directive.DirectiveWrapper.class);
            if (wrapper.getDirective() == null) {
                return gson.fromJson(directive, Directive.class);
            }
            return wrapper.getDirective();
        }

        @Override
        public void OnSuccess(Response response) {

            /*
            try {
                LogUtils.e(TAG, response.headers().toString());
                LogUtils.e(TAG, response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
            */

            String boundary = getBoundary(response);
            LogUtils.e(TAG, "boundary = " + boundary);

            MultipartStream mpStream = new MultipartStream(response.body().byteStream(), boundary.getBytes(), 100000, null);
            try {
                if (mpStream.skipPreamble()) {
                    LogUtils.e(TAG, "Found initial boundary: true");

                    //we have to use the count hack here because otherwise readBoundary() throws an exception
                    int count = 0;
                    while (count < 1 || mpStream.readBoundary()) {
                        String headers;
                        try {
                            headers = mpStream.readHeaders();
                        } catch (MultipartStream.MalformedStreamException exp) {
                            break;
                        }
                        ByteArrayOutputStream data = new ByteArrayOutputStream();
                        mpStream.readBodyData(data);
                        if (!isJson(headers)) {
                            // get the audio data
                            // convert our multipart into byte data
                            String contentId = getCID(headers);
                            if(contentId != null) {
                                Matcher matcher = PATTERN.matcher(contentId);
                                if (matcher.find()) {
                                    String currentId = "cid:" + matcher.group(1);
                                    audio.put(currentId, new ByteArrayInputStream(data.toByteArray()));
                                }
                            }
                        } else {
                            // get the json directive
                            String directive = data.toString(Charset.defaultCharset().displayName());
                            directives.add(getDirective(directive));
                        }
                        count++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Directive directive: directives) {
                if(directive.isTypeSpeak()){
                    String cid = directive.getPayload().getUrl();
                    ByteArrayInputStream sound = audio.get(cid);

                    final File path = new File(MyConfigure.ZCCL_SDCARD, System.currentTimeMillis() + ".mp3");
                    try {
                        FileUtils.writeBytesToFile(IOUtils.toByteArray(sound), path.getAbsolutePath(), false);
                        sound.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LogUtils.e(TAG, "开始说话：" + path.length());
                    mMyMediaPlayer.playPath(path.getAbsolutePath(), null);

                }else if(directive.isTypePlay()){

                }
            }

            /*
            if(!StringUtils.isEmpty(boundary)) {
                String[] results = new String[0];
                try {
                    results = response.body().string().split(boundary);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < results.length; i++) {

                    if (results[i].contains("application/octet-stream")) {
                        int index = results[i].indexOf("octet-stream");
                        String data = results[i].substring(index + "octet-stream".length() + 4);

                        LogUtils.e(TAG, data);
                        byte[] voice = data.getBytes();

                        String filePath = MyConfigure.ZCCLRES + System.currentTimeMillis() + ".mp3";
                        FileUtils.writeBytesToFile(voice, filePath, false);
                        final File file = new File(filePath);

                        LogUtils.e(TAG, "开始说话");
                        myMediaPlayer.playPath(filePath, new PlayerCallback() {
                            @Override
                            public void OnEndPlay(MediaPlayer mp) {
                                file.delete();
                            }

                            @Override
                            public void OnError(MediaPlayer mp, int what, int extra) {
                                file.delete();
                            }
                        });

                    }
                }
            }
            */
        }

        @Override
        public void OnFailure(Throwable e) {

        }
    }

}
