package com.zccl.ruiqianqi.mind.eventbus;

import com.zccl.ruiqianqi.mind.receiver.system.SystemReceiver;

/**
 * Created by ruiqianqi on 2017/3/24 0024.
 */

public class MainBusEvent {

    /**
     * 测试用的
     */
    public static class OneEvent{
        private int id;
        private String tag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    /**
     * 测试用的
     */
    public static class TwoEvent{
        private int id;
        private String tag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    /**
     * 测试用的
     */
    public static class ThreeEvent{
        private int id;
        private String tag;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    /**********************************************************************************************/
    /**
     * 语言变化事件
     */
    public static class LanguageEvent{
        private String language;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    /**
     * 电池电量变化事件
     */
    public static class BatteryEvent{
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * 网络变化事件
     */
    public static class NetEvent{
        private boolean isConn;
        private String text;

        public boolean isConn() {
            return isConn;
        }

        public void setConn(boolean conn) {
            isConn = conn;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * 来电信息变化事件
     */
    public static class PhoneEvent{
        private String status;
        private String number;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }
    }

    /**
     * 传感器变化事件
     */
    public static class SensorEvent{
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * 状态变化事件
     */
    public static class AppStatusEvent{

        private String action;

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }

    /**
     * 进入监听事件
     */
    public static class ListenEvent{

        // 开始循环监听
        public static final int RECYCLE_LISTEN = 1;
        // 结束当然监听
        public static final int STOP_LISTEN = 2;

        private int type;
        private String text;
        private boolean isUseExpression;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isUseExpression() {
            return isUseExpression;
        }

        public void setUseExpression(boolean useExpression) {
            isUseExpression = useExpression;
        }
    }

    /**
     * HOME事件
     */
    public static class HomeEvent{

        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
