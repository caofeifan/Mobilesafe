package com.cff.mobilesafe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.domain.TaskInfo;
import com.cff.mobilesafe.listener.OnItemTaskClickListener;
import com.cff.mobilesafe.view.ItemTaskViewHolder;
import com.cff.mobilesafe.view.ItemTitleViewHolder;

import java.util.List;

/**
 * 进程信息 adpter
 * Created by caofeifan on 2017/3/24.
 */

public class TaskMAnagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = TaskMAnagerAdapter.class.getSimpleName();

    private static final int ITEM_TITLE = 0001;
    private static final int ITEM_CONTENT = 0002;

    List<TaskInfo>[] taskInfos;
    Context context;
    OnItemTaskClickListener onItemTaskClickListener;

    public TaskMAnagerAdapter(Context context, List<TaskInfo>... taskInfos) {
        this.context = context;
        this.taskInfos = taskInfos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        if (viewType == ITEM_TITLE){
            view = View.inflate(context, R.layout.item_title, null);
            holder = new ItemTitleViewHolder(view);
        }else {
            view = View.inflate(parent.getContext(), R.layout.item_taskinfo, null);
            holder = new ItemTaskViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //判断holder类型
        if (position == 0 || position == taskInfos[0].size()+1){//title类型
            if (position == 0){
                ((ItemTitleViewHolder) holder).tv_title.setText("用户进程("+taskInfos[0].size()+")");
            }else {
                ((ItemTitleViewHolder) holder).tv_title.setText("系统进程("+taskInfos[1].size()+")");
            }
        }else {
            TaskInfo taskInfo;
            if (position<taskInfos[0].size()+1){//用户进程
                taskInfo = taskInfos[0].get(position - 1);
            }else {//系统进程
                taskInfo = taskInfos[1].get(position - taskInfos[0].size()-2);
            }
            ((ItemTaskViewHolder) holder).iv_icon.setImageDrawable(taskInfo.getIcon());
            ((ItemTaskViewHolder) holder).tv_taskName.setText(taskInfo.getAppName());
            ((ItemTaskViewHolder) holder).tv_memSize.setText(taskInfo.getMemeorySize());
            if (taskInfo.getPackageName().equals(context.getPackageName())){
                taskInfo.setChecked(false);
                ((ItemTaskViewHolder) holder).cb_task.setVisibility(View.INVISIBLE);

            }

            /**
             * 点击事件
             */
            ((ItemTaskViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemTaskClickListener != null){
                        onItemTaskClickListener.onItemClick(v,position+"");
                    }

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return taskInfos[0].size()+taskInfos[1].size()+2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == taskInfos[0].size()+1){
            return ITEM_TITLE;
        }
        return ITEM_CONTENT;
    }

    public void setOnItemClickListener(OnItemTaskClickListener listener) {
        onItemTaskClickListener = listener;
    }

    /**
     * 数据变化
     * @param taskInfos
     */
    public void notifyDataChanged(List<TaskInfo>... taskInfos){
        for (int i = 0;i<taskInfos.length;i++){
            this.taskInfos[i] = taskInfos[i];
        }
        notifyDataSetChanged();
    }
}
