package com.cff.mobilesafe.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    /**
     * 获取文件的MD5值
     * @param sourceDir
     * @return
     */
    public static String getFileMD5(String sourceDir) {
        File file = new File(sourceDir);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            //数字摘要
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            while ((len = fis.read(buffer)) != -1){
                messageDigest.update(buffer,0,len);
            }
            StringBuffer sb = new StringBuffer();
            //对字符串加密，返回字节数组
            byte[] result = messageDigest.digest();
            for (byte b : result){
                int number = b & 0xff;
                String hex = Integer.toHexString(number);
                if (hex.length() ==1){
                    sb.append("0"+hex);
                }else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
