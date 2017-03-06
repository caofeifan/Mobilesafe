package com.cff.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.cff.mobilesafe.dao.AddressDao;

/**
 * 监听来电归属地
 * Created by caofeifan on 2017/3/6.
 */

public class AddressService extends Service {
    TelephonyManager tm;
    SharedPreferences mPref;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new MyListener(),PhoneStateListener.LISTEN_CALL_STATE);//监听来电
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(new MyListener(),PhoneStateListener.LISTEN_NONE);//停止监听
        mPref.edit().putBoolean("show_address",false).commit();
        Toast.makeText(AddressService.this,"服务关闭",Toast.LENGTH_SHORT).show();
    }

    class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state){
                //监听来电
                case TelephonyManager.CALL_STATE_RINGING:
                    AddressDao addressDao = new AddressDao(AddressService.this);
                    String address = addressDao.getAddress(incomingNumber);
                    Toast.makeText(AddressService.this,address,Toast.LENGTH_LONG).show();
                    break;
                /*case TelephonyManager.CALL_STATE_RINGING:break;
                case TelephonyManager.CALL_STATE_RINGING:break;*/
                default:break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }
}
