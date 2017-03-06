package com.cff.mobilesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.receiver.AdminReceiver;

public class TransparentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_transparent);

        //设备管理器
        DevicePolicyManager mDPM = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdminSample = new ComponentName(this,AdminReceiver.class);

        //激活设备管理器
        // Launch the activity to have the user enable our admin.
        Intent intent1 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "茶几设备管理器");
        startActivity(intent1);
        finish();
    }
}
