package com.cff.mobilesafe.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.domain.AppCache;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;
import com.cff.mobilesafe.utils.ImageUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 在此写用途
 * Created by caofeifan on 2017/4/1.
 */

public class CleanCacheAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context context;
    public List<AppCache> apps;
    public OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

    public CleanCacheAdapter(Context context,List<AppCache> apps){
        this.context = context;
        this.apps = apps;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_clean_cache, null);
        return new CleanCacheViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AppCache appCache = apps.get(position);
        ((CleanCacheViewHolder)holder).iv_icon.setImageDrawable(new BitmapDrawable(appCache.getIcon()));
        ((CleanCacheViewHolder)holder).tv_appName.setText(appCache.getAppName());
        ((CleanCacheViewHolder)holder).tv_cacheSize.setText(Formatter.formatFileSize(context,appCache.getAppSize()));
        ((CleanCacheViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRecyclerViewItemClickListener != null){
                    onRecyclerViewItemClickListener.onItemClick(v,String.valueOf(position));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener){
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public void notifyDataChanged(List<AppCache> apps){
        this.apps = apps;
        notifyDataSetChanged();
    }



    class CleanCacheViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_icon)
        ImageView iv_icon;
        @BindView(R.id.tv_appName)
        TextView tv_appName;
        @BindView(R.id.tv_cacheSize)
        TextView tv_cacheSize;
        @BindView(R.id.iv_clean)
        ImageView iv_clean;

        public CleanCacheViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
