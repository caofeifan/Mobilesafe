package com.cff.mobilesafe.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.cff.mobilesafe.R;

public class Setup1Activity extends BaseActivity {
    private GestureDetector mGesture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setup1);
        mGesture = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            //扔

            /**
             * getRawX():基于屏幕的坐标点
             * getX（）:基于控件的坐标点
             * @param e1  滑动的起点
             * @param e2  滑动的终点
             * @param velocityX 水平速度
             * @param velocityY 垂直速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float e1X = e1.getRawX();  //基于屏幕的坐标点
                float e1Y = e1.getRawY();
                float e2X = e2.getRawX();
                float e2Y = e2.getRawY();
                //上一页
                if (e2X-e1X>200){

                }
                //下一页
                if (e1X-e2X>200){
                    showNext();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGesture.onTouchEvent(event);//将触摸事件委托给手势
        return super.onTouchEvent(event);
    }

    //下一页
    public void next(View view){
        showNext();
    }

    private void showNext(){
        enterTargetActivity(Setup1Activity.this,Setup2Activity.class);
        finish();
        //设置页面切换动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);
    }
}
