package com.cff.mobilesafe.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.view.SettingItemView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();

    @BindView(R.id.sivUpdate)
    SettingItemView sivUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        //默认为true
        boolean atuoUpdate = mPref.getBoolean("auto_update",true);
        if (atuoUpdate){
            sivUpdate.setChecked(true);
        }else {
            sivUpdate.setChecked(false);
        }
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivUpdate.isChecked()){
                    sivUpdate.setChecked(false);
                    mPref.edit().putBoolean("auto_update",false).commit();
                    Log.i(TAG, "onClick: --------"+mPref.getBoolean("auto_update",true));
                }else {
                    sivUpdate.setChecked(true);
                    mPref.edit().putBoolean("auto_update",true).commit();
                    Log.i(TAG, "onClick: ++++++"+mPref.getBoolean("auto_update",true));
                }

            }
        });
    }
}
