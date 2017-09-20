package com.cff.mobilesafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.activity.BackgroundActivity;


public class RocketService extends Service {
    private static final String TAG = RocketService.class.getSimpleName();
    private static final int CODE_SEND_ROCKET = 010;
    private static final int CODE_FINISH = 012;
    private SharedPreferences mPref;
    WindowManager wm;
    View view;
    WindowManager.LayoutParams params;
    AnimationDrawable rocketAnimation;

    int winWidth;
    int winHeight;
    private int startX;
    private int startY;

    public RocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        showRocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: 服务销毁----");
        if (wm!= null&&view != null){
            wm.removeView(view);
        }

    }

    /**
     * 自定义
     */
    public void showRocket(){
        /*int lastX = mPref.getInt("startX",0);
        int lastY = mPref.getInt("startY",0);
        int style = mPref.getInt("currentStyle",0);*/
        int[] items = new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,R.drawable.call_locate_green};
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        winWidth = wm.getDefaultDisplay().getWidth();
        winHeight = wm.getDefaultDisplay().getHeight();
        params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;//添加权限SYSTEM_ALERT_WINDOW
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;


        /*params.x = lastX;
        params.y = lastY;*/
        params.gravity = Gravity.LEFT+Gravity.TOP;//设置位置为左上方
        Log.i(TAG, "showRocket: 初始位置"+params.x+"-------------"+params.y);

        view = View.inflate(this,R.layout.rocket,null);
        final ImageView ivRocket = (ImageView) view.findViewById(R.id.imageView);
        ivRocket.setBackgroundResource(R.drawable.animation_rocket);
        rocketAnimation = (AnimationDrawable) ivRocket.getBackground();
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
                        /*mPref.edit().putInt("startX",params.x).commit();
                        mPref.edit().putInt("startY",params.y).commit();*/
                        if (params.x>winWidth/3 && params.x<winWidth*2/3 && params.y>winHeight-330){
                            //开始动画
                            rocketAnimation.start();
                            sendRocket();
                            //进入Background页面，显示烟雾
                            Intent intent = new Intent(RocketService.this, BackgroundActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else if (params.y<winHeight-150){
                            params.x = 0;
                            wm.updateViewLayout(view,params);
                        }
                        break;
                    default:break;
                }
                return false;
            }
        });
        wm.addView(view,params);
    }

    private Handler mHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CODE_SEND_ROCKET:
                    params.y = msg.arg1;
                    wm.updateViewLayout(view,params);


                    break;
                case CODE_FINISH:
                    //动画结束
                    rocketAnimation.stop();
                    //恢复初始位置
                    params.x = 0;
                    params.y = 0;
                    wm.updateViewLayout(view,params);
                    break;
            }
/*            wm.updateViewLayout(view,params);*/
        }
    };

    /**
     * 发射火箭
     */
    public void sendRocket(){
        //设置火箭居中
        params.x = winWidth/2-view.getWidth()/2;
        wm.updateViewLayout(view,params);

        new Thread(){
            @Override
            public void run() {
                super.run();
                int pos = winHeight-100;
                while (pos>0){
                    pos -= 40;
                    Message msg = Message.obtain();
                    msg.what = CODE_SEND_ROCKET;
                    msg.arg1 = pos;
                    mHandler.sendMessage(msg);
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    sleep(300);
                    Message msg = Message.obtain();
                    msg.what = CODE_FINISH;
                    msg.arg1 = pos;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


}
