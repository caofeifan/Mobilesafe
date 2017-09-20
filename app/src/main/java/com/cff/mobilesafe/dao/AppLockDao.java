package com.cff.mobilesafe.dao;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.cff.mobilesafe.domain.AppLock;
import com.cff.mobilesafe.engine.AppInfos;
import com.cff.mobilesafe.utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/29.
 */

public class AppLockDao {
    private MyDatabaseHelper dbHelper;
    private Context context;

    public AppLockDao(Context context) {
        dbHelper = new MyDatabaseHelper(context,"mobilesafe.db");
        this.context = context;
    }

    public void add(String packageName){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename",packageName);
        db.insert("applock", null, values);
        db.close();

    }

    public void delete(String packageName){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("delete from applock where packagename = ?",new String[]{packageName});
        db.close();
        context.getContentResolver().notifyChange(Uri.parse("change"),null);
    }

    public boolean find(String packagename){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select lock from applock where packagename = ?", new String[]{packagename});
        if (cursor.moveToNext()) {
            int isLock = cursor.getInt(0);
            cursor.close();
            db.close();
            return true;
        }
        return false;
    }

    public Boolean findLocked(String packagename,int locked){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select lock from applock where packagename = ?", new String[]{packagename});
        if (cursor.moveToNext()) {
            int isLock = cursor.getInt(0);
            cursor.close();
            db.close();
            if (isLock == locked){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    public List<AppLock> findAll(){
        List<AppLock> appLocks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select packagename,lock from applock",null);
        while (cursor.moveToNext()){
            String packageName = cursor.getString(0);
            int isLock = cursor.getInt(1);
            AppLock appLock = AppInfos.getAppLockByPackageName(context, packageName,isLock);
            appLocks.add(appLock);
        }
        return appLocks;
    }

    /**
     *
     * @param packageName
     * @param isLock
     */
    public int update(String packageName,boolean isLock){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (isLock){
            values.put("lock",1);

        }else {
            values.put("lock",0);
        }
        int row = -1;
        row = db.update("applock", values, "packagename = ?", new String[]{packageName});
        context.getContentResolver().notifyChange(Uri.parse("content://change"),null);
        return row;

    }
}
