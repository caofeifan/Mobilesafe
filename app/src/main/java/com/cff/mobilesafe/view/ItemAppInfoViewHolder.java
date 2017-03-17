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
 * Created by caofeifan on 2017/3/15.
 */

public class ItemAppInfoViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.iv_icon)
    public ImageView iv_icon;

    @BindView(R.id.tv_appName)
    public TextView tv_appName;

    @BindView(R.id.tv_rom)
    public TextView tv_rom;

    @BindView(R.id.tv_appSize)
    public TextView tv_appSize;

    public ItemAppInfoViewHolder(View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
