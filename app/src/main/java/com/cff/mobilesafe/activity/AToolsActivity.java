package com.cff.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.utils.SMS;

/**
 * 高级工具
 */

public class AToolsActivity extends BaseActivity {
    private static final String TAG = AToolsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);

    }
    //归属地查询
    public void numberAddressQuery(View view){
        enterTargetActivity(this,AddressActivity.class);
    }

    /**
     * 短信备份
     * @param view
     */
    public void backUpSMS(View view){
        final ProgressDialog dialog = new ProgressDialog(AToolsActivity.this);
        dialog.setTitle("提示");
        dialog.setMessage("正在备份");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();

        new Thread(){
            @Override
            public void run() {
                Log.i(TAG, "run: ---------------=================");
                boolean backUp = SMS.backUp(AToolsActivity.this,new SMS.BackUpSms(){

                    @Override
                    public void before(int count) {
                        dialog.setMax(count);
                    }

                    @Override
                    public void onBackUpSms(int process) {
                        dialog.setProgress(process);
                    }
                });
                dialog.dismiss();
                if (backUp){
                    Looper.prepare();
                    Toast.makeText(AToolsActivity.this,"备份成功",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }else {
                    Looper.prepare();
                    Toast.makeText(AToolsActivity.this,"备份失败",Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }.start();

    }

    public void appLock(View view){
        Intent intent = new Intent(AToolsActivity.this, AppLockActivity.class);
        startActivity(intent);
    }
}
