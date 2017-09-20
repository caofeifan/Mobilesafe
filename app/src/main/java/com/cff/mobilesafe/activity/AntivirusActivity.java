package com.cff.mobilesafe.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.dao.AntivirusDao;
import com.cff.mobilesafe.domain.AppInfo;
import com.cff.mobilesafe.engine.AppInfos;
import com.cff.mobilesafe.utils.CommonUtil;
import com.cff.mobilesafe.utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AntivirusActivity extends BaseActivity {
    private static final String TAG = AntivirusActivity.class.getSimpleName();
    private static final int BEGIN = 1; //开始扫描
    private static final int SCANNING = 2;//扫描中
    private static final int FINISH = 3;//扫描结束

    @BindView(R.id.iv_image2)
    ImageView iv_image2;
    @BindView(R.id.tv_initvirus)
    TextView tv_initvirus;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.tv_scan_result)
    TextView tv_scan_result;
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.ll_content)
    LinearLayout ll_content;

    private List<ScannInfo> scannInfos;

    private Message msg;
    private RotateAnimation animation;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case BEGIN:
                    prepareScanner();
                    break;
                case SCANNING:
                    Scanning(msg);
                    break;
                case FINISH:
                    finishScanner();
                    break;
                default:break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_antivirus);
        ButterKnife.bind(this);
        
        initUI();
        initData();
    }



    private void initUI() {
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        animation.setInterpolator(linearInterpolator);
        animation.setDuration(5000);
        animation.setRepeatCount(Animation.INFINITE);

    }

    private void initData() {
        new Thread(){

            @Override
            public void run() {
                super.run();

                msg = Message.obtain();
                msg.what = BEGIN;

                scannInfos = new ArrayList<ScannInfo>();
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> packages = packageManager.getInstalledPackages(0);
                progressBar.setMax(packages.size());
                int i = 0;
                for (PackageInfo packageInfo : packages){
                    ScannInfo scannInfo = new ScannInfo();
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                    scannInfo.appName = appName;
                    scannInfo.packageName =packageInfo.applicationInfo.packageName;
                    //获取目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //获取文件的MD5
                    String md5 = CommonUtil.getFileMD5(sourceDir);
                    System.out.println("--------------------------->>>>"+md5);
                    System.out.println("--------------------------->>>>"+appName);
                    String desc = AntivirusDao.checkFileVirus(AntivirusActivity.this, md5);

                    if (desc == null){
                        scannInfo.desc = false;

                    }else {
                        scannInfo.desc = true;
                        scannInfo.virus = desc;
                    }
                    i++;
                    progressBar.setProgress(i);
                    msg = Message.obtain();
                    msg.what = SCANNING;
                    msg.obj = scannInfo;
                    handler.sendMessage(msg);
                }
                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void prepareScanner() {
        iv_image2.startAnimation(animation);
        tv_content.setVisibility(View.VISIBLE);
    }

    /**
     * 扫描中，添加
     */
    private void Scanning(Message msg) {
        ScannInfo scannInfo = (ScannInfo) msg.obj;
        System.out.println("----------------->"+scannInfo.appName);
        TextView textView = new TextView(AntivirusActivity.this);
        String text = "";
        if (scannInfo != null){
            /**
             * true 有病毒
             * false 安全
             */
            if (scannInfo.desc == true){
                textView.setTextColor(Color.RED);
                text = scannInfo.appName+"   危险";
            }else {
                textView.setTextColor(Color.GREEN);
                text = scannInfo.appName+"   安全";
            }

            textView.setText(text);
        }
        ll_content.addView(textView);


    }

    private void finishScanner() {
        iv_image2.clearAnimation();
        iv_image2.setVisibility(View.INVISIBLE);
    }

    class ScannInfo {
        /**
         * 是否有病毒
         */
        boolean desc;
        /**
         * 病毒描述
         */
        String virus;
        /**
         * 所属app姓名
         */
        String appName;
        String packageName;
    }
}
