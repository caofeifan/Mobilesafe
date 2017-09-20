package com.cff.mobilesafe.utils;

import android.Manifest;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cff.mobilesafe.activity.SplashActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.cff.mobilesafe.activity.BaseActivity.verifyStoragePermissions;

/**
 * Created by 80755 on 2017/2/24.
 */

public class OkHttpUtil {
    private static final String TAG = OkHttpUtil.class.getSimpleName();

    //异步下载文件
    /**
     *
     */
    public static void download(final String url, final String path, final Handler handler){
        final OkHttpClient client = new OkHttpClient();
        new Thread(){
            @Override
            public void run() {
                super.run();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i(TAG, "onFailure: 服务器响应出错！");
                        e.printStackTrace();

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(!response.isSuccessful()) throw new IOException("Unexcepted code"+response);
                        InputStream in = null;
                        byte[] buffer = new byte[2048];
                        int len = 0;
                        FileOutputStream fos = null;
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                            Log.i(TAG, "onResponse: 目录存在。。。");
                        }

                        in = response.body().byteStream();
                        long total = response.body().contentLength();
                        File file = new File(path,"app-release.apk");
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        Message msg = null;
                        while ((len = in.read(buffer)) != -1){
                            fos.write(buffer,0,len);
                            sum += len;
                            int progress = (int) (sum*1.0f/total*100);
                            Log.d(TAG, "onResponse: progress: "+sum+"/"+total);
                            msg = Message.obtain(handler);
                            msg.what = SplashActivity.CODE_DOWNLOAD;
                            msg.arg1 = progress;
                            handler.sendMessage(msg);
                        }
                        fos.flush();
                        msg = Message.obtain(handler);
                        msg.what = SplashActivity.CODE_DOWNLOAD_FINISHED;
                        handler.sendMessage(msg);
                        Log.i(TAG, "onResponse: 文件下载成功");
                        if (in!= null){
                            in.close();
                        }
                        if (fos != null){
                            fos.close();
                        }
                    }
                });
            }
        }.start();
    }
}
