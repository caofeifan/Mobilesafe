package com.cff.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.cff.mobilesafe.activity.BaseActivity;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * 监听手机重启的广播
 * Created by caofeifan on 2017/2/26.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences mPref = context.getSharedPreferences("config",Context.MODE_PRIVATE);
        String sim = mPref.getString("sim",null);
        if (!TextUtils.isEmpty(sim)){
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String currentSim = tm.getSimSerialNumber()+"123";
            if (sim.equals(currentSim)){
                Log.i(TAG, "onReceive: 手机安全");
            }else {
                Log.i(TAG, "onReceive: 手机危险");
                String phone = mPref.getString("safePhone","");
                //发送短信给安全号码
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone,null,"sim card changed!",null,null);
            }
        }
    }
}
