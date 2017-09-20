package com.cff.mobilesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * 生命周期只有10s
 */
import com.cff.mobilesafe.service.KillProcessWidgetService;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/26.
 */

public class MyAppWidget extends AppWidgetProvider {

    private Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        intent = new Intent(context, KillProcessWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        intent = new Intent(context, KillProcessWidgetService.class);
        context.stopService(intent);
    }
}
