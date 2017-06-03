package com.zccl.ruiqianqi.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zc on 2016/1/30.
 */
public class JsonUtils {

    /**
     * 直接JSON转类，好像jsonString只要不为空，就有对象返回
     * 看来还得判断转换类的其他字段
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T parseJson(String jsonString, Class<T> cls) {
        if(StringUtils.isEmpty(jsonString)){
            return null;
        }
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * Json转List
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> jsonToList(String jsonString, Class<T> cls) {
        if(StringUtils.isEmpty(jsonString)){
            return null;
        }
        // 这是一个组合类型
        Type type = new TypeToken<ArrayList<JsonObject>>(){}.getType();
        ArrayList<JsonObject> jsonObjS = new Gson().fromJson(jsonString, type);

        ArrayList<T> listOfT = new ArrayList<>();
        for (JsonObject jsonObj : jsonObjS) {
            listOfT.add(new Gson().fromJson(jsonObj, cls));
        }

        return listOfT;
    }

    /**
     * Json 转 List
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> jsonToList2(String jsonString, Class<T> cls) {
        if(StringUtils.isEmpty(jsonString)){
            return null;
        }
        ArrayList<T> list = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();
        JsonArray rootArray = jsonParser.parse(jsonString).getAsJsonArray();
        for (JsonElement json : rootArray) {
            try {
                list.add(gson.fromJson(json, cls));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * Json 转 ListMap
     * @param jsonString
     * @return
     */
    public static <T> ArrayList<Map<String, T>> jsonToListMap(String jsonString, Class<T> cls) {
        if(StringUtils.isEmpty(jsonString)){
            return null;
        }
        ArrayList<Map<String, T>> list = new ArrayList<>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString,
                    new TypeToken<List<Map<String, T>>>() {
                    }.getType());
        } catch (Exception e) {

        }
        return list;
    }
}
