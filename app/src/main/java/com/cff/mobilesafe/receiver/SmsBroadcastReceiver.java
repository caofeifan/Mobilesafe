package com.cff.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.cff.mobilesafe.activity.SplashActivity;
import com.cff.mobilesafe.activity.TransparentActivity;
import com.cff.mobilesafe.service.LocationService;


/**
 * 在此写用途
 * Created by caofeifan on 2017/2/28.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = Telephony.Sms.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        //短信最多140个字节，超出部分分为多条发送，短信指令很短，for循环只执行一次
        for (Object object : objects) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            String originatingAddress = message.getOriginatingAddress();//短信来源
            String messageBody = message.getMessageBody();//短信内容
            Log.i(TAG, "onReceive: " + originatingAddress + ":" + messageBody);

            if ("#*alarm*#".equals(messageBody)) {
                /*MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.start();*/
                abortBroadcast();//中断短信
                Log.i(TAG, "onReceive: ==========================");
            } else if ("#*location*#".equals(messageBody)) {//GSP定位
                context.startService(new Intent(context, LocationService.class));
                SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
                String location = sp.getString("location","");
                Log.i(TAG, "onReceive: "+location);


            }else if ("#*wipedata*#".equals(messageBody)){//远程删除数据
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
                ComponentName mDeviceAdminSample = new ComponentName(context,AdminReceiver.class);
                //清除数据
                if (mDPM.isAdminActive(mDeviceAdminSample)){
                    mDPM.wipeData(0);//删除数据，恢复出厂设置
                    mDPM.resetPassword("123456",0);
                }
            }else if ("#*lockscreen*#".equals(messageBody)){//远程锁屏
                //设备管理器
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
                ComponentName mDeviceAdminSample = new ComponentName(context,AdminReceiver.class);

                //激活设备管理器
                // Launch the activity to have the user enable our admin.
                /*Intent intent1 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "茶几设备管理器");*/
                Intent intent1 = new Intent(context, TransparentActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
                boolean is = mDPM.isAdminActive(mDeviceAdminSample);
                if (mDPM.isAdminActive(mDeviceAdminSample)){
                    mDPM.lockNow();
                    mDPM.resetPassword("123456",0);
                    System.out.println("---------");
                }
                System.out.println("-+++++++++");

                /*//卸载程序
                //取消激活
                mDPM.removeActiveAdmin(mDeviceAdminSample);
                //卸载程序
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.addCategory(Intent.CATEGORY_DEFAULT);
                intent2.setData(Uri.parse("package:"+context.getPackageName()));*/



            }

        }
    }


}
