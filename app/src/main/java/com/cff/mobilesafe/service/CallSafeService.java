package com.cff.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.renderscript.RenderScript;
import android.support.annotation.Nullable;
import android.telecom.InCallService;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.cff.mobilesafe.dao.BlackNumberDao;
import com.cff.mobilesafe.domain.BlackNumberInfo;
import com.cff.mobilesafe.utils.MyDatabaseHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/14.
 */

public class CallSafeService extends Service {
    private static final String TAG = CallSafeService.class.getSimpleName();

    private InnerReceiver innerReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: -------------------------");

        //给系统服务添加监听，监听来电
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        MyPhoneStateListener listener = new MyPhoneStateListener();
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);

        //注册,拦截短信Receiver
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (innerReceiver != null){
            unregisterReceiver(innerReceiver);
        }

    }

    public class MyPhoneStateListener extends PhoneStateListener{
       /* @see TelephonyManager#CALL_STATE_IDLE
     * @see TelephonyManager#CALL_STATE_RINGING
     * @see TelephonyManager#CALL_STATE_OFFHOOK*/
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    BlackNumberDao blackNumberDao = new BlackNumberDao(CallSafeService.this);
                    BlackNumberInfo blackNumberInfo = blackNumberDao.find(incomingNumber);
                    if (blackNumberInfo.getMode().equals("0") || blackNumberInfo.getMode().equals("2")){
                        Toast.makeText(CallSafeService.this,"电话铃响...",Toast.LENGTH_SHORT).show();
                        //电话拦截
                        endCall();
                        //删除电话记录
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri,true,
                                new MyContentObserver(new Handler(),incomingNumber));
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:break;
                default:break;
            }
        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            //通过类加载器加载类
            Class<?> aClass = getClassLoader().loadClass("android.os.ServiceManager");
            Method method = aClass.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder)method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //查看数据库，
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //短信最多140个字节，超出部分分为多条发送，短信指令很短，for循环只执行一次
            for (Object object : objects) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();//短信来源
                String messageBody = message.getMessageBody();//短信内容
                Log.i(TAG, "onReceive: ---------------------------" );
                BlackNumberDao blackNumberDao = new BlackNumberDao(CallSafeService.this);
                BlackNumberInfo blackNumberInfo = blackNumberDao.find(originatingAddress);
                if (blackNumberInfo != null){
                    abortBroadcast();
                    Toast.makeText(CallSafeService.this,"来自："+blackNumberInfo.getNumber()+"被拦截了",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CallSafeService.this,"android6.0短信拦截失效！！",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    class MyContentObserver extends ContentObserver {
        String inComingNumber;
        public MyContentObserver(Handler handler,String incomingNumber) {
            super(handler);
            this.inComingNumber = incomingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            getContentResolver().unregisterContentObserver(this);
            deleteNumber(inComingNumber);
        }
    }

    /**
     * 删除电话号码
     * @param inComingNumber
     */
    private void deleteNumber(String inComingNumber) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri,"number = ?",new String[]{inComingNumber});
    }
}
