package com.zccl.ruiqianqi.tools.algorithm;

/**
 * Created by ruiqianqi on 2016/8/24 0024.
 */
public class Element {
    private String cmd;
    private String value;

    public Element(){
    }

    public Element(String cmd, String value){
        this.cmd = cmd;
        this.value = value;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }else {
            if(obj instanceof Element) {
                Element element = (Element) obj;
                return hashCode() == element.hashCode();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return cmd.hashCode();
    }
}
