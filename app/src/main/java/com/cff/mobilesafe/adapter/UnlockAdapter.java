package com.cff.mobilesafe.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.domain.AppLock;
import com.cff.mobilesafe.fragment.UnLockFragment;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;
import com.cff.mobilesafe.utils.ImageUtils;
import com.cff.mobilesafe.view.itemLockViewHolder;

import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/28.
 */

public class UnlockAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = UnlockAdapter.class.getSimpleName();
    List<AppLock> appLocks;
    Context context;
    OnRecyclerViewItemClickListener mOnItemClickListener;

    public UnlockAdapter(Context context, List<AppLock> appLocks){
        this.appLocks = appLocks;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_lock, null);
        return new itemLockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        AppLock appLock = appLocks.get(position);
        ((itemLockViewHolder) holder).tv_appName.setText(appLock.getAppName());
        ((itemLockViewHolder) holder).iv_icon.setBackground(new BitmapDrawable(appLock.getAppIcon()));
        ((itemLockViewHolder) holder).iv_lock.setImageResource(R.drawable.iv_lock_selector);
        final View view= ((itemLockViewHolder) holder).itemView;
        ((itemLockViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (mOnItemClickListener != null){
                    //初始化一个位移动画
                    TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
                    animation.setDuration(500);
                    view.startAnimation(animation);

                    mOnItemClickListener.onItemClick(v,String.valueOf(position));


                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return appLocks.size();
    }

    /**
     * 数据变化
     * @param appLocks
     */
    public void notifyListChanged(List<AppLock> appLocks){
        this.appLocks = appLocks;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+appLocks.size());
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
