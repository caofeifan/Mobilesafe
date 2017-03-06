package com.cff.mobilesafe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.cff.mobilesafe.R;

import static android.R.attr.permission;
import static android.R.attr.targetSdkVersion;

public class BaseActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS};

    public SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getSupportActionBar().hide();
        mPref = getSharedPreferences("config",MODE_PRIVATE);
    }

    public void enterTargetActivity(Context context,Class clazz){
        Intent intent = new Intent(context,clazz);
        startActivity(intent);
    }
    public void enterTargetActivityForResult(Context context,Class clazz,int requestCode){
        Intent intent = new Intent(context,clazz);
        startActivityForResult(intent,requestCode);
    }

    public static void verifyStoragePermissions(Activity activity,String requestPermission) {
        // Check if we have write permission
        int permission = -1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                permission = ActivityCompat.checkSelfPermission(activity,
                        requestPermission);
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                permission = PermissionChecker.checkSelfPermission(activity,
                        requestPermission);
            }
        }

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * 展示Toast
     * @param str
     */
    public void showToast(String str){
        Toast.makeText(this, str,Toast.LENGTH_SHORT).show();
    }
}
