package com.zccl.ruiqianqi.domain.model.datadown;

import com.zccl.ruiqianqi.config.RemoteProtocol;

import java.util.ArrayList;
import java.util.Map;

import static com.zccl.ruiqianqi.config.RemoteProtocol.B_PHOTO_QUERY;

/**
 * Created by ruiqianqi on 2017/3/22 0022.
 */

public class QueryPhoto extends BasePush {

    public static class BaseQueryPhoto{

        protected String cmd = B_PHOTO_QUERY;
        /**
         * {@link RemoteProtocol#TYPE_PHOTO_QUERY_LIST}
         * {@link RemoteProtocol#TYPE_PHOTO_THUMBNAIL}
         * {@link RemoteProtocol#TYPE_PHOTO_ORIGINAL}
         * {@link RemoteProtocol#TYPE_PHOTO_DELETE}
         */
        protected String type;

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class QueryPhotoNames extends BaseQueryPhoto{
        // 查询所有图片名字
    }

    public static class QueryPhotoData extends BaseQueryPhoto{

        // 要获取图片信息的图片名字
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class QueryPhotoDelete extends BaseQueryPhoto{
        /**
         * 要删除的图片的名字集合
         * [{"name":""},{"name":""}......]
         */
        private ArrayList<Map<String, String>> names;

        public ArrayList<Map<String, String>> getNames() {
            return names;
        }

        public void setNames(ArrayList<Map<String, String>> names) {
            this.names = names;
        }
    }
}
