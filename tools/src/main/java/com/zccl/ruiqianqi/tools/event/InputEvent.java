package com.zccl.ruiqianqi.tools.event;

import com.zccl.ruiqianqi.tools.config.MyConfigure;
import com.zccl.ruiqianqi.tools.FileUtils;
import com.zccl.ruiqianqi.tools.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ruiqianqi on 2016/12/6 0006.
 *
 */
public class InputEvent {

    public static class EventInfo{
        // 屏幕输入设备
        public String touchInput;
        // A、B、AB
        public String protocol;
    }

    /**
     * 得到手机屏幕输入设备
     * @return
     */
    public static EventInfo getEventInfo(){
        InputStream is = FileUtils.getFileStream(null, "/proc/bus/input/devices", MyConfigure.SIX_ABSOLUTE);
        if(is != null){
            String temp;
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            EventInfo eventInfo = new EventInfo();
            eventInfo.touchInput = "";
            eventInfo.protocol = "";
            try {
                while((temp = br.readLine())!=null){
                    String[] strS = temp.split(": ");
                    if(!(strS.length > 1 && !StringUtils.isEmpty(strS[1]))){
                        continue;
                    }

                    if(strS[0].equalsIgnoreCase("i")){

                    }else if(strS[0].equalsIgnoreCase("n")){

                    }else if(strS[0].equalsIgnoreCase("h")){
                        String[] aa = strS[1].split(" ");
                        for (int i = 0; i < aa.length; i++) {
                            if(aa[i].trim().startsWith("event")){
                                eventInfo.touchInput = "/dev/input/"+aa[i];
                                break;
                            }
                        }

                    }else if(strS[0].equalsIgnoreCase("b")){
                        String [] eventS = strS[1].split("=");
                        if(eventS[0].equalsIgnoreCase("key")){
                            String [] keyEventS = eventS[1].split(" ");

                            // 找A协议 0x14a [0x14]在第10个int中表示【从右往左，从0开始】，a表示第a位置1，结果就是0400
                            if(keyEventS.length >= 11){
                                // 其中s的长度也不能超出7，否则也会抛异常。
                                String var = keyEventS[keyEventS.length-11];
                                if(var.length() > 8){
                                    var = var.substring(var.length() - 8);
                                }

                                // 参数中radix的范围是在2~36之间，超出范围会抛异常。
                                int code = Integer.parseInt(var, 16);
                                if((code & 0x400)==0x400){
                                    eventInfo.protocol += "A";
                                }
                            }
                        }else if(eventS[0].equalsIgnoreCase("abs")){
                            String [] absEventS = eventS[1].split(" ");

                            if(absEventS.length==2){
                                String var = absEventS[0];
                                if(var.length() > 8){
                                    var = var.substring(var.length()-8);
                                }
                                int code = Integer.parseInt(var, 16);
                                // 多点触摸协议
                                if((code & 0x600000)==0x600000){
                                    // B协议
                                    if((code & 0x8000)==0x8000){
                                        eventInfo.protocol += "B";
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return eventInfo;
        }
        return null;
    }

}
