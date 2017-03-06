package com.cff.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Setup4Activity extends BaseActivity {
    private GestureDetector mDetector;

    @BindView(R.id.cbProtect)
    CheckBox cbProtect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setup4);
        ButterKnife.bind(this);

        Boolean protect = mPref.getBoolean("protect",false);
        if (protect){
            cbProtect.setText("防盗保护已经开启");
            cbProtect.setChecked(true);
        }else {
            cbProtect.setText("防盗保护未开启");
            cbProtect.setChecked(false);
        }
        mDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float e1X = e1.getRawX();
                float e2X = e2.getRawX();
                //上一页
                if (e2X-e1X>200) showPrePaeg();
                //下一页
                if (e1X-e2X>200) showNextPage();
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbProtect.setText("防盗保护已经开启");
                    mPref.edit().putBoolean("protect",true).commit();
                }else {
                    cbProtect.setText("防盗保护未开启");
                    mPref.edit().putBoolean("protect",false).commit();
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void showNextPage() {
        enterTargetActivity(Setup4Activity.this,LostFindActivity.class);
        //表示已经设置过设置向导了
        mPref.edit().putBoolean("configed",true).commit();
        finish();
        //设置页面切换动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);
    }

    private void showPrePaeg() {
        enterTargetActivity(Setup4Activity.this,Setup3Activity.class);
        finish();
        //设置页面切换动画
        overridePendingTransition(R.anim.pre_tran_in,R.anim.pre_tran_out);
    }

    public void previous(View view){
        showPrePaeg();
    }

    public void next(View view){
        showNextPage();
    }
}
