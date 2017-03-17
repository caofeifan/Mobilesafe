package com.cff.mobilesafe.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.cff.mobilesafe.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/16.
 */

public class ItemTitleViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_title)
    public TextView tv_title;

    public ItemTitleViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
