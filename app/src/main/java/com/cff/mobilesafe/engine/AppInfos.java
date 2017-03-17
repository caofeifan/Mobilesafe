package com.cff.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.Log;

import com.cff.mobilesafe.domain.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本信息
 * 业务逻辑
 * Created by caofeifan on 2017/3/15.
 */

public class AppInfos {
    private static final String TAG = AppInfos.class.getSimpleName();

    /**
     * 耗时操作
     * @param context
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context){
        ArrayList<AppInfo> appInfos = new ArrayList<>();
        //获取packageManager
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo:installedPackages){
            AppInfo appInfo = new AppInfo();
            //获取app信息
            Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
            String apkName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packageInfo.packageName;

            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            long apkSize = file.length();
            //判断系统APp or 用户APP
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                //系统app
                appInfo.setUserApp(false);
            }else {
                //用户app
                appInfo.setUserApp(true);
            }

            //判断系统APp or 用户APP
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                //系统app
                appInfo.setRom(true);
            }else {
                //用户app
                appInfo.setRom(false);
            }
            int width = icon.getIntrinsicWidth();
            int height = icon.getIntrinsicHeight();
            Log.i(TAG, "getAppInfos: width:==="+width+"height:==="+height);
            appInfo.setIcon(icon);
            appInfo.setApkName(apkName);
            appInfo.setApkPackageName(packageName);
            appInfo.setApkSize(apkSize);
            appInfos.add(appInfo);
            Log.i(TAG, "getAppInfos: APP name"+ apkName);
            Log.i(TAG, "getAppInfos: APP packageName"+ packageName);
            Log.i(TAG, "getAppInfos: APP apkSize"+ apkSize);
        }
        return appInfos;
    }
}
