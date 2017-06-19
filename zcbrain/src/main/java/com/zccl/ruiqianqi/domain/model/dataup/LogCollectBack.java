package com.zccl.ruiqianqi.domain.model.dataup;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_ORDER_CALLBACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_LOG_COLLECT;

/**
 * Created by ruiqianqi on 2017/3/22 0022.
 */

public class LogCollectBack {
    private String cmd = A_ORDER_CALLBACK;
    private String command;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public static class LogCollect{
        private String cmd = B_LOG_COLLECT;
        private String id = "";
        private String text = "";
        private String service = "";
        private String operation = "";
        private String answer = "";
        private String semantic = "";
        private String from = "";

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }

        public String getOperation() {
            return operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getSemantic() {
            return semantic;
        }

        public void setSemantic(String semantic) {
            this.semantic = semantic;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }
    }

}
