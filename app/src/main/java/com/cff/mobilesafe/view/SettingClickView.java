package com.cff.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by caofeifan on 2017/2/24.
 * 自定义的组合空间
 */

public class SettingClickView extends RelativeLayout {
    private static final String TAG = SettingClickView.class.getSimpleName();
    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.cbState)
    CheckBox cbState;
    View view;

    private String mTitle;
    private String mDescOn;
    private String mDescOff;
    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = attrs.getAttributeName(i);
            String attributeValue = attrs.getAttributeValue(i);
            Log.i(TAG, "SettingItemView: "+attributeName+"="+attributeValue);
        }
        mTitle = attrs.getAttributeValue(NAMESPACE,"title");//根据属性名称获取属性值
        mDescOn = attrs.getAttributeValue(NAMESPACE,"desc_on");//根据属性名称获取属性值
        mDescOff = attrs.getAttributeValue(NAMESPACE,"desc_off");//根据属性名称获取属性值
        initView();
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        int attributeCount = attrs.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = attrs.getAttributeName(i);
            String attributeValue = attrs.getAttributeValue(i);
            Log.i(TAG, "SettingItemView: "+attributeName+"="+attributeValue);
        }
        mTitle = attrs.getAttributeValue(NAMESPACE,"title");//根据属性名称获取属性值
        mDescOn = attrs.getAttributeValue(NAMESPACE,"desc_on");//根据属性名称获取属性值
        mDescOff = attrs.getAttributeValue(NAMESPACE,"desc_off");//根据属性名称获取属性值
        initView();
    }

    public SettingClickView(Context context) {
        super(context);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView(){
        //将自定义的布局文件设置给当前settingItemView
        view = View.inflate(getContext(), R.layout.view_setting_item,this);
        ButterKnife.bind(view);
        setTvTitle(mTitle);
    }

    public void setTvTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTvDesc(String desc) {
        tvDesc.setText(desc);
    }

    public void setChecked(boolean state) {
        cbState.setChecked(state);
        if (state){
            setTvDesc(mDescOn);
        }else {
            setTvDesc(mDescOff);
        }
    }

    public boolean isChecked(){
        return cbState.isChecked();
    }
}
