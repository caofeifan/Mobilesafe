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
 * Created by caofeifan on 2017/3/14.
 */

public class ItemLoadingViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_loading)
    public TextView tv_loading;
    @BindView(R.id.iv_loading)
    public ImageView iv_loading;

    public ItemLoadingViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
