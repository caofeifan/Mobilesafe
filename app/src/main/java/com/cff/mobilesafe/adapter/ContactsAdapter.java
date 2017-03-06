package com.cff.mobilesafe.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cff.mobilesafe.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 在此写用途
 * Created by caofeifan on 2017/2/27.
 */

public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    List<Map<String,String>> list;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public ContactsAdapter(List<Map<String, String>> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact,null);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new ItemViewHolder(view);
    }

    /**
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Map<String,String> map = list.get(position);
        ((ItemViewHolder)holder).tvContactName.setText(map.get("name"));
        ((ItemViewHolder)holder).tvPhoneNum.setText(map.get("phoneNum0"));
        //将数据保存在itemView的Tag中，以便点击时进行获取
        ((ItemViewHolder)holder).itemView.setTag(position+"");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(v, (String) v.getTag());
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvContactName)
        TextView tvContactName;
        @BindView(R.id.tvPhoneNum)
        TextView tvPhoneNum;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
