package com.cff.mobilesafe.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.view.SettingItemView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Setup2Activity extends BaseActivity {
    private static final String TAG = Setup2Activity.class.getSimpleName();

    @BindView(R.id.sivSIM)
    SettingItemView sivSIM;
    private GestureDetector mDetector;
    private TelephonyManager mTmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setup2);
        ButterKnife.bind(this);
        //检查权限，android6.0必须检查
        verifyStoragePermissions(Setup2Activity.this, Manifest.permission.READ_PHONE_STATE);
        mDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float e1X = e1.getRawX();
                float e1Y = e1.getRawY();
                float e2X = e2.getRawX();
                float e2Y = e2.getRawY();
                //上一页
                if (e2X-e1X>200) showPrePaeg();
                if (e1X-e2X>200) showNextPage();
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        if (mPref.getString("sim",null)==null){
            sivSIM.setChecked(false);
        }else {
            sivSIM.setChecked(true);
        }

    }
    @OnClick(R.id.sivSIM)
    public void onClick(View view){
        if (sivSIM.isChecked()){
            sivSIM.setChecked(false);
        mPref.edit().remove("sim").commit(); //删除已经绑定的SIM卡
        }else {
            sivSIM.setChecked(true);
            //保存SIM卡信息
            mTmanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            int state = mTmanager.getSimState();
            if (state == TelephonyManager.SIM_STATE_READY){
                String str = mTmanager.getSimSerialNumber();
                mPref.edit().putString("sim",str).commit();
                Log.i(TAG, "onClick: SIM卡序列号："+str);
            }

        }
    }
    //下一页
    public void next(View view){
        showNextPage();
    }

    public void previous(View view){
        showPrePaeg();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void showNextPage(){
        //如果SIM卡没有绑定，就不允许进入下一个页面
        if(TextUtils.isEmpty(mPref.getString("sim",null))){
            Toast.makeText(this,"SIM卡未绑定",Toast.LENGTH_SHORT).show();
            return;
        }
        enterTargetActivity(Setup2Activity.this,Setup3Activity.class);
        finish();
        //设置页面切换动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);
    }

    private void showPrePaeg(){
        enterTargetActivity(Setup2Activity.this,Setup1Activity.class);
        finish();
        //设置页面切换动画
        overridePendingTransition(R.anim.pre_tran_in,R.anim.pre_tran_out);
    }
}
