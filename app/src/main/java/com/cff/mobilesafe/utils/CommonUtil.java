package com.cff.mobilesafe.utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 在此写用途
 * Created by caofeifan on 2017/2/24.
 */

public class CommonUtil {
    private static final String TAG = CommonUtil.class.getSimpleName();
    /**
     * MD5加密
     * @param pwd
     * @return
     * @throws NoSuchAlgorithmException
     */

    public static String encodeByMd5(String pwd){
        Log.i(TAG, "encodeByMd5: 加密前"+pwd);
        //确定计算方法
        MessageDigest instance = null;
        try {
            instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(pwd.getBytes());//对字符串加密，返回字节数组

            StringBuffer sb = new StringBuffer();
            for (byte b:digest) {
                int i = b & 0xff;
                String hexString  = Integer.toHexString(i);
                if (hexString.length()<2){
                    hexString = "0" +hexString;
                }
                sb.append(hexString);
            }
            Log.i(TAG, "encodeByMd5: 加密后"+sb);
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //无法加密
        return null;

    }
}
