package com.cff.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.service.AddressService;
import com.cff.mobilesafe.service.CallSafeService;
import com.cff.mobilesafe.service.RocketService;
import com.cff.mobilesafe.service.WatchDogService;
import com.cff.mobilesafe.utils.SystemInfoUitl;
import com.cff.mobilesafe.view.SettingClickView;
import com.cff.mobilesafe.view.SettingItemView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
    private static final String TAG = SettingActivity.class.getSimpleName();
    private static final int CODE_REQUEST_STYLE = 001;
    private static final int REQUEST_CODE = 1;

    String[] items = new String[]{"半透明","活力橙","卫士蓝","苹果绿"};
    @BindView(R.id.sivUpdate)
    SettingItemView sivUpdate;
    @BindView(R.id.sivAddress)
    SettingItemView sivAddress;
    @BindView(R.id.scvStyle)
    SettingClickView scvStyle;
    @BindView(R.id.scvLocation)
    SettingClickView scvLocation;
    @BindView(R.id.scvSocket)
    SettingClickView scvSocket;
    @BindView(R.id.sivBlackNum)
    SettingItemView sivBlackNumberInfo;
    @BindView(R.id.sv_watch_dog)
    SettingItemView sv_watch_dog;



    boolean showSocket;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        initUpdateView();
        initAddressView();
        initAddressStyleView();
        initAddressLocation();
        initSocket();
        initBlackNumInfo();
        initWatchDogInfo();
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
    /**
         * 初始化黑名单显示
         */
    public void initBlackNumInfo(){
        boolean showBlackNumberInfo = mPref.getBoolean("show_blackNumberInfo", true);
        if (showBlackNumberInfo) {
            sivBlackNumberInfo.setChecked(true);
        } else {
            sivBlackNumberInfo.setChecked(false);
        }
        sivBlackNumberInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sivBlackNumberInfo.isChecked()){
                    sivBlackNumberInfo.setChecked(false);
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                    mPref.edit().putBoolean("show_blackNumberInfo",false).commit();
                }else {
                    sivBlackNumberInfo.setChecked(true);
                    startService(new Intent(SettingActivity.this, CallSafeService.class));
                    mPref.edit().putBoolean("show_blackNumberInfo",true).commit();
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

    public void initAddressLocation(){
        scvLocation.setTvTitle("归属地提示框显示位置");
        scvLocation.setCurrentStyle("设置归属地提示框的显示位置");
        scvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterTargetActivity(SettingActivity.this,DragViewActivity.class);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void initSocket(){
        showSocket= mPref.getBoolean("show_socket",false);

        if (showSocket){//开启火箭
            scvSocket.setTvTitle("关闭小火箭");
            scvSocket.setCurrentStyle("开启状态");
            //判断特殊权限
            if (!Settings.canDrawOverlays(SettingActivity.this)){
                requestAlertWindowPermission();
            }else {
                startService(new Intent(SettingActivity.this, RocketService.class));
            }
        }else {
            scvSocket.setTvTitle("开启小火箭");
            scvSocket.setCurrentStyle("关闭状态");
        }

        scvSocket.setOnClickListener(new View.OnClickListener() {
            
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: --------------------------"+showSocket);
                if (showSocket){//关闭小火箭,关闭Service
                    stopService(new Intent(SettingActivity.this, RocketService.class));
                    mPref.edit().putBoolean("show_socket", false).commit();
                    showSocket = false;
                }else {
                    //判断特殊权限
                    if (!Settings.canDrawOverlays(SettingActivity.this)){
                        requestAlertWindowPermission();
                    }else {
                        startService(new Intent(SettingActivity.this, RocketService.class));
                    }
                    mPref.edit().putBoolean("show_socket", true).commit();
                    showSocket = true;
                }


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


    private  void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE:
                if (Settings.canDrawOverlays(this)){
                    startService(new Intent(SettingActivity.this, RocketService.class));
                }
                break;
            default:break;
        }
    }

    /**
     * 初始化看门狗
     */
    public void initWatchDogInfo(){

        boolean serviceRunning = SystemInfoUitl.isServiceRunning(SettingActivity.this, "com.cff.mobilesafe.service.WatchDogService");
        System.out.println("---------->"+serviceRunning);
        if (serviceRunning) {
            sv_watch_dog.setChecked(true);
        } else {
            sv_watch_dog.setChecked(false);
        }
        sv_watch_dog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sv_watch_dog.isChecked()){
                    sv_watch_dog.setChecked(false);
                    stopService(new Intent(SettingActivity.this, WatchDogService.class));
                    mPref.edit().putBoolean("show_watchDog",false).commit();
                }else {
                    sv_watch_dog.setChecked(true);
                    mPref.edit().putBoolean("show_watchDog",true).commit();
                    startService(new Intent(SettingActivity.this, WatchDogService.class));

                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean serviceRunning = SystemInfoUitl.isServiceRunning(SettingActivity.this, "com.cff.mobilesafe.service.WatchDogService");
        System.out.println("---------->"+serviceRunning);
        if (serviceRunning) {
            sv_watch_dog.setChecked(true);
        } else {
            sv_watch_dog.setChecked(false);
        }
    }
}
