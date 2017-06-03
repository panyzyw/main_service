package com.zccl.ruiqianqi.socket.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * Created by zc on 2015/12/30.
 */
public class ByteBufHandler implements EventHandler<ByteBufEvent> {

    private static String TAG = ByteBufHandler.class.getSimpleName();

    private ByteBufEventListener byteBufEventListener;
    public ByteBufHandler(){

    }

    @Override
    public void onEvent(ByteBufEvent byteBufEvent, long l, boolean b) throws Exception {

        // 对内存池的数据进行处理
        if(null != byteBufEventListener){
            byteBufEventListener.decodeBytes(byteBufEvent);
        }

        // 释放内存池的内存
        ByteBufEvent.EventMsg eventMsg = byteBufEvent.getEventMsg();
        if(null != eventMsg) {
            eventMsg.setHeader(null);
            eventMsg.setBody(null);
        }
    }

    public void setEventHandlerListener(ByteBufEventListener byteBufEventListener){
        this.byteBufEventListener = byteBufEventListener;
    }

    public interface ByteBufEventListener{
        void decodeBytes(ByteBufEvent byteBufEvent);
    }
}
