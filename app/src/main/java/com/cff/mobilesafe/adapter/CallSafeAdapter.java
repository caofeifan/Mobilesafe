package com.cff.mobilesafe.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.domain.BlackNumberInfo;
import com.cff.mobilesafe.listener.OnRecyclerViewItemClickListener;
import com.cff.mobilesafe.view.ItemLoadingViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/13.
 */

public class CallSafeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CallSafeAdapter.class.getSimpleName();
    private Context context;
    public static final int HEADER_VIEW = 0x00000111;
    public static final int LOADING_VIEW = 0x00000222;
    public static final int FOOTER_VIEW = 0x00000333;
    public static final int ITEM_VIEW = 0x00000444;
    public static final int EMPTY_VIEW = 0x00000555;
    List<BlackNumberInfo> list;

    private boolean isLoading;
    private AnimationDrawable animationDrawable;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;


    public CallSafeAdapter(Context context , List<BlackNumberInfo> list) {
        this.list = list;
        isLoading = true;
        this.context = context;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        final View view;
        switch (viewType) {
            case LOADING_VIEW:
                view = View.inflate(parent.getContext(), R.layout.item_loading, null);
                holder = new ItemLoadingViewHolder(view);
                break;
            case ITEM_VIEW:
                view = View.inflate(parent.getContext(), R.layout.item_call_safe2, null);
                holder = new ItemViewHolder(view);


                break;
        }

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position<=list.size()-1){
            BlackNumberInfo blackNumberInfo = list.get(position);
            ((ItemViewHolder) holder).tv_name.setText(blackNumberInfo.getName());
            ((ItemViewHolder) holder).tv_number.setText(blackNumberInfo.getNumber());
            ((ItemViewHolder) holder).tv_mode.setText(blackNumberInfo.getMode());
            ((ItemViewHolder) holder).itemView.setTag(blackNumberInfo.getNumber());
            ((ItemViewHolder) holder).iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //调用Dao
                    deleteData(position);
                    Toast.makeText(context,"成功删除",Toast.LENGTH_LONG).show();
                }
            });
        }else {

            animationDrawable = (AnimationDrawable)((ItemLoadingViewHolder) holder).iv_loading.getBackground();
            animationDrawable.start();
            Log.i(TAG, "onBindViewHolder: ------------"+animationDrawable+"---------"+list.size());
        }

    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount: ------------"+isLoading+"---------"+list.size());
        if (isLoading){
            return list.size()+1;
        }
        return list.size();

    }

    /**
     * 获取Item类型
     * @param position item位置
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position>list.size()-1){
            return LOADING_VIEW;
        }
        return ITEM_VIEW;

    }



    public void loadingFinished(){
        //停止动画

        /*animationDrawable.stop();*/
        isLoading = false;
        notifyDataSetChanged();
    }

    /**
     * add new data in to certain location
     *
     * @param position
     */
    public void addData(int position, List<BlackNumberInfo> data) {
        if (0 <= position && position < list.size()) {
            list.addAll(position, data);
            notifyItemRangeInserted(position, data.size());
        } else {
            throw new ArrayIndexOutOfBoundsException("inserted position most greater than 0 and less than data size");
        }
    }

    public boolean deleteData(int position){
        if (position>0&&position<list.size()){
            list.remove(position);
            notifyDataSetChanged();
            return true;
        }
        return false;

    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.tv_number)
        TextView tv_number;
        @BindView(R.id.tv_mode)
        TextView tv_mode;
        @BindView(R.id.iv_delete)
        ImageView iv_delete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        onRecyclerViewItemClickListener = listener;
    }

}
