package com.yongyida.robot.voice.utils;

import android.content.Context;
import android.content.SharedPreferences;
//import android.os.SystemProperties;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * 获取机器人ID，错误重试次数为3次，每次有时间间隔
 *
 * @author Bright. Create on 2017/1/14 0014.
 */
@Deprecated
public class RobotIDUtils {
    private static final String TAG = RobotIDUtils.class.getSimpleName();

    /**
     * 多次尝试获取Robot的ID和SID字符串
     *
     * @return ID
     */
    @Deprecated
    public static String getRobotID(Context context) {
        String id = SystemPropertiesGet("gsm.serial", "none").trim();
        // AS、Eclipse报错请用上面代码或加载layoutlib.jar
//        String id = SystemProperties.get("gsm.serial", "none").trim();

        SharedPreferences preferences = context.getSharedPreferences("robot_id", Context.MODE_PRIVATE);
        String savedID = preferences.getString("id", "");
        if (!savedID.isEmpty() && savedID.equals(id)) {
            Log.i(TAG, "getRobotID: savedID = " + savedID);
            return savedID;
        }

        int count = 0;
        final int TRY_COUNT = 3;
        while (id.equals("none") && count < TRY_COUNT) {
            Log.i(TAG, "getRobotID: try getting id. count = " + count);
            try {
                Thread.sleep(1000);
                count++;
                id = SystemPropertiesGet("gsm.serial", "none").trim();
//                id = SystemProperties.get("gsm.serial", "none").trim();
            } catch (Exception e) {
                Log.i(TAG, "getRobotID: Error: " + e.getLocalizedMessage());
            }
        }
        if (id.equals("none")) {
            id = "";
        }
        id = checkAndSaveSN(context, id);
        return id;
    }

    /**
     * 查询已保存的ID，如果ID为空，保存传入参数id；如果ID不为空，对比传入参数id；
     * 对比后，以不为null及不是空字符串的传入参数id为准。
     *
     * @param id 传入参数id
     * @return id为空时，返回已保存的ID，如果ID也是空，返回"".
     */
    private static String checkAndSaveSN(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences("robot_id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String savedID = preferences.getString("id", "");
        Log.i(TAG, "checkAndSaveSN: savedID = " + savedID + ", id = " + id);

        // TODO: 正则表达式 \w+ 包括字母、数字、下划线。RobotID格式为：字母数字-字母数字
        if (id != null && !id.isEmpty() && id.trim().matches("\\w+-\\w+")
                && id.length() >= 20 && id.length() <= 32) {

            if (savedID.isEmpty()) {
                // TODO：无保存数据，保存id
                editor.putString("id", id).apply();

            } else {
                // TODO: id与savedID不一致，需要比对
                if (!id.equals(savedID)) {
                    editor.putString("id", id).apply();
                }
                return id;
            }
        } else if (!savedID.isEmpty()) {
            return savedID;
        }
        return id;
    }

    /**
     * 如果使用系统方法SystemProperties.get报错，可以使用此方法反射调用，略微比系统方法慢。
     */
    public static String SystemPropertiesGet(String key, String defValue) {
        Class<?> clazz;
        try {
            clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getDeclaredMethod("get", String.class);
            String result = (String) method.invoke(clazz.newInstance(), key);
            if (result == null || result.isEmpty()) {
                return defValue;
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }
}
