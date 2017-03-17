package com.cff.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.dao.AddressDao;

/**
 * 监听来电归属地
 * Created by caofeifan on 2017/3/6.
 */

public class AddressService extends Service {
    TelephonyManager tm;
    SharedPreferences mPref;
    OutCallReceiver receiver;
    WindowManager wm;
    View view;

    int winWidth;
    int winHeight;
    private int startX;
    private int startY;


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

        //注册Receiver
        receiver = new OutCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver,intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(new MyListener(),PhoneStateListener.LISTEN_NONE);//停止监听
        mPref.edit().putBoolean("show_address",false).commit();
        Toast.makeText(AddressService.this,"服务关闭",Toast.LENGTH_SHORT).show();
        //解注册
        unregisterReceiver(receiver);
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
                    showMyToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    hideMyToast();
                    break;
                default:break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    public class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();//获取去电号码
            AddressDao addressDao = new AddressDao(context);
            String address = addressDao.getAddress(number);
            showMyToast(address);
        }
    }

    /**
     * 自定义
     */
    public void showMyToast(String text){
        int lastX = mPref.getInt("startX",0);
        int lastY = mPref.getInt("startY",0);
        int style = mPref.getInt("currentStyle",0);
        int[] items = new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,R.drawable.call_locate_green};
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        winWidth = wm.getDefaultDisplay().getWidth();
        winHeight = wm.getDefaultDisplay().getHeight();
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;//添加权限SYSTEM_ALERT_WINDOW
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        params.x = lastX;
        params.y = lastY;
        params.gravity = Gravity.LEFT+Gravity.TOP;//设置位置为左上方


        view = View.inflate(this,R.layout.mytoast,null);
        view.setBackgroundResource(items[style]);
        TextView tvMyToast = (TextView) view.findViewById(R.id.tvMyToast);
        tvMyToast.setText(text);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
                        wm.updateViewLayout(view,params);

                        //更新副创
                        params.x += dx;
                        params.y += dy;

                        //防止坐标偏移
                        if (params.x<0) params.x = 0;
                        if (params.y<0) params.y = 0;
                        if (params.x>winWidth-view.getWidth()){
                            params.x = winWidth-view.getWidth();
                        }
                        if (params.y>winHeight-view.getHeight()){
                            params.y = winHeight-view.getHeight();
                        }
                        //重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        mPref.edit().putInt("startX",params.x).commit();
                        mPref.edit().putInt("startY",params.y).commit();
                        break;
                    default:break;
                }
                return false;
            }
        });
        wm.addView(view,params);
    }
    public void hideMyToast(){
        if (wm != null && view != null){
            wm.removeView(view);
        }
    }
}
