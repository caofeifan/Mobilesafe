package com.cff.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.cff.mobilesafe.domain.VirusInfo;
import com.cff.mobilesafe.utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/27.
 */

public class AntivirusDao {

    public static String checkFileVirus(Context context, String md5) {
        String str = null;
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "antivirus.db");
        /**
         * 查询描述信息
         */
        String sql = "select desc from datable where md5 = ?";
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql, new String[]{md5});
        if (cursor.moveToNext()) {
            str = cursor.getString(0);
        }
        cursor.close();
        return str;
    }

    /**
     * 添加病毒数据库
     */
    public static List<Long> addVirus(Context context, List<VirusInfo> virusInfos) {
        ArrayList<Long> rows = new ArrayList<>();
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "antivirus.db");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (VirusInfo virusInfo :virusInfos){
            Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[] {virusInfo.getMd5()});
            if (cursor.moveToNext()){
                System.out.println("---------------------------");
                continue;
            }
            ContentValues values = new ContentValues();
            values.put("md5", virusInfo.getMd5());
            values.put("type", virusInfo.getType());
            values.put("name", virusInfo.getName());
            values.put("desc", virusInfo.getDesc());
            long row = db.insert("datable", null, values);
            cursor.close();
            rows.add(row);
            System.out.println("---------------------------");
        }
        db.close();
        return rows;
    }
}
