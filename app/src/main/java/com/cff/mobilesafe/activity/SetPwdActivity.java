package com.cff.mobilesafe.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetPwdActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.bt_0)
    Button bt_0;
    @BindView(R.id.bt_1)
    Button bt_1;
    @BindView(R.id.bt_2)
    Button bt_2;
    @BindView(R.id.bt_3)
    Button bt_3;
    @BindView(R.id.bt_4)
    Button bt_4;
    @BindView(R.id.bt_5)
    Button bt_5;
    @BindView(R.id.bt_6)
    Button bt_6;
    @BindView(R.id.bt_7)
    Button bt_7;
    @BindView(R.id.bt_8)
    Button bt_8;
    @BindView(R.id.bt_9)
    Button bt_9;
    @BindView(R.id.bt_clear)
    Button bt_clear;
    @BindView(R.id.bt_delete)
    Button bt_delete;
    @BindView(R.id.bt_ok)
    Button bt_ok;
    private String packageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pwd);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        Intent intent = getIntent();

        if (intent != null) {
            packageName = intent.getStringExtra("packageName");
        }
        /**
         * 隐藏键盘
         */
        et_pwd.setInputType(InputType.TYPE_NULL);
        bt_0.setOnClickListener(this);
        bt_1.setOnClickListener(this);
        bt_2.setOnClickListener(this);
        bt_3.setOnClickListener(this);
        bt_4.setOnClickListener(this);
        bt_5.setOnClickListener(this);
        bt_6.setOnClickListener(this);
        bt_7.setOnClickListener(this);
        bt_8.setOnClickListener(this);
        bt_9.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        bt_clear.setOnClickListener(this);
        bt_ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String str = et_pwd.getText().toString();
        switch (v.getId()){
            case R.id.bt_0:
                et_pwd.setText(str+0);
                break;
            case R.id.bt_1:
                et_pwd.setText(str+1);
                break;
            case R.id.bt_2:
                et_pwd.setText(str+2);
                break;
            case R.id.bt_3:
                et_pwd.setText(str+3);
                break;
            case R.id.bt_4:et_pwd.setText(str+4);
                break;
            case R.id.bt_5:et_pwd.setText(str+5);
                break;
            case R.id.bt_6:et_pwd.setText(str+6);
                break;
            case R.id.bt_7:et_pwd.setText(str+7);
                break;
            case R.id.bt_8:et_pwd.setText(str+8);
                break;
            case R.id.bt_9:et_pwd.setText(str+9);
                break;
            case R.id.bt_clear:
                str = "";
                et_pwd.setText(str);
                break;
            case R.id.bt_delete:

                if (str.length()>=1){
                    String substring = str.substring(0, str.length() - 1);
                    et_pwd.setText(substring);
                }

                break;
            case R.id.bt_ok:
                String result = et_pwd.getText().toString();
                if ("123".equals(result)) {
                    // 如果密码正确。说明是自己人
                    /**
                     * 是自己家人。不要拦截他
                     */
                    System.out.println("密码输入正确");

                    Intent intent = new Intent();
                    // 发送广播。停止保护
                    intent.setAction("stopProtect");
                    // 跟狗说。现在停止保护短信
                    intent.putExtra("packageName", packageName);
                    sendBroadcast(intent);
                    /**
                     *
                     */

                    Toast.makeText(SetPwdActivity.this, "密码正确",Toast.LENGTH_SHORT).show();

                    finish();

                } else {
                    Toast.makeText(SetPwdActivity.this, "密码错误",Toast.LENGTH_SHORT).show();
                }

                break;
            default:break;




        }
    }
}
