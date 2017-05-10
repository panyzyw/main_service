package com.zccl.ruiqianqi.socket.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Created by zc on 2015/12/30.
 */
public class ByteBufEventFactory implements EventFactory<ByteBufEvent> {
    @Override
    public ByteBufEvent newInstance() {
        return new ByteBufEvent();
    }
}
