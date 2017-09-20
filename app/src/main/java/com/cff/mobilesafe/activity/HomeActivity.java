package com.cff.mobilesafe.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.service.LocationService;
import com.cff.mobilesafe.service.RocketService;
import com.cff.mobilesafe.utils.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页面
 */
public class HomeActivity extends BaseActivity {
    @BindView(R.id.gvHome)
    GridView gvHome;

    private String[] mItems = new String[]{"手机防盗","通信卫士","软件管理","进程管理","流量统计","手机杀毒","缓存管理","高级工具","设置中心"};
    private int[] mPicItems = new int[]{R.mipmap.home_safe,R.mipmap.home_callmsgsafe,R.mipmap.home_apps,R.mipmap.home_taskmanager,
            R.mipmap.home_netmanager,R.mipmap.home_trojan,R.mipmap.home_sysoptimize,R.mipmap.home_tools, R.mipmap.home_settings};
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        //检查权限，android6.0必须检查
        verifyStoragePermissions(HomeActivity.this, Manifest.permission.SEND_SMS);
        verifyStoragePermissions(HomeActivity.this, Manifest.permission.RECEIVE_SMS);
        verifyStoragePermissions(HomeActivity.this, Manifest.permission.CALL_PHONE);
        verifyStoragePermissions(HomeActivity.this, Manifest.permission.READ_CALL_LOG);
        verifyStoragePermissions(HomeActivity.this, Manifest.permission.WRITE_CALL_LOG);
        verifyStoragePermissions(HomeActivity.this, Manifest.permission.READ_SMS);
        mPref = getSharedPreferences("config",MODE_PRIVATE);

        startService(new Intent(HomeActivity.this, LocationService.class));

        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 8:
                        //进入设置中心
                        enterTargetActivity(HomeActivity.this,SettingActivity.class);
                        break;
                    case 7:
                        //进入高级工具页面
                        enterTargetActivity(HomeActivity.this,AToolsActivity.class);
                        break;
                    case 6:
                        enterTargetActivity(HomeActivity.this,CleanCacheActivity.class);
                        break;
                    case 5:
                        //进入手机杀毒页面
                        enterTargetActivity(HomeActivity.this,AntivirusActivity.class);
                        break;
                    case 4:break;
                    case 3:
                        //进入软件管理页面
                        enterTargetActivity(HomeActivity.this,TaskManagerActivity.class);
                        break;
                    case 2:
                        //进入软件管理页面
                        enterTargetActivity(HomeActivity.this,AppManagerActivity.class);
                        break;
                    case 1:
                        //进入通信卫士页面
                        enterTargetActivity(HomeActivity.this,CallSafeActivity.class);
                        break;
                    case 0:
                        showPwdDialog();
                        break;
                    default:break;


                }
            }
        });
    }
    private void showPwdDialog(){
        //判断是否有密码
        String savedPwd = mPref.getString("pwd",null);
        if (TextUtils.isEmpty(savedPwd)){
            showPwdSetDialog();
        }else {
            showPwdInputDialog();
        }

    }
    //密码设置dialog
    private void showPwdSetDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View v = View.inflate(this,R.layout.dailog_setpawd,null);
        final EditText etPwd = (EditText) v.findViewById(R.id.edPwd);
        final EditText etPwdConfirm = (EditText) v.findViewById(R.id.edPwdConfirm);
        Button btOk = (Button) v.findViewById(R.id.btOk);
        Button btCancel = (Button) v.findViewById(R.id.btCancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPwd.getText().toString();
                String pwdConfirm = etPwdConfirm.getText().toString();
                if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(pwdConfirm)){
                    if (TextUtils.equals(pwd,pwdConfirm)){
                        mPref.edit().putString("pwd", CommonUtil.encodeByMd5(pwd)).commit();
                        dialog.dismiss();
                        enterTargetActivity(HomeActivity.this,LostFindActivity.class);
                    }else {
                        Toast.makeText(HomeActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                        etPwd.setText("");
                        etPwdConfirm.setText("");
                    }
                }else {
                    Toast.makeText(HomeActivity.this,"不能为空！",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //设置View，边距为零
        dialog.setView(v,0,0,0,0);
        dialog.show();
    }

    private void showPwdInputDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View v = View.inflate(this,R.layout.dialog_inputpwd,null);
        final EditText etInputPwd = (EditText) v.findViewById(R.id.edInputPwd);
        Button btOk = (Button) v.findViewById(R.id.btInputOk);
        Button btCancel = (Button) v.findViewById(R.id.btInputCancel);
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savedPwd = mPref.getString("pwd",null);
                String inputPwd = etInputPwd.getText().toString();
                if (TextUtils.isEmpty(inputPwd)){
                    Toast.makeText(HomeActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    if (TextUtils.equals(CommonUtil.encodeByMd5(inputPwd),savedPwd)){
                        dialog.dismiss();
                        enterTargetActivity(HomeActivity.this,LostFindActivity.class);
                    }else {
                        Toast.makeText(HomeActivity.this,"密码错误，请再次输入",Toast.LENGTH_SHORT).show();
                        etInputPwd.setText("");
                    }
                }
            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //设置View，边距为零
        dialog.setView(v,0,0,0,0);
        dialog.show();
    }
    class HomeAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = View.inflate(HomeActivity.this,R.layout.home_list_item,null);
            ImageView ivItem = (ImageView) v.findViewById(R.id.ivItem);
            TextView tvItem = (TextView) v.findViewById(R.id.tvItem);
            tvItem.setText(mItems[position]);
            ivItem.setImageResource(mPicItems[position]);
            return v;
        }
    }
}
