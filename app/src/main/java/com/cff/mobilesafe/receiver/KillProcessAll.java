package com.cff.mobilesafe.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import java.util.List;

/**
 * 清理所有进程
 * Created by caofeifan on 2017/3/27.
 */

public class KillProcessAll extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager = context.getPackageManager();
        //得到所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo :runningAppProcesses){
            /**
             * 不杀掉本进程
             */
            if (runningAppProcessInfo.processName.equals(context.getPackageName())){
                Toast.makeText(context,"清理完毕啦。。。",Toast.LENGTH_SHORT).show();
                continue;
            }
            /**
             * 杀掉其他进程
             */
            activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            Toast.makeText(context,"清理完毕",Toast.LENGTH_SHORT).show();

        }
    }
}
