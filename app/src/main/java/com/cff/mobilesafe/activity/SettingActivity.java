package com.cff.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.service.AddressService;
import com.cff.mobilesafe.view.SettingClickView;
import com.cff.mobilesafe.view.SettingItemView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    private static final int CODE_REQUEST_STYLE = 001;

    String[] items = new String[]{"半透明","活力橙","卫士蓝","苹果绿"};
    @BindView(R.id.sivUpdate)
    SettingItemView sivUpdate;
    @BindView(R.id.sivAddress)
    SettingItemView sivAddress;
    @BindView(R.id.scvStyle)
    SettingClickView scvStyle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        initUpdateView();
        initAddressView();
        initAddressStyleView();

    }

    /**
     * 更新开启
     */
    public void initUpdateView() {
        boolean atuoUpdate = mPref.getBoolean("auto_update", true);
        if (atuoUpdate) {
            sivUpdate.setChecked(true);
        } else {
            sivUpdate.setChecked(false);
        }
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivUpdate.isChecked()) {
                    sivUpdate.setChecked(false);
                    mPref.edit().putBoolean("auto_update", false).commit();
                    Log.i(TAG, "onClick: --------" + mPref.getBoolean("auto_update", true));
                } else {
                    sivUpdate.setChecked(true);
                    mPref.edit().putBoolean("auto_update", true).commit();
                    Log.i(TAG, "onClick: ++++++" + mPref.getBoolean("auto_update", true));
                }

            }
        });
}
    /**
     * 初始化归属地显示
     */
    public void initAddressView(){
        boolean showAddress = mPref.getBoolean("show_address", true);
        if (showAddress) {
            sivAddress.setChecked(true);
        } else {
            sivAddress.setChecked(false);
        }
        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivAddress.isChecked()){
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                    mPref.edit().putBoolean("show_address",false).commit();
                }else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));
                    mPref.edit().putBoolean("show_address",true).commit();
                }
            }
        });
    }

    public void initAddressStyleView(){
        int selected = mPref.getInt("currentStyle",0);
        scvStyle.setCurrentStyle(items[selected]);
        scvStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSimgleChooseDialog();
            }
        });
    }

    private void showSimgleChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("设置归属地框风格");

        int selected = mPref.getInt("currentStyle",0);
        builder.setSingleChoiceItems(items, selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt("currentStyle",which).commit();
                scvStyle.setCurrentStyle(items[which]);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }




}
