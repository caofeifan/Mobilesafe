package com.cff.mobilesafe.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/24.
 */

public class ItemTaskViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.iv_icon)
    public ImageView iv_icon;
    @BindView(R.id.tv_taskName)
    public TextView tv_taskName;
    @BindView(R.id.tv_memSize)
    public TextView tv_memSize;
    @BindView(R.id.cb_task)
    public CheckBox cb_task;

    public ItemTaskViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
