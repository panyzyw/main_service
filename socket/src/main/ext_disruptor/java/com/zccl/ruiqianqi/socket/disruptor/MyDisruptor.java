package com.zccl.ruiqianqi.socket.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zc on 2015/12/30.
 * 这是无锁队列
 */
public class MyDisruptor {

    private static String TAG = MyDisruptor.class.getSimpleName();
    //
    private EventFactory<ByteBufEvent> eventFactory = null;
    /** 事件的处理线程 */
    private ExecutorService executor = null;
    /** 事件消费者 */
    private ByteBufHandler eventHandler = null;
    /** RingBuffer 大小，必须是 2 的 N 次方 */
    private int ringBufferSize = 1024 * 1024;
    /** 环形队列 */
    private RingBuffer<ByteBufEvent> ringBuffer = null;
    // 事件发布对象
    private Disruptor<ByteBufEvent> disruptor = null;
    //
    private Translator translator = null;

    /**
     * BlockingWaitStrategy 是最低效的策略，但其对CPU的消耗最小并且在各种不同部署环境中能提供更加一致的性能表现；
     * SleepingWaitStrategy 的性能表现跟 BlockingWaitStrategy 差不多，对 CPU 的消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类似的场景；
     * YieldingWaitStrategy 的性能是最好的，适合用于低延迟的系统。在要求极高性能且事件处理线数小于 CPU 逻辑核心数的场景中，推荐使用此策略；例如，CPU开启超线程的特性。
     */
    public MyDisruptor(){
        eventFactory = new ByteBufEventFactory();
        executor = Executors.newSingleThreadExecutor();
        // 单一生产者: ProducerType.SINGLE
        disruptor = new Disruptor<>(eventFactory, ringBufferSize, Executors.defaultThreadFactory(), ProducerType.SINGLE, new YieldingWaitStrategy());
        eventHandler = new ByteBufHandler();
        translator = new Translator();
    }

    /**
     * 开始，构造完对象后，就要调用这个开始循环队列
     */
    public void start(){
        disruptor.handleEventsWith(eventHandler);
        disruptor.start();
        ringBuffer = disruptor.getRingBuffer();
    }

    /**
     * 设置数据处理的回调接口
     * @param byteBufEventListener
     */
    public void setEventHandlerListener(ByteBufHandler.ByteBufEventListener byteBufEventListener){
        eventHandler.setEventHandlerListener(byteBufEventListener);
    }



    /**
     * 发布
     * @param eventMsg
     */
    public void publishEvent(ByteBufEvent.EventMsg eventMsg){
        // 发布事件；
        long sequence = ringBuffer.next();// 请求下一个事件序号；
        try {
            ByteBufEvent event = ringBuffer.get(sequence);// 获取该序号对应的事件对象；
            event.setEventMsg(eventMsg);
        } finally{
            ringBuffer.publish(sequence);// 发布事件；
        }
    }


    /**
     * 发布
     * @param eventMsg
     */
    public void publishEvent2(ByteBufEvent.EventMsg eventMsg) {
        // 发布事件；
        // 获取要通过事件传递的业务数据；
        ringBuffer.publishEvent(translator, eventMsg);
    }

    /**
     * 关闭
     */
    public void close(){
        // 关闭 disruptor，方法会堵塞，直至所有的事件都得到处理；
        disruptor.shutdown();
        // 关闭 disruptor 使用的线程池；如果需要的话，必须手动关闭， disruptor 在 shutdown 时不会自动关闭；
        executor.shutdown();
    }

    /**
     * 发布事件使用
     */
    static class Translator implements EventTranslatorOneArg<ByteBufEvent, ByteBufEvent.EventMsg> {
        @Override
        public void translateTo(ByteBufEvent event, long sequence, ByteBufEvent.EventMsg eventMsg) {
            event.setEventMsg(eventMsg);
        }
    }
}
