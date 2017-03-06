package com.cff.mobilesafe.utils;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.Context;

import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/6.
 */

public class ServiceStateUtil {
    /**
     * 服务是否正在运行
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list  = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo:list) {
            String className = runningServiceInfo.service.getClassName();//获取服务的名称
            System.out.println("---------"+className);
            if (className.equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
