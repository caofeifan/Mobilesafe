package com.cff.mobilesafe.activity;

import android.Manifest;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.adapter.CleanCacheAdapter;
import com.cff.mobilesafe.domain.AppCache;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;
import com.cff.mobilesafe.utils.ImageUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cff.mobilesafe.activity.BaseActivity.verifyStoragePermissions;

public class CleanCacheActivity extends AppCompatActivity {
    @BindView(R.id.rl_appCache)
    RecyclerView recyclerView;
    @BindView(R.id.bt_cancel)
    Button bt_cancel;
    @BindView(R.id.bt_cleanAll)
    Button bt_cancelAll;
    private List<AppCache> apps;
    private CleanCacheAdapter adapter;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            List<AppCache> appss = (List<AppCache>) msg.obj;


            /**
             * 初始化数据
             */

            adapter = new CleanCacheAdapter(CleanCacheActivity.this, appss);
            recyclerView.setAdapter(adapter);
            /**
             * 设置listener
             */
            adapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
                @Override
                public void onItemClick(View view, String position) {
                    deleteItem(Integer.parseInt(position));
                }
            });

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);
        ButterKnife.bind(this);
        initUI();
        verifyStoragePermissions(CleanCacheActivity.this, Manifest.permission.DELETE_CACHE_FILES);
    }

    /**
     *
     */
    private void initUI() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        apps = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        final List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);


        /**
         * hid 掉了
         * packageManager.getPackageSizeInfo();
         * public void getPackageSizeInfo(String packageName, IPackageStatsObserver observer) {
                getPackageSizeInfoAsUser(packageName, UserHandle.myUserId(), observer);
         }
         */
     //   packageManager.getPackageSizeInfo();

        /**
         * 初始化数据
         */
        new Thread(){
            @Override
            public void run() {
                super.run();
                /**
                 * 初始化数据
                 */
                for (PackageInfo packageInfo: installedPackages){
                    getCacheSize(packageInfo);
                }
                Message msg = Message.obtain();
                msg.obj = apps;
                msg.what = 0;
                handler.sendMessage(msg);

            }
        }.start();

    }

    /**
     * 利用反射
     * @param packageInfo
     */
    private void getCacheSize(final PackageInfo packageInfo) {
        try {
            /**
             * 获取类
             */
          //  Class<?> aClass = getClassLoader().loadClass("PackageManager");
            /**
             * 方法、参数
             * public void getPackageSizeInfo(String packageName, IPackageStatsObserver observer) {
                    getPackageSizeInfoAsUser(packageName, UserHandle.myUserId(), observer);
             }
             */
            Method method = null;
            try {
                method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class,IPackageStatsObserver.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            /**
             * invoke调用方法
             * 参数：被调用方法的对象，方法的参数
             */
            // 调用 getPackageSizeInfo 方法，需要两个参数：1、需要检测的应用包名；2、回调
                method.invoke(getPackageManager(),
                        packageInfo.packageName,
                        new IPackageStatsObserver.Stub() {
                            @Override
                            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                                // 子线程中默认无法处理消息循环，自然也就不能显示Toast，所以需要手动Looper一下
                                /*Looper.prepare();
                                // 从pStats中提取各个所需数据
                                Toast.makeText(CleanCacheActivity.this,
                                        "缓存大小=" + Formatter.formatFileSize(CleanCacheActivity.this, pStats.cacheSize) +
                                                "\n数据大小=" + Formatter.formatFileSize(CleanCacheActivity.this, pStats.dataSize) +
                                                "\n程序大小=" + Formatter.formatFileSize(CleanCacheActivity.this, pStats.codeSize),
                                        Toast.LENGTH_LONG).show();
                                // 遍历一次消息队列，弹出Toast
                                Looper.loop();*/
                                AppCache appCache = new AppCache();
                                Bitmap bitmap = ImageUtils.drawableToBitmap(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                                System.out.println("--------------------"+bitmap.toString());
                                appCache.setIcon(bitmap);
                                appCache.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                                appCache.setAppSize(pStats.cacheSize);
                                apps.add(appCache);

                            }
                        }
                );
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

    }

    /**
     * 删除指定item
     */
    public void deleteItem(int position){
        apps.remove(position);
        adapter.notifyDataChanged(apps);
    }

    /**
     *
     * @param view
     */
    public void cleanAll(View view) throws Throwable {
        List<PackageInfo> installedPackages = getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo: installedPackages){
            try {
                Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);

                try {
                    method.invoke(getPackageManager(),packageInfo.packageName,new MyIPackageDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        /*Method[] methods = PackageManager.class.getMethods();*/
        /*try {
            Method method = PackageManager.class.getMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);

            try {
                method.invoke(getPackageManager(),Integer.MAX_VALUE,new MyIPackageDataObserver());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/
        /*for (Method method :methods){
            if (method.getName().equals("freeStorageAndNotify")){
                try {
                    method.invoke(getPackageManager(),Integer.MAX_VALUE,new MyIPackageDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }*/
        Toast.makeText(CleanCacheActivity.this,"全部清除",Toast.LENGTH_SHORT).show();
    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }
}
