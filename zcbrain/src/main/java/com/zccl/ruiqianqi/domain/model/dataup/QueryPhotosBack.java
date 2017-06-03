package com.zccl.ruiqianqi.domain.model.dataup;

import com.zccl.ruiqianqi.config.RemoteProtocol;

import static com.zccl.ruiqianqi.config.RemoteProtocol.A_ORDER_CALLBACK;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_NAMES_RESULT;
import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_QUERY_RESULT;

/**
 * Created by ruiqianqi on 2017/3/22 0022.
 */

public class QueryPhotosBack {
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

    public static class PhotoNamesResult{
        /**
         * 获取所有图片名称
         * {@link RemoteProtocol#B_PHOTO_NAMES_RESULT}
         */
        private String cmd = B_PHOTO_NAMES_RESULT;
        /**
         * [{"name":""},{"name":""}......]
         */
        private String names;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getNames() {
            return names;
        }

        public void setNames(String names) {
            this.names = names;
        }
    }

    public static class PhotoDataResult{
        /**
         * 获取图片数据
         * {@link RemoteProtocol#B_PHOTO_QUERY_RESULT}
         */
        private String cmd = B_PHOTO_QUERY_RESULT;
        private String name;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
