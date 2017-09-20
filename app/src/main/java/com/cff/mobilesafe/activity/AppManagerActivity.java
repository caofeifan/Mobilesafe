package com.cff.mobilesafe.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.adapter.AppInfoAdapter;
import com.cff.mobilesafe.domain.AppInfo;
import com.cff.mobilesafe.engine.AppInfos;
import com.cff.mobilesafe.listener.OnItemViewClickPopupWindowListener;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;
import com.cff.mobilesafe.receiver.UninstallReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Filter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppManagerActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = AppManagerActivity.class.getSimpleName();
    private static final int CODE_REQUEST_UNINSTALL = 001;


    @BindView(R.id.tv_rom)
    TextView tv_rom;

    @BindView(R.id.tv_sd)
    TextView tv_sd;

    @BindView(R.id.list_view_app)
    RecyclerView mRecyclerView;

    @BindView(R.id.tv_appTips)
    TextView tv_appTips;

    private List<AppInfo> appInfos;
    private ArrayList<AppInfo> userAppInfos;
    private ArrayList<AppInfo> systemAppInfos;
    private AppInfoAdapter adapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter = new AppInfoAdapter(AppManagerActivity.this, userAppInfos,systemAppInfos);
            mRecyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemViewClickPopupWindowListener() {
                @Override
                public void onItemClick(View view , int position) {
                    showPopUpwindow(view , position);
                }
            });
        }
    };
    private PopupWindow popupWindow;
    private View contentView;
    private AppInfo clickAppInfo;
    private UninstallReceiver receiver;
    private int clickPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_app_manager);
        ButterKnife.bind(this);

        initUI();
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                //拆分用户程序和系统程序集合
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo:appInfos){
                    if (appInfo.isUserApp()){
                        userAppInfos.add(appInfo);
                    }else {
                        systemAppInfos.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //内存空间
        File directory = Environment.getDataDirectory();
        long freeSpace = directory.getFreeSpace();
        long totalSpace = directory.getTotalSpace();
        long usableSpace = directory.getUsableSpace();
        //SD卡空间
        File storageDirectory = Environment.getExternalStorageDirectory();
        long usableSpace1 = storageDirectory.getUsableSpace();

        tv_rom.setText("内存可用：" + Formatter.formatFileSize(this, usableSpace));
        tv_sd.setText("sd卡可用：" + Formatter.formatFileSize(this, usableSpace1));


        //注册广播
        receiver = new UninstallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        registerReceiver(receiver, filter);

        //设置滚动监听
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int firstVisibleItemPosition;
                if (userAppInfos != null && userAppInfos != null){
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    //线性布局
                    if (layoutManager instanceof LinearLayoutManager){
                        firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
                        if (firstVisibleItemPosition>=0 && firstVisibleItemPosition < userAppInfos.size()+1){
                            tv_appTips.setText("用户程序("+userAppInfos.size()+")");
                        }else {
                            tv_appTips.setText("系统程序("+systemAppInfos.size()+")");
                        }
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                dismissPopupWindow();
            }
        });

    }

    public void showPopUpwindow(View view , int position){
        clickPosition = position;
        dismissPopupWindow();
        contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup,null);
        if (position<userAppInfos.size()+1){
            //用户程序
            clickAppInfo = userAppInfos.get(position-1);
        }else {
            clickAppInfo = systemAppInfos.get(position-userAppInfos.size()-2);
        }
        Log.i(TAG, "showPopUpwindow: ----------------------"+clickAppInfo.getApkName());

        LinearLayout ll_unInstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
        LinearLayout ll_run = (LinearLayout) contentView.findViewById(R.id.ll_run);
        LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);

        ll_unInstall.setOnClickListener(this);
        ll_run.setOnClickListener(this);
        ll_share.setOnClickListener(this);

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int[] location = new int[2];
        //获取点击的View的位置
        view.getLocationInWindow(location);
        popupWindow.showAtLocation(view, Gravity.LEFT+Gravity.TOP,200,location[1]-100);

        //设置背景
        /*popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));*/
        ScaleAnimation sa = new ScaleAnimation(0.5f,1.0f,0.5f,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(500);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        contentView.startAnimation(sa);
        contentView.startAnimation(alphaAnimation);

    }

    /**
     * 删除popUpwindow
     */
    public void dismissPopupWindow(){
        if (popupWindow != null && popupWindow.isShowing()){
            ScaleAnimation sa = new ScaleAnimation(1.0f,0.5f,1.0f,0.5f,
                    Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            sa.setDuration(500);
            AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
            alphaAnimation.setDuration(500);
            contentView.startAnimation(sa);
            contentView.startAnimation(alphaAnimation);
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPopupWindow();
        unregisterReceiver(receiver);
    }

    public void clickTextView(View view){
        dismissPopupWindow();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_uninstall:
                unInstall();
                break;
            case R.id.ll_run://运行App
                runApp();
                break;
            case R.id.ll_share:
                share();
                break;
            default:break;
        }
    }

    public void runApp(){
        Log.i(TAG, "runApp: --------------"+clickAppInfo.getApkPackageName());
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(clickAppInfo.getApkPackageName(), 0);
            //获取Activity数
            ActivityInfo[] activityInfos = packageInfo.activities;
            if (activityInfos != null && activityInfos.length>0){
                Intent localIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
                this.startActivity(localIntent);
                dismissPopupWindow();
            }else {
                Toast.makeText(AppManagerActivity.this, "该应用没有界面，亲！！", Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    /***
     * 发送短信，分享
     */
    public void share(){
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.SUBJECT", "分享");
        intent.putExtra("android.intent.extra.TEXT", "Hi，给你分享一款软件："+clickAppInfo.getApkName()+
                    "下载地址："+"https://play.google.com/store/apps/details?id="+clickAppInfo.getApkName());
        this.startActivity(Intent.createChooser(intent, "分享"));
        dismissPopupWindow();
    }

    public void unInstall(){

        if (clickAppInfo.isUserApp()) {

            Intent uninstall_intent = new Intent();
            uninstall_intent.setAction(Intent.ACTION_DELETE);
            uninstall_intent.setData(Uri.parse("package:"+clickAppInfo.getApkPackageName()));
            startActivityForResult(uninstall_intent, CODE_REQUEST_UNINSTALL);
            dismissPopupWindow();

        }else {
            Toast.makeText(AppManagerActivity.this,"sdf",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(AppManagerActivity.this,"\"onActivityResult: =======================\"",Toast.LENGTH_SHORT).show();
        initData();
    }
}
