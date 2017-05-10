package com.zccl.ruiqianqi.socket.eventbus;

/**
 * Created by zc on 2015/12/30.
 */
public class SocketBusEvent {

    /**
     * 网络消息在总线中的携带体
     */
    public static class SocketCarrier{
        // 心跳响应
        public static final int RESPONSE_HEART = 0;
        // 消息响应
        public static final int RESPONSE_MSG = 1;
        // 数据响应
        public static final int RESPONSE_DATA = 2;

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

    /**
     * 链路空闲事件
     */
    public static class IdleEvent{
        // 心跳请求
        public static final int REQUEST_HEART = 0;

        private int cmd;

        public int getCmd() {
            return cmd;
        }

        public void setCmd(int cmd) {
            this.cmd = cmd;
        }
    }
}
