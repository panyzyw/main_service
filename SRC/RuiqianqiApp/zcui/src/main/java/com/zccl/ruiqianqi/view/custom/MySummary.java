package com.zccl.ruiqianqi.view.custom;

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
     * 查看CPU类型
     * cat /proc/cpuinfo
     *
     * armeabi默认选项，
     支持基于 ARM* v5TE 的设备
     支持软浮点运算（不支持硬件辅助的浮点计算）
     支持所有 ARM* 设备

     armeabi-v7a
     支持基于 ARM* v7 的设备
     支持硬件 FPU 指令
     支持硬件浮点运算

     不同手机由于cpu的不同，使用不同的驱动。
     ABI:指应用基于哪种指令集来进行编译，它们都是表示cpu的类型。
     armeabi armeabi-v7a arm64-v8a x86 x86_64 mips mips64
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
     *  HashMap几乎可以等价于Hashtable，除了HashMap是非synchronized的，并可以接受null
     *  (HashMap allows one null key and any number of null values.，而Hashtable则不行)。
     *  这就是说，HashMap中如果在表中没有发现搜索键，或者如果发现了搜索键，但它是一个空的值，那么get()将返回null。
     *  如果有必要，用containKey()方法来区别这两种情况。
     *
     *  当该类的两个对象的 hashCode() 返回值相同时，它们通过equals()方法比较也应该返回 true。
     *  通常来说，所有参与计算 hashCode() 返回值的关键属性，都应该用于作为 equals() 比较的标准。
     */
    private void hash(){

    }

    /**
     * Integer ss = 0;
     * ss++;
     * 这个操作之后，对象就改变了，所以不能当作锁
     */
    private void lock(){

    }

    /**
     应该说Memcached和Redis都能很好的满足解决我们的问题，它们性能都很高，总的来说，可以把Redis理解为是对Memcached的拓展，是更加重量级的实现，提供了更多更强大的功能。具体来说：

     1.性能上：
     性能上都很出色，具体到细节，由于Redis只使用单核，而Memcached可以使用多核，所以平均每一个核上Redis在存储小数据时比
     Memcached性能更高。而在100k以上的数据中，Memcached性能要高于Redis，虽然Redis最近也在存储大数据的性能上进行优化，但是比起 Memcached，还是稍有逊色。

     2.内存空间和数据量大小：
     MemCached可以修改最大内存，采用LRU算法。Redis增加了VM的特性，突破了物理内存的限制。

     3.操作便利上：
     MemCached数据结构单一，仅用来缓存数据，而Redis支持更加丰富的数据类型，也可以在服务器端直接对数据进行丰富的操作,这样可以减少网络IO次数和数据体积。

     4.可靠性上：
     MemCached不支持数据持久化，断电或重启后数据消失，但其稳定性是有保证的。Redis支持数据持久化和数据恢复，允许单点故障，但是同时也会付出性能的代价。

     5.应用场景：
     Memcached：动态系统中减轻数据库负载，提升性能；做缓存，适合多读少写，大数据量的情况（如人人网大量查询用户信息、好友信息、文章信息等）。
     Redis：适用于对读写效率要求都很高，数据处理业务复杂和对安全性要求较高的系统（如新浪微博的计数和微博发布部分系统，对数据安全性、读写要求都很高）。

     六、需要慎重考虑的部分
     1.Memcached单个key-value大小有限，一个value最大只支持1MB，而Redis最大支持512MB
     2.Memcached只是个内存缓存，对可靠性无要求；而Redis更倾向于内存数据库，因此对对可靠性方面要求比较高
     3.从本质上讲，Memcached只是一个单一key-value内存Cache；而Redis则是一个数据结构内存数据库，支持五种数据类型，因此Redis除单纯缓存作用外，还可以处理一些简单的逻辑运算，Redis不仅可以缓存，而且还可以作为数据库用
     4.新版本（3.0）的Redis是指集群分布式，也就是说集群本身均衡客户端请求，各个节点可以交流，可拓展行、可维护性更强大。

     */
    private void redis(){

    }

    /**
     * recyclerview的item宽高，设置问题
     * 宽好像就是ItemDecoration画完了之后，剩下的平分
     * 高好像要在item布局中明确设置了,就是根据item的根布局来的
     */
    private void recyclerViewItem(){

    }

    /**
     * EditText为什么在touch mode下也需要焦点
     * 比如Button之类的控件,在touch mode下,就已经没有获取焦点的必要了.
     *
     * android:focusableInTouchMode="true"
     * android:focusable="true"
     *
     * 这样包含有EditText的布局响应就只需一次
     * android:focusableInTouchMode="false"
     * android:focusable="false"
     */
    private void focus(){

    }

    /**
     * 在4.0以上版本，google禁掉了android.permission.WRITE_APN_SETTINGS
     */
    private void permission(){

    }

}
