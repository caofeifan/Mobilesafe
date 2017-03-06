package com.cff.mobilesafe.activity;

import android.Manifest;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Setup3Activity extends BaseActivity {
    private static final String TAG = Setup3Activity.class.getSimpleName();
    public static final int CODE_SELECT_CONTACT = 284;
    private GestureDetector mDetector;
    @BindView(R.id.btGetContacts)
    Button btGetContacts;
    @BindView(R.id.etPhoneNum)
    EditText etPhoneNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_setup3);
        ButterKnife.bind(this);
        mDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float e1X = e1.getRawX();
                float e2X = e2.getRawX();
                //上一页
                if (e2X-e1X>200) showPrePaeg();
                //下一页
                if (e1X-e2X>200) showNextPage();


                return super.onFling(e1, e2, velocityX, velocityY);

            }
        });
        String safePhone = mPref.getString("safePhone","");
        etPhoneNum.setText(safePhone);
    }

    public void previous(View view){
        showPrePaeg();
    }

    public void next(View view){
        showNextPage();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void showNextPage(){
        if (TextUtils.isEmpty(etPhoneNum.getText().toString().trim())){
            Toast.makeText(this,"安全号码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        mPref.edit().putString("safePhone",etPhoneNum.getText().toString().trim()).commit();
        enterTargetActivity(Setup3Activity.this,Setup4Activity.class);
        finish();
        //设置页面切换动画
        overridePendingTransition(R.anim.next_tran_in,R.anim.next_tran_out);
    }

    private void showPrePaeg(){
        enterTargetActivity(Setup3Activity.this,Setup2Activity.class);
        finish();
        //设置页面切换动画
        overridePendingTransition(R.anim.pre_tran_in,R.anim.pre_tran_out);
    }

    @OnClick(R.id.btGetContacts)
    public void getContacts(){
        verifyStoragePermissions(Setup3Activity.this, Manifest.permission.READ_CONTACTS);
        enterTargetActivityForResult(Setup3Activity.this,ContactActivity.class,CODE_SELECT_CONTACT);
    }

    /**
     * 获取返回结果
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requestCode){
            switch (requestCode){
                case CODE_SELECT_CONTACT:
                    //获取结果，并显示现在textView中
                    String str = data.getStringExtra("phoneNum");
                    System.out.println("================="+str);
                    if (!TextUtils.isEmpty(str)){
                        etPhoneNum.setText(str);
                        mPref.edit().putString("safePhone",str).commit();
                    }

                    break;
                default:break;
            }
        }
    }
}
