package com.cff.mobilesafe.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.dao.AntivirusDao;
import com.cff.mobilesafe.domain.UpdateInfo;
import com.cff.mobilesafe.domain.VirusInfo;
import com.cff.mobilesafe.domain.test;
import com.cff.mobilesafe.service.RocketService;
import com.cff.mobilesafe.utils.JsonUtil;
import com.cff.mobilesafe.utils.MyDatabaseHelper;
import com.cff.mobilesafe.utils.OkHttpClientManager;
import com.cff.mobilesafe.utils.OkHttpUtil;
import com.cff.mobilesafe.utils.StreamUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class SplashActivity extends BaseActivity {
    private String TAG = this.getClass().getSimpleName();

    public static final int CODE_UPDATE_DIALOG = 0;
    public static final int CODE_JSON_ERROR = 2;
    public static final int CODE_NET_ERROR = 3;
    public static final int CODE_URL_ERROR = 4;
    public static final int CODE_ENTER_HOME = 5;
    public static final int CODE_DOWNLOAD = 6;
    public static final int CODE_DOWNLOAD_FINISHED = 7;
    public static final int REQUEST_CODE_CANCEL_INSTALL = 8;


    private SharedPreferences mPref;
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadUrl;

    private RelativeLayout rlRoot;
    private TextView tvProgress;
    private Button bt;
    String mSDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CODE_UPDATE_DIALOG:
                    showUpdateDailog();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this,"数据解析出错！",Toast.LENGTH_SHORT).show();
                    break;
                case CODE_NET_ERROR:
                    Toast.makeText(SplashActivity.this,"网络连接出错！",Toast.LENGTH_SHORT).show();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this,"URL出错！",Toast.LENGTH_SHORT).show();
                    break;
                case CODE_ENTER_HOME:
                    enterTargetActivity(SplashActivity.this,HomeActivity.class);
                    break;
                case CODE_DOWNLOAD:
                    int progress = msg.arg1;
                    Log.d(TAG, "handleMessage: 下载进度为："+progress);
                    tvProgress.setText("下载进度："+progress+"%");
                    tvProgress.setVisibility(View.VISIBLE);
                    if (progress==100){

                    }
                    break;
                case CODE_DOWNLOAD_FINISHED:
                    installApk(mSDPath+"/app-release.apk");
                    break;
                default:break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.tv_Version);
        tv.setText("版本号："+getVersionName());
        tvProgress = (TextView) findViewById(R.id.progress);
        rlRoot = (RelativeLayout)findViewById(R.id.activity_main);
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        boolean autoUpdate = mPref.getBoolean("auto_update",true);
        Log.i(TAG, "onCreate: autoUpdate = "+ autoUpdate);
        verifyStoragePermissions(SplashActivity.this,Manifest.permission.PROCESS_OUTGOING_CALLS);
        /**
         * 创建快捷方式
         */
        createShortcut();
        /**
         * 复制数据库到指定目录
         */

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this,"antivirus.db");
        dbHelper.readToPath("antivirus.db");


        /**
         * 更新病毒数据库
         */
    //    updateVirus("http://10.0.2.2:8080/virus.json");
        verifyStoragePermissions(SplashActivity.this,Manifest.permission.PROCESS_OUTGOING_CALLS);
        if (autoUpdate){
            /**
             * 获取应用版本号
             */
            checkVersion();
        }else {
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME,2000);
        }
        //渐变的动画
        AlphaAnimation anim = new AlphaAnimation(0.3f,1f);
        anim.setDuration(1000);
        rlRoot.startAnimation(anim);
    }

    /**
     * 更新病毒数据库
     */
    private void updateVirus(String url) {
        //将服务器中的病毒特征码添加到数据库中
        OkHttpClientManager.getAsyn(url, new OkHttpClientManager.ResultCallback<List<VirusInfo>>() {
            @Override
            public void onError(Request request, Exception e) {
                System.out.println("--------------"+e.toString());
            }

            @Override
            public void onResponse(List<VirusInfo> list) {
                /**
                 * 插入到数据库
                 */
                List<Long> rows = AntivirusDao.addVirus(SplashActivity.this,list);
                if (rows != null){
                    Toast.makeText(SplashActivity.this,"数据库已更新！！！="+rows.size(),Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    /**
     * 创建快捷方式
     */
    private void createShortcut() {
        Intent intent = new Intent();
        /**
         *true 表示可以重复创建快捷方式
         * false 不可以重复创建
         */
        intent.putExtra("duplicate",false);
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        /**
         * 干什么
         * 叫什么
         * 长什么样子
         */
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机卫士");
        /**
         * 不能显示意图，
         * 必须使用隐式意图
         */
        Intent shortcut_intent = new Intent();

        shortcut_intent.setAction("com.cff.mobilesafe.HomeActivity");
        shortcut_intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortcut_intent);
        sendBroadcast(intent);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    private String getVersionName(){
        try {
            //包信息
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private int getVersionCode(){
        try {
            //包信息
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //获取服务器版本信息,并交验
    private void checkVersion(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                final long startTime = System.currentTimeMillis();
                try {
                    //模拟器加载本机的地址
                    URL urlServer = new URL("http://10.0.2.2:8080/updateVersion.json");
                    conn = (HttpURLConnection) urlServer.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);//连接超时
                    conn.setReadTimeout(5000);//服务器响应超时
                    int responseCode = conn.getResponseCode();
                    String result = null;
                    if (responseCode ==200){
                        InputStream inputStream  = conn.getInputStream();
                        result = StreamUtil.readFromStream(inputStream);
                    }
                    //解析Json
                    test updateInfo = JsonUtil.fromJson(result, test.class);
                    UpdateInfo updateInfo1 = new UpdateInfo();
                    updateInfo1.setDescription("sdfsdfs");
                    updateInfo1.setVersionCode(3);
                    updateInfo1.setVersionName("fslkjdfh");
                    mVersionName = updateInfo.getUpdateInfo().getVersionName();
                    mVersionCode = updateInfo.getUpdateInfo().getVersionCode();
                    mDescription = updateInfo.getUpdateInfo().getDescription();
                    mDownloadUrl = updateInfo.getUpdateInfo().getDownloadUrl();
                    Log.i(TAG, "run: mVersionName:"+updateInfo.getUpdateInfo());
                    Log.i(TAG, "run: mVersionCode:"+mDescription);
                    Log.i(TAG, "run: mVersionCode:"+mDownloadUrl);
                    //校验

                    if (mVersionCode > getVersionCode()){
                        Log.i(TAG, "run: 有新版本需要更新,旧版本"+getVersionCode());
                        msg.what = CODE_UPDATE_DIALOG;
                        //弹出升级对话框
                    }else {
                        //不需要更新，等待3秒后跳转到主页面
                        msg.what = CODE_ENTER_HOME;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = CODE_URL_ERROR;
                } catch (IOException e) {
                    msg.what = CODE_NET_ERROR;
                    e.printStackTrace();
                }finally {
                    if (conn != null){
                        conn.disconnect();
                    }
                    //闪屏展示两秒
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;
                    if (timeUsed<2000){
                        try {
                            Thread.sleep(2000-timeUsed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }
            }
        }.start();

    }
    private void showUpdateDailog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(""+mVersionName);
        dialog.setMessage(mDescription);
        dialog.setCancelable(false);//屏蔽返回键
        dialog.setPositiveButton("确认升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //检查权限，android6.0必须检查
                verifyStoragePermissions(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                //下载文件
                OkHttpUtil.download("http://10.0.2.2:8080/app-release.apk",mSDPath,mHandler);
            }
        });
        dialog.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterTargetActivity(SplashActivity.this,HomeActivity.class);
            }
        });
        //设置用户点击返回键触发事件
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterTargetActivityForResult(SplashActivity.this,HomeActivity.class,REQUEST_CODE_CANCEL_INSTALL);
            }
        });
        dialog.show();
    }

    private void installApk(String path){
        File file = new File(path);
        //跳转到系统下载页面
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivityForResult(intent,REQUEST_CODE_CANCEL_INSTALL);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            //用户取消安装，返回主界面
            case REQUEST_CODE_CANCEL_INSTALL:
                Log.i(TAG, "onActivityResult: 返回主界面");
                enterTargetActivity(SplashActivity.this,HomeActivity.class);
                break;

            default:break;
        }
    }


}
