package com.cff.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cff.mobilesafe.utils.MyDatabaseHelper;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/6.
 */

public class AddressDao {
    private static final String TAG = AddressDao.class.getSimpleName();
    private static Context context;
    public AddressDao(Context context) {
        this.context = context;
    }

    public String getAddress(String number){
        String address = "未知号码";
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context,"address.db",null,1);
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (number.matches("^1[3-8]\\d{9}$")){//正则表达式匹配手机号码
            //查询记录
            Cursor cursor = db.rawQuery("select location from data2 where id = (select outkey from data1 where id = ? ) ",new String[]{number.substring(0,7)});
            //提取记录
            if (cursor.moveToNext()){
                address = cursor.getString(0);
            }
            cursor.close();
        }else if(number.matches("^\\d+$")) {//匹配其他号码(数字)
            switch (number.length()){
                case 3:
                    address = "报警电话";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 6:break;
                case 7:
                case 8:
                    address = "本地座机";
                    break;
                default:
                    if (number.startsWith("0") && number.length()>10){//长途电话
                        //有些区号是3位，有些是4位
                        //先查询4位的
                        Cursor cursor = db.rawQuery("select location from data2 where area = ?",new String[]{number.substring(1,4)});
                        if (cursor.moveToNext()) {
                            address = "固定电话："+ cursor.getString(0);
                        } else {
                            cursor.close();
                            //查询3位的
                            cursor = db.rawQuery("select location from data2 where area = ?",new String[]{number.substring(1,3)});
                            if (cursor.moveToNext()){
                                address = "固定电话："+ cursor.getString(0);
                            }
                            cursor.close();
                        }

                    }
                    break;
            }
        }
        //关闭数据库
        db.close();
        return address;
    }
}
