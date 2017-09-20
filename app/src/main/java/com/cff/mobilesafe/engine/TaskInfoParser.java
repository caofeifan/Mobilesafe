package com.cff.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;
import android.util.Log;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/24.
 */

public class TaskInfoParser {
    private static final String TAG = TaskInfoParser.class.getSimpleName();
    public static List<TaskInfo> getTaskInfos(Context context){
        List<TaskInfo> taskInfos = new ArrayList<>();
        /**
         * 获取packageManager
         */
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : appProcesses){
            TaskInfo taskInfo = new TaskInfo();
            /**
             * 获取到进程的名字
             */
            String processName = runningAppProcessInfo.processName;

            try {
                /***
                 * 获取一个进程占用的内存数
                 */
                Debug.MemoryInfo[] processMemoryInfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
                int dirty = processMemoryInfo[0].getTotalPrivateDirty();//KB

                PackageInfo packageInfo = packageManager.getPackageInfo(processName, 0);

                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                /**
                 * 是否是系统进程
                 */
                int flag = packageInfo.applicationInfo.flags;
                if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0){
                    taskInfo.setUserApp(false);
                }else {
                    taskInfo.setUserApp(true);
                }
                taskInfo.setAppName(appName);
                taskInfo.setMemeorySize((Formatter.formatFileSize(context,dirty*1024)));
                taskInfo.setIcon(icon);
                taskInfo.setPackageName(processName);
                taskInfos.add(taskInfo);


            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                taskInfo.setAppName("XXX");
            }

        }
        return taskInfos;
    }
    /**
     * 清理进程
     */
    public static void killProcess(Context context,String packageName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(packageName);

    }

}
