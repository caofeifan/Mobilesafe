package com.cff.mobilesafe.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.utils.MyDatabaseHelper;

public class AToolsActivity extends BaseActivity {
    private static final String TAG = AToolsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        Log.i(TAG, "onCreate: -------------------------------");

    }
    //归属地查询
    public void numberAddressQuery(View view){
        enterTargetActivity(this,AddressActivity.class);
    }
}
