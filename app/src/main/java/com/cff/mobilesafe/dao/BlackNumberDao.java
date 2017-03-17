package com.cff.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cff.mobilesafe.domain.BlackNumberInfo;
import com.cff.mobilesafe.utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单
 * Created by caofeifan on 2017/3/13.
 */

public class BlackNumberDao {
    private final MyDatabaseHelper dbHelper;

    public BlackNumberDao(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }


    /**
     * @param number 号码
     * @param mode   模式
     */
    public void add(String number, String mode) {
    }

    /**
     * @param number
     */
    public void delete(String number) {
    }

    /**
     * 通过电话号码修改拦截模式
     *
     * @param number
     */
    public boolean changeNumberMode(String number) {
        return false;
    }

    public BlackNumberInfo find(String number) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select name,number,mode from blacknumber where number = ?",
                new String[]{number});
        BlackNumberInfo blackNumberInfo = null;
        if (cursor.moveToNext()){
            blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setName(cursor.getString(0));
            blackNumberInfo.setNumber(cursor.getString(1));
            blackNumberInfo.setMode(cursor.getString(2));
        }

        return blackNumberInfo;
    }

    public List<BlackNumberInfo> findAll() {
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setName("曹飞帆");
            blackNumberInfo.setNumber("13022807887");
            blackNumberInfo.setMode("电话拦截");
            blackNumberInfos.add(blackNumberInfo);
        }


        return blackNumberInfos;
    }

    /**
     * 分页加载数据
     *
     * @param currentPage 当前页
     * @param pageSize    每页的大小
     * @return limit 限制当前数据
     * offset 从第几条开始
     */
    public List<BlackNumberInfo> findPart(int currentPage, int pageSize) {
        ArrayList<BlackNumberInfo> blackNumberInfos = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select name,number,mode from blacknumber limit ? offset ?",
                new String[]{pageSize + "", String.valueOf(currentPage * pageSize)});
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setName(cursor.getString(0));
            blackNumberInfo.setNumber(cursor.getString(1));
            blackNumberInfo.setMode(cursor.getString(2));
            blackNumberInfos.add(blackNumberInfo);
        }
        return blackNumberInfos;

    }

    public void addBlackNumberInfo(){
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
        writableDatabase.execSQL("insert into blacknumber(name,number,mode) values('韩晓羽','18392565390','短信屏蔽')",
                new String[]{});
    }

}
