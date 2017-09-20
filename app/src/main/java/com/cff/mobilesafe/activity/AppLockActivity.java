package com.cff.mobilesafe.activity;

import android.icu.util.BuddhistCalendar;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.dao.AppLockDao;
import com.cff.mobilesafe.domain.AppInfo;
import com.cff.mobilesafe.domain.AppLock;
import com.cff.mobilesafe.engine.AppInfos;
import com.cff.mobilesafe.fragment.LockFragment;
import com.cff.mobilesafe.fragment.UnLockFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/28.
 */

public class AppLockActivity extends BaseActivity implements View.OnClickListener,
        UnLockFragment.OnFragmentInteractionListener,LockFragment.OnFragmentInteractionListener1 {
    @BindView(R.id.fl_content)
    FrameLayout fl_content;
    @BindView(R.id.tv_unlock)
    TextView tv_unlock;
    @BindView(R.id.tv_lock)
    TextView tv_lock;
    private FragmentManager fragmentManager;
    private UnLockFragment unLockFragment;
    private LockFragment lockFragment;

    private List<AppLock> appsUnLock;
    private List<AppLock> appsLock;
    private AppLockDao appLockDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        /**
         * 初始化List
         */
        appsUnLock = new ArrayList<>();
        appsLock = new ArrayList<>();
        tv_unlock.setOnClickListener(this);
        tv_lock.setOnClickListener(this);
        /**
         * 处理话List
         */
        List<AppInfo> appInfos = AppInfos.getAppInfos(this);
        appLockDao = new AppLockDao(this);
        for (AppInfo appInfo : appInfos){
            if (appLockDao.find(appInfo.getApkPackageName())){//在数据库中的

            }else {//不在数据库中
                appLockDao.add(appInfo.getApkPackageName());
            }

        }
        List<AppLock> list = appLockDao.findAll();
        for (AppLock appLock : list){
            if (appLock.isLock()){
                appsLock.add(appLock);
            }else {
                appsUnLock.add(appLock);
            }
        }
        /**
         * 获取fragment管理者
         */
        fragmentManager = getSupportFragmentManager();
        unLockFragment = new UnLockFragment();
        initFragment(unLockFragment,lockFragment,appsUnLock,"appsUnLock");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_unlock:

                pressedUnlock();

                break;
            case R.id.tv_lock:
                pressedLock();

                break;
        }

    }

    /**
     *按下未加锁
     */
    private void pressedUnlock() {
        tv_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);
        tv_lock.setBackgroundResource(R.mipmap.tab_right_default);
        unLockFragment = new UnLockFragment();
        initFragment(unLockFragment,lockFragment,appsUnLock,"appsUnLock");
    }

    /**
     *按下加锁
     */
    private void pressedLock() {
        tv_unlock.setBackgroundResource(R.mipmap.tab_left_default);
        tv_lock.setBackgroundResource(R.mipmap.tab_right_pressed);
        lockFragment = new LockFragment();
        initFragment(lockFragment,unLockFragment,appsLock,"appsLock");
    }



    @Override
    public void onFragmentInteraction(AppLock appLock) {
        //更新数据库内容,加锁
        appLockDao.update(appLock.getPackageName(),true);
        appsUnLock.remove(appLock);
        appsLock.add(appLock);
        ArrayList<String> packagesName = new ArrayList<>();
        for (AppLock locks : appsLock){
            packagesName.add(locks.getPackageName());
        }
        //向Fragment发送参数
        Bundle arguments = new Bundle();
        arguments.putSerializable("packagesName", (Serializable) appsLock);
        if (lockFragment != null){
            lockFragment.updateView(appsLock);
            System.out.println("-------------------------0000000000000");
        }else {
            lockFragment = new LockFragment();
            lockFragment.setArguments(arguments);
        }
    }

    @Override
    public void onFragmentInteraction1(AppLock appLock) {
        //更新数据库内容,解锁
        appLockDao.update(appLock.getPackageName(),false);
        appsLock.remove(appLock);
        appsUnLock.add(appLock);
        /**
         * 传递参数
         */
        Bundle arguments = new Bundle();
        arguments.putSerializable("appsUnLock",(Serializable) appsUnLock);
        if (unLockFragment != null) {
            unLockFragment.updateView(appsUnLock);
        }else {
            unLockFragment = new UnLockFragment();
            unLockFragment.setArguments(arguments);
        }
    }

    public void initFragment(Fragment fragment1,Fragment fragment2,List<AppLock> list,String key){
        /**
         * 开启事务
         */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle argument = new Bundle();
        argument.putSerializable(key, (Serializable) list);
        fragment1.setArguments(argument);
        /**
         * 默认显示的fragment
         */
        transaction.replace(R.id.fl_content, fragment1);
        transaction.commit();
        /**
         * 销毁加锁Fragment
         */
        if (fragment2 != null){
            transaction.remove(fragment2);
            transaction.hide(fragment2);
        }
    }
}
