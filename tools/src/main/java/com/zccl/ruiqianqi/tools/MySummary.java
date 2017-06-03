package com.zccl.ruiqianqi.tools;

import android.media.MediaPlayer;
import android.widget.VideoView;

/**
 * Created by zc on 2015/11/10.
 */
public class MySummary {
    /**
     * 点9图片要全部显示到背景上，如果点9图片的色边与图片边距离太大，那控件就要缩小了，因为控件会在中间无像素的区域绘制
     * 所以用了点9的话，理论上来说，控制都会缩小一点，一般的就2个像素，边框一个像素，控制线一个像素
     */
    private void pointNine(){

    }

    /**
     * 百分比布局的话，TextView Button EditText要想文字正常显示，最好android:padding="1dp"
     * 找到问题了，他妈的是padding的原因，好像有默认的padding值，要用0或1给它覆盖掉，然后android:gravity="center"
     * 文字居中显示
     */
    private void textView(){

    }

    /**
     *  在这儿使用点9，比在下面使用点正常一些，比进度的范围大一些
     *  android:background="@drawable/loading_jindubeijing2"
     *
     *  把背景点9放这儿的话，会和进度一样高的
     *  android:progressDrawable="@drawable/loading_progressbar_style"
     *
     *  还有问题，进度用颜色，背景用点9，无法正确匹配
     */
    private void progressBar(){

    }

    /**
     * armeabi默认选项，
     支持基于 ARM* v5TE 的设备
     支持软浮点运算（不支持硬件辅助的浮点计算）
     支持所有 ARM* 设备

     armeabi-v7a
     支持基于 ARM* v7 的设备
     支持硬件 FPU 指令
     支持硬件浮点运算

     不同手机由于cpu的不同，使用不同的驱动。
     ABI:指应用基于哪种指令集来进行编译，ABI总共有七种，
     分别是
     armeabi、armeabi-v7a、arm64-v8a、
     mips、mips64、
     x86、x86_64，
     它们都是表示cpu的类型。
     */
    private void armeabi(){

    }

    /**
     * 1. tcp 收发缓冲区默认值
     [root@qljt core]# cat /proc/sys/net/ipv4/tcp_rmem
     4096    87380   4161536
     87380  ：tcp接收缓冲区的默认值

     [root@qljt core]# cat /proc/sys/net/ipv4/tcp_wmem
     4096    16384   4161536
     16384  ： tcp 发送缓冲区的默认值

     2. tcp 或udp收发缓冲区最大值
     [root@qljt core]# cat /proc/sys/net/core/rmem_max
     131071
     131071：tcp 或 udp 接收缓冲区最大可设置值的一半。
     也就是说调用 setsockopt(s, SOL_SOCKET, SO_RCVBUF, &rcv_size, &optlen);  时rcv_size 如果超过 131071，那么
     getsockopt(s, SOL_SOCKET, SO_RCVBUF, &rcv_size, &optlen); 去到的值就等于 131071 * 2 = 262142

     [root@qljt core]# cat /proc/sys/net/core/wmem_max
     131071
     131071：tcp 或 udp 发送缓冲区最大可设置值得一半。
     跟上面同一个道理

     3. udp收发缓冲区默认值
     [root@qljt core]# cat /proc/sys/net/core/rmem_default
     111616：udp接收缓冲区的默认值
     [root@qljt core]# cat /proc/sys/net/core/wmem_default
     111616
     111616：udp发送缓冲区的默认值

     4. tcp 或udp收发缓冲区最小值
     tcp 或udp接收缓冲区的最小值为 256 bytes，由内核的宏决定；
     tcp 或udp发送缓冲区的最小值为 2048 bytes，由内核的宏决定
     */
    private void tcpudp(){

    }

    /**
     * android自带多媒体
     */
    private void media(){
        MediaPlayer mediaPlayer = null;
        VideoView videoView = null;
    }

    /**
     * Glide [google]
     * Picasso [square]+[OkHttp]
     * Universal-Image-Loader
     * android-Volley [google]
     * Fresco [facebook]+[OkHttp]
     * 五大Android开源组件加载网络图片的优缺点比较
     */
    private void imageLoad(){

    }
}
