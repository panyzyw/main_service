package com.zccl.ruiqianqi.tools.media;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zc on 2015/11/10.
 5 bits: 2 (00010) //编解码类型：AAC-LC = 2
 4 bits: 4 (0100)  //采样率44100 = 4
 4 bits: 2 (0010)  //声道 = 2
 1 bit: 0 (0)      //标志位，位于表明IMDCT窗口长度= 0
 1 bit: 0 (0)      //标志位，表明是否依赖于corecoder = 0
 1 bit: 0 (0)      //选择了AAC-LC = 0

 There are 13 supported frequencies:
 0: 96000 Hz
 1: 88200 Hz
 2: 64000 Hz
 3: 48000 Hz
 4: 44100 Hz
 5: 32000 Hz
 6: 24000 Hz
 7: 22050 Hz
 8: 16000 Hz
 9: 12000 Hz
 10: 11025 Hz
 11: 8000 Hz
 12: 7350 Hz
 13: Reserved
 14: Reserved
 15: frequency is written explictly

 Byte 1: 00010010
 Byte 2: 00010000

 00010010 00010000
 [ 2 ][ 4 ][2 ][0]

 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MyAudioDecoder{

    /** 类标识 */
    private static String TAG = MyAudioDecoder.class.getSimpleName();

    private MyAudioTrack audioPlayer;
    /** 编解码器 */
    private MediaCodec mediaCodec;
    /** 编解码格式 */
    private MediaFormat mediaFormat;
    /** 硬解数据信息缓存 */
    private BufferInfo info = null;
    /** 输入buffers */
    private ByteBuffer[] inputBuffers = null;
    /** 输出buffers */
    private ByteBuffer[] outputBuffers = null;

    private MediaExtractor mediaExtractor;

    public MyAudioDecoder(){
        initAudio();
        //getSupportTypes();
    }

    /**
     * 初始化音频播放器
     * mp3为audio/mpeg
     * aac为audio/mp4a-latm
     * mp4为video/mp4v-es
     *
     * audio/mpeg-L1
     * audio/mpeg-L2
     * audio/mpeg
     */
    private void initAudio(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {

                // 此类可分离视频文件的音轨和视频轨道
               /* mediaExtractor = new MediaExtractor();
                mediaExtractor.setDataSource(MyConfigure.SDCARD + "0.mp3");

                mediaFormat = mediaExtractor.getTrackFormat(0);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                int sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                // 声道个数：单声道或双声道
                int channels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);

                LogUtils.e(TAG, "mime = " + mime);
                LogUtils.e(TAG, "sampleRate = " + sampleRate);
                LogUtils.e(TAG, "channels = " + channels);
                */

                mediaFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_MPEG, 24000, 1);

                //byte[] bytes = new byte[]{(byte) 0x13, (byte)0x08};
                //ByteBuffer bb = ByteBuffer.wrap(bytes);
                //mediaFormat.setByteBuffer("csd-0", bb);

                mediaFormat.setInteger(MediaFormat.KEY_IS_ADTS, 1);
                //mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);

                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);

                mediaCodec = MediaCodec.createDecoderByType(mime);
                mediaCodec.configure(mediaFormat, null, null, 0);

                // 底层的解码开始启动，等待传入数据
                mediaCodec.start();

                // MediaCodec在此ByteBuffer[]中获取输入数据
                inputBuffers = mediaCodec.getInputBuffers();
                // MediaCodec将解码后的数据放到此ByteBuffer[]中 我们可以直接在这里面得到PCM数据
                outputBuffers = mediaCodec.getOutputBuffers();


                LogUtils.e(TAG, "initAudio初始化音频播放器");

            } catch (IOException e) {
                e.printStackTrace();
            }

            // 用于描述解码得到的byte[]数据的相关信息
            info = new BufferInfo();
            audioPlayer = new MyAudioTrack();

        }else {

        }
    }

    /**
     * 打印内置的编解码器
     */
    private void getSupportTypes(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            int numCodecs = MediaCodecList.getCodecCount();
            for (int i = 0; i < numCodecs; i++) {
                MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
                String[] types = codecInfo.getSupportedTypes();
                for (int j = 0; j <types.length ; j++) {
                    LogUtils.e(TAG, types[j]);
                }
            }
        }
    }

    /**
     * 开始解码
     * @param buf
     */
    public void decodeAudio(byte[] buf) {

        if(buf != null) {

            try {
                // 时间单位为微秒, 返回开始存放的索引值
                // -1表示无限等待
                // 0表示不等待
                int inputIndex = mediaCodec.dequeueInputBuffer(-1);
                if (inputIndex >= 0) {
                    ByteBuffer inputBuffer = inputBuffers[inputIndex];
                    inputBuffer.clear();

                    // 读取数据到inputBuffer中
                    inputBuffer.put(buf, 0, buf.length);

                    // 通知MediaDecode解码刚刚传入的数据
                    // 在输入数据的末尾，对象会通过queueInputBuffer(int, int, int, long, int)函数
                    // 发送一个带有BUFFER_FLAG_END_OF_STREAM 下标的信号。
                    mediaCodec.queueInputBuffer(inputIndex, 0, buf.length, System.currentTimeMillis(), 0);
                }

                // if inputBuffer is null ,this function will be blocking
                // 时间单位为微秒
                // -1表示无限等待
                // 0表示不等待
                int outIndex = mediaCodec.dequeueOutputBuffer(info, 10000);
                LogUtils.e(TAG, "取出 = " + outIndex);
                while (outIndex >= 0) {

                    byte[] array = new byte[info.size];
                    outputBuffers[outIndex].get(array);

                    LogUtils.e(TAG, "播放了");
                    if (audioPlayer != null && array != null) {
                        audioPlayer.writeData(array);
                    }

                    // 此操作一定要做，不然MediaCodec用完所有的Buffer后 将不能向外输出数据
                    mediaCodec.releaseOutputBuffer(outIndex, false);
                    // 数据取出后一定记得清空此Buffer MediaCodec是循环使用这些Buffer的，不清空下次会得到同样的数据
                    outputBuffers[outIndex].clear();

                    /*
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {

                    }
                    */

                    outIndex = mediaCodec.dequeueOutputBuffer(info, 10000);
                    if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        break;
                    }

                    /*
                    else if (outIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                        outputBuffers = mediaCodec.getOutputBuffers();
                    }
                    else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        mediaFormat = mediaCodec.getOutputFormat();
                    }
                    */

                }

            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

            /*
            while (outIndex >= 0) {
                //这个应该是编解码后的数据吧
                ByteBuffer outputBuffer = outputBuffers[outIndex];
                byte[] outData = new byte[info.size];
                outputBuffer.get(outData);
                if(audioPlayer!=null){
                    audioPlayer.playAudio(outData);
                }
                //outputStream.write(outData, 0, outData.length);
                mediaCodec.releaseOutputBuffer(outIndex, true);
                outIndex = mediaCodec.dequeueOutputBuffer(info, 0);
            }
            */

        }
    }

    /**
     * 停止解码
     */
    public void stopAudio() {
        if (mediaCodec != null) {
            mediaCodec.stop();
            mediaCodec.release();
        }

        if(audioPlayer!=null){
            audioPlayer.release();
        }
    }
}
