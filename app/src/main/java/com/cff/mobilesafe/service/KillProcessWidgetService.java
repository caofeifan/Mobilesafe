package com.cff.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.receiver.MyAppWidget;
import com.cff.mobilesafe.utils.SystemInfoUitl;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 清理进程的服务
 * Created by caofeifan on 2017/3/26.
 */

public class KillProcessWidgetService extends Service{

    private AppWidgetManager widgetManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         *
         */
        widgetManager = AppWidgetManager.getInstance(this);
        /**
         * 每隔5S更新一次桌面
         */
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
               /**
                 * 上下文，处理该控件的provider
                 */
                ComponentName componentName = new ComponentName(getApplicationContext(), MyAppWidget.class);
                /**
                 * 添加一个远程View
                 */
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
                /**
                 * 更新remoteview内容
                 */
                int processCount = SystemInfoUitl.getProcessCount(getApplicationContext());
                long availMem = SystemInfoUitl.getAvailMem(getApplicationContext());
                remoteViews.setTextViewText(R.id.process_count,"进程数："+processCount+"个");
                remoteViews.setTextViewText(R.id.process_memory,"可用内存："+ Formatter.formatFileSize(getApplicationContext(),availMem));
                /**
                 * 设置remoteView的点击事件
                 */
                Intent intent = new Intent();
                /**
                 * 隐示意图
                 */
                intent.setAction("com.cff.mobilesafe.killProcessAll");
                /**
                 * 延迟意图
                 */
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                remoteViews.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);

                widgetManager.updateAppWidget(componentName,remoteViews);
                System.out.println("小窗口更新了！！！");

            }
        };
        /**
         * 定时任务
         * 延迟执行
         * 每隔5 s 执行一次
         */
        timer.schedule(timerTask,0,5000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
