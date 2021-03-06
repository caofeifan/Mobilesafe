package com.cff.mobilesafe.utils;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 80755 on 2017/2/24.
 */

public class JsonUtil {
    private static final String TAG = JsonUtil.class.getSimpleName();
    private static Gson gson = null;
    static {
        if (gson == null){
            gson = new Gson();
        }
    }

    /**
     * 对象转换成JSON字符串
     *
     * @param obj
     *            需要转换的对象
     * @return 对象的string字符
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * JSON字符串转换成对象
     *
     * @param jsonString
     *            需要转换的字符串
     * @param type
     *            需要转换的对象类型
     * @return 对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T fromJson(String jsonString, Class<T> type) {
        Log.i(TAG, "fromJson: "+jsonString);
        return gson.fromJson(jsonString,type);
    }


    /**
     * 将JSONArray对象转换成list集合
     *
     * @param jsonArr
     * @return
     */
    /**
     * @param json
     * @param clazz
     * @return
     */
    public static <T> ArrayList<T> jsonToArrayList(String json, Class<T> clazz)
    {
        Type type = new TypeToken<ArrayList<JsonObject>>() {}.getType();
        ArrayList<JsonObject> jsonObjects = new Gson().fromJson(json, type);

        ArrayList<T> arrayList = new ArrayList<>();
        for (JsonObject jsonObject : jsonObjects)
        {
            arrayList.add(new Gson().fromJson(jsonObject, clazz));
        }
        return arrayList;
    }

    /**
     * 将json字符串转换成map对象
     *
     * @param json
     * @return
     */


    /**
     * 将JSONObject转换成map对象
     *
     * @param json
     * @return
     */

}
