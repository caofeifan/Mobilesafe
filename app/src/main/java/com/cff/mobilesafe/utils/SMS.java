package com.cff.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.cff.mobilesafe.domain.SMSBackup;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 短信相关类
 * Created by caofeifan on 2017/3/17.
 */

public class SMS {
    private static final String TAG = SMS.class.getSimpleName();
    /**
     * 短信备份
     * 1、判断是否有SD卡
     * 2、权限 ---内容观察者
     * 3、写短信到SD卡:xml格式
     * @param context
     * @return
     */
    public static boolean backUp(Context context, BackUpSms callback){
        System.out.println("-----------------------");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file = new File(Environment.getExternalStorageDirectory(), "smsbackup.xml");
            Log.i(TAG, "backUp: "+file.getPath());
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                /**
                 * type = 1;接收
                 * type=  2:发送
                 */
                Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);
                int count = cursor.getCount();
                Log.i(TAG, "backUp: 短信数-------"+count);
                System.out.println("backUp: 短信数-------"+count);
                /**
                 * dialog最大值
                 * 回调
                 */
                /*dialog.setMax(count);*/
                callback.before(count);
                /**
                 * 序列化器
                 * xml pull解析
                 */
                XmlSerializer xmlSerializer = Xml.newSerializer();
                xmlSerializer.setOutput(fileOutputStream,"utf-8");

                xmlSerializer.startDocument("utf-8",true);
                xmlSerializer.startTag(null,"smss");

                xmlSerializer.attribute(null,"size",String.valueOf(count));

                /**
                 * 进度条默认值
                 */
                int process = 0;
                while (cursor.moveToNext()){
                    xmlSerializer.startTag(null,"sms");
                    xmlSerializer.startTag(null,"address");
                    xmlSerializer.text(cursor.getString(0));
                    xmlSerializer.endTag(null,"address");

                    xmlSerializer.startTag(null,"date");
                    xmlSerializer.text(cursor.getString(1));
                    xmlSerializer.endTag(null,"date");

                    xmlSerializer.startTag(null,"type");
                    xmlSerializer.text(cursor.getString(2));
                    xmlSerializer.endTag(null,"type");

                    xmlSerializer.startTag(null,"body");
                    xmlSerializer.text(Crypto.encrypt(Global.SEED,cursor.getString(3)));
                    xmlSerializer.endTag(null,"body");

                    xmlSerializer.endTag(null,"sms");

                    process++;
                    /*dialog.setProgress(process);*/
                    /**
                     * 回调接口
                     */
                    callback.onBackUpSms(process);
                    Thread.sleep(200);
                }


                xmlSerializer.endTag(null,"smss");
                xmlSerializer.endDocument();
                cursor.close();
                fileOutputStream.close();
                /**
                 * 关闭progress
                 */
                /*dialog.dismiss();*/
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return false;
    }

    /**
     *
     */
    public interface BackUpSms{
        public void before(int count);
        public void onBackUpSms(int process);
    }
}
