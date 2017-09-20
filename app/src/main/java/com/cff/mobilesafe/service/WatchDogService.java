package com.cff.mobilesafe.service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.icu.text.UFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.cff.mobilesafe.activity.SetPwdActivity;
import com.cff.mobilesafe.dao.AppLockDao;
import com.cff.mobilesafe.domain.AppLock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/29.
 */

public class WatchDogService extends Service {
    private static final String TAG = WatchDogService.class.getSimpleName();
    private static final int REGISTER_PERMISSION = 0;
    private static final int LAUNCH_LOCK = 1;

    private ActivityManager activityManager;
    private Thread thread;
    private SharedPreferences mPref;
    //临时停止保护的包名
    private String tempStopProtectPackageName ="111";
    private boolean flag;
    private WatchDogReceiver receiver;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case REGISTER_PERMISSION:
                    registerPermission(WatchDogService.this);
                    break;
                case LAUNCH_LOCK:
                    String packageName = (String) msg.obj;
                    System.out.println("在程序锁里面");
                    Intent intent = new Intent(WatchDogService.this,SetPwdActivity.class);
                    /**
                     * 停止保护的对象
                     */
                    intent.putExtra("packageName", packageName);
                    /**
                     * 在Service中启动activity时，要设置此flag。
                     */
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
            }
        }
    };
    private AppLockDao appLockDao;
    private List<String> packageNames;


    /**
     * 申请权限
     * @param context
     */
    private void registerPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Toast.makeText(context, "权限不够\n请打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class AppLockContentObserver extends ContentObserver{
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            /**
             * 获取所有锁定的
             */
            appLockDao = new AppLockDao(WatchDogService.this);
            List<AppLock> appLocks = appLockDao.findAll();
            for (AppLock appLock :appLocks){
                if (appLock.isLock()){
                    packageNames.add(appLock.getPackageName());
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        packageNames = new ArrayList<>();

        mPref = getSharedPreferences("config",MODE_PRIVATE);

        /**
         * 获取所有锁定的
         */
        appLockDao = new AppLockDao(WatchDogService.this);
        List<AppLock> appLocks = appLockDao.findAll();
        for (AppLock appLock :appLocks){
            if (appLock.isLock()){
                packageNames.add(appLock.getPackageName());
            }
        }

        /**
         * 注册内容观察者
         */
        getContentResolver().registerContentObserver(Uri.parse("content://change"),false,new AppLockContentObserver(new Handler()));
        /**
         * 获取到进程管理器
         */
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        /**
         * 获取到当前的任务栈
         * 任务栈最上面的任务
         */
        startWatchdog();
    }

    private void startWatchdog() {
        /**
         * 注册广播接收者
         */
        receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("stopProtect");
        /**
         * 锁屏，解锁屏
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
        /**
         * 避免程序阻塞
         */ /**
          *
          */
         thread = new Thread(){
              @Override
              public void run() {
                  flag = true;
                  while (flag){
                      if (!mPref.getBoolean("show_watchDog", false)){
                          flag = false;
                          break;
                      }
                      /**
                       * 获取前台  packageName
                       */
                      String packageName = queryUsageStats(WatchDogService.this);
                      if (!TextUtils.equals(packageName,null)){
                          if (packageNames.contains(packageName)){
                              if (packageName.equals(tempStopProtectPackageName)){

                              }else {
                                  System.out.println("在程序锁里面");
                                  Intent intent = new Intent(WatchDogService.this,SetPwdActivity.class);
                                  /**
                                   * 停止保护的对象
                                   */
                                  intent.putExtra("packageName", packageName);
                                  /**
                                   * 在Service中启动activity时，要设置此flag。
                                   */
                                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                  startActivity(intent);
                              }

                          }else {
                              System.out.println("不在程序锁里面");
                          }
                      }
                      SystemClock.sleep(30);
                  }
              }
          };
        thread.start();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String queryUsageStats(Context context) {
        class RecentUseComparator implements Comparator<UsageStats> {
            @Override
            public int compare(UsageStats lhs, UsageStats rhs) {
                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
            }
        }
        RecentUseComparator mRecentComp = new RecentUseComparator();
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000 * 10, ts);
        if (usageStats == null || usageStats.size() == 0) {
            if (HavaPermissionForTest(context) == false) {
                /**
                 * 向Handler发送消息
                 */
                Message msg = Message.obtain();
                msg.what = REGISTER_PERMISSION;
                handler.sendMessage(msg);

            }
            return null;
        }
        /**
         * 排序，返回排序好的 usageStats
         */
        Collections.sort(usageStats, mRecentComp);
        String currentTopPackage = usageStats.get(0).getPackageName();
        return currentTopPackage;

        /*if (currentTopPackage.equals(packageName)) {
            return true;
        } else {
            return false;
        }*/
    }
    /**
     * 判断是否有用权限
     *
     * @param context 上下文参数
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean HavaPermissionForTest(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    public class WatchDogReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * 停止保护对象
             */
            if (intent.getAction().equals("stopProtect")){
                System.out.println("-------停止保护--");
                //获取到停止保护的对象
                tempStopProtectPackageName = intent.getStringExtra("packageName");
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                System.out.println("--------锁屏，停止看门狗--");
                tempStopProtectPackageName = null;
                /**
                 * 锁屏，停止看门狗
                 */
                flag = false;
            }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                System.out.println("--------解锁屏幕--");
                /**
                 * 解锁屏幕
                 */
                if (flag == false){
                    startWatchdog();
                }
            }
        }
    }
}
