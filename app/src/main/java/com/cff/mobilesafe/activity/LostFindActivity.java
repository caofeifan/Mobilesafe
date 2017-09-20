package com.cff.mobilesafe.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LostFindActivity extends BaseActivity {
    @BindView(R.id.tvSafePhone)
    TextView tvSafePhone;
    @BindView(R.id.ivProtect)
    ImageView ivProtect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        boolean configed = mPref.getBoolean("configed",false);

        if (configed){
            setContentView(R.layout.activity_lost_find);
            //绑定activity

        }else {
            enterTargetActivity(LostFindActivity.this,Setup1Activity.class);

        }
        ButterKnife.bind( LostFindActivity.this ) ;
        String phone = mPref.getString("safePhone","");
        Boolean protect = mPref.getBoolean("protect",false);
        if (TextUtils.isEmpty(phone)){
            tvSafePhone.setText("无");
        }else {
            tvSafePhone.setText(phone);
        }
        if (protect){
            ivProtect.setImageResource(R.mipmap.lock);
        }else {
            ivProtect.setImageResource(R.mipmap.unlock);
        }


    }

    /**
     * 重新进入设置向导
     * @param view
     */
    public void reEnterGuide(View view){
        enterTargetActivity(LostFindActivity.this,Setup1Activity.class);
        finish();
    }
}
