package com.cff.mobilesafe.activity;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.utils.MyDatabaseHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AToolsActivity extends BaseActivity {
    private static final String TAG = AToolsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this,"mydata.db",null,1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.i(TAG, "onCreate: -------------------------------");


    }
    //归属地查询
    public void numberAddressQuery(View view){}
}
