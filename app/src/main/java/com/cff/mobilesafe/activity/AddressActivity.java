package com.cff.mobilesafe.activity;

import android.media.MediaCodec;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.dao.AddressDao;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressActivity extends BaseActivity {
    @BindView(R.id.etNum)
    EditText etNum;
    @BindView(R.id.tvResult)
    TextView tvResult;

    private static final String TAG = AddressActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_address);
        ButterKnife.bind(this);

        //监听EditText变化
        etNum.addTextChangedListener(new TextWatcher() {
            //变化前的回调
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            //变化时的回调
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNum = etNum.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNum)){
                    AddressDao addressDao = new AddressDao(AddressActivity.this);
                    String address = addressDao.getAddress(phoneNum);
                    tvResult.setText(address);
                }else {
                    showToast("电话号码不能为空");
                }
            }
            //变化结束后的回调
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //开始查询
    public void query(View view){
        String phoneNum = etNum.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNum)){
            AddressDao addressDao = new AddressDao(this);
            String address = addressDao.getAddress(phoneNum);
            tvResult.setText(address);
        }else {
            showToast("电话号码不能为空");
        }
    }
}
