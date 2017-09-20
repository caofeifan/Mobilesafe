package com.cff.mobilesafe.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/3.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = MyDatabaseHelper.class.getSimpleName();
    Context context;

    public MyDatabaseHelper(Context context) {
        super(context, "address.db",null, 1);
        this.context = context;
    }
    public MyDatabaseHelper(Context context,String name) {
        super(context, name,null, 1);
        this.context = context;
    }

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table dict(id Integer primary key autoincrement, word,detail)");
        db.execSQL("create table blacknumber(_id integer primary key autoincrement,name varchar(20),number varchar(20),mode varchar(2))");
        db.execSQL("create table applock(_id integer primary key autoincrement, packagename varchar(50),lock integer default 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void readToPath(String fileName){
        File file = new File("data/data/com.cff.mobilesafe/databases",fileName);
        if (!file.exists()){
            Log.i(TAG, "readToPath: 数据库文件不存在");
            InputStream in = null;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                in = context.getAssets().open(fileName);
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = in.read(buff)) != -1){
                    fos.write(buff,0,len);
                }
                in.close();
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (in != null){
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }else {
            Log.i(TAG, "readToPath: 数据库文件已存在");
        }



    }

/*    public void rawQuery(SQLiteDatabase db){

    }*/
}
