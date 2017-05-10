package com.zccl.ruiqianqi.socket.disruptor;

/**
 * Created by zc on 2015/12/30.
 */
public class ByteBufEvent {

    private EventMsg eventMsg;

    public EventMsg getEventMsg() {
        return eventMsg;
    }

    public void setEventMsg(EventMsg eventMsg) {
        this.eventMsg = eventMsg;
    }

    /**
     * 传递的消息体
     */
    public static class EventMsg{
        private int type;
        private byte[] header;
        private byte[] body;

        public void setType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public byte[] getHeader() {
            return header;
        }

        public void setHeader(byte[] header) {
            this.header = header;
        }

        public byte[] getBody() {
            return body;
        }

        public void setBody(byte[] body) {
            this.body = body;
        }
    }
}
