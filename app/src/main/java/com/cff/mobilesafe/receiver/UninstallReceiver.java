package com.cff.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/16.
 */

public class UninstallReceiver extends BroadcastReceiver{
    private static final String TAG = UninstallReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: 接收到卸载广播");
    }
}
