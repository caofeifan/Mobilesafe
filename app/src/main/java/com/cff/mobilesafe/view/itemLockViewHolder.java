package com.cff.mobilesafe.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/28.
 */

public class itemLockViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.iv_icon)
    public ImageView iv_icon;

    @BindView(R.id.tv_appName)
    public TextView tv_appName;
    @BindView(R.id.iv_lock)
    public ImageView iv_lock;
    public itemLockViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
