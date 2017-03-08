package com.cff.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cff.mobilesafe.dao.AddressDao;

/**
 * 在此写用途
 * 需要权限PROCESS_OUTGOING_CALLS
 * Created by caofeifan on 2017/3/7.
 */

public class OutCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String number = getResultData();//获取去电号码
        AddressDao addressDao = new AddressDao(context);
        String address = addressDao.getAddress(number);
        Toast.makeText(context,address,Toast.LENGTH_LONG).show();
    }
}
