package com.cff.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.cff.mobilesafe.domain.AppInfo;

import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/24.
 */

public class SystemInfoUitl {

    public static int getProcessCount(Context context){
        /**
         * 任务管理器（活动管理器）
         */
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        /**
         * 获取手机上运行的进程数
         */
        int size = runningAppProcesses.size();
        return size;
    }

    public static long getAvailMem(Context context){
        /**
         * 任务管理器（活动管理器）
         */
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        /**
         * 内存信息
         */
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        /**
         * 可用内存，剩余内存
         */
        long availMem = memoryInfo.availMem;
        return availMem;
    }

    public static long getTotalMem(Context context){
        /**
         * 任务管理器（活动管理器）
         */
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        /**
         * 内存信息
         */
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        /**
         * 总内存
         */
        long totalMem = memoryInfo.totalMem;
        return totalMem;
    }

    public static boolean isServiceRunning(Context context, String serviceName){
        /**
         * 任务管理器（活动管理器）
         */
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(20);
        for (ActivityManager.RunningServiceInfo runningService : runningServices){
            if (TextUtils.equals(runningService.service.getClassName(), serviceName)){
                return true;
            }
        }
        return false;
    }

}
