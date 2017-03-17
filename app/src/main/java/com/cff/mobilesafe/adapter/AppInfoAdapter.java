package com.cff.mobilesafe.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.cff.mobilesafe.R;
import com.cff.mobilesafe.domain.AppInfo;
import com.cff.mobilesafe.listener.OnItemViewClickPopupWindowListener;
import com.cff.mobilesafe.utils.Global;
import com.cff.mobilesafe.utils.ImageUtils;
import com.cff.mobilesafe.view.ItemAppInfoViewHolder;
import com.cff.mobilesafe.view.ItemTitleViewHolder;

import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/15.
 */

public class AppInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = AppInfoAdapter.class.getSimpleName();
    private static final int ITEM_TITLE = 0001;
    private static final int ITEM_CONTENT = 0002;

    public List<AppInfo>[] list;
    private Context context;
    private OnItemViewClickPopupWindowListener popupWindowListener;
    AppInfo appInfo;

    public AppInfoAdapter(Context context,List<AppInfo>... list){
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case ITEM_TITLE:
                View view = View.inflate(context, R.layout.item_title, null);

                viewHolder = new ItemTitleViewHolder(view);
                break;
            case ITEM_CONTENT:
                View view1 = View.inflate(context, R.layout.item_app_info, null);

                viewHolder = new ItemAppInfoViewHolder(view1);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (position == 0){
            ((ItemTitleViewHolder) holder).tv_title.setText("用户程序("+list[0].size()+")");
        }else if (position == list[0].size()+1){
            ((ItemTitleViewHolder) holder).tv_title.setText("系统程序("+list[1].size()+")");
        }else {

            if (position <= list[0].size()) {
                appInfo = list[0].get(position-1);
                //设置图片大小
                Drawable drawable = appInfo.getIcon();
                Bitmap bitmap = ImageUtils.drawableToBitmap(drawable);
                Bitmap bitmap1 = ImageUtils.scaleBitmap(bitmap, Global.APP_ICON_WIDTH, Global.APP_ICON_WIDTH, true);
                ((ItemAppInfoViewHolder)holder).iv_icon.setBackground(new BitmapDrawable(context.getResources(),bitmap1));
                ((ItemAppInfoViewHolder) holder).tv_appName.setText(appInfo.getApkName().toString());
                ((ItemAppInfoViewHolder)holder).tv_appSize.setText(Formatter.formatFileSize(context,appInfo.getApkSize()));
                if (appInfo.isRom()){
                    ((ItemAppInfoViewHolder)holder).tv_rom.setText("手机内存");
                }else {
                    ((ItemAppInfoViewHolder)holder).tv_rom.setText("SD卡");
                }

            }else if(position>list[0].size()+1){
                appInfo = list[1].get(position-list[0].size()-2);
                //设置图片大小
                Drawable drawable = appInfo.getIcon();
                Bitmap bitmap = ImageUtils.drawableToBitmap(drawable);
                Bitmap bitmap1 = ImageUtils.scaleBitmap(bitmap, Global.APP_ICON_WIDTH, Global.APP_ICON_WIDTH, true);
                ((ItemAppInfoViewHolder)holder).iv_icon.setBackground(new BitmapDrawable(context.getResources(),bitmap1));
                ((ItemAppInfoViewHolder) holder).tv_appName.setText(appInfo.getApkName().toString());
                ((ItemAppInfoViewHolder)holder).tv_appSize.setText(Formatter.formatFileSize(context,appInfo.getApkSize()));
                if (appInfo.isRom()){
                    ((ItemAppInfoViewHolder)holder).tv_rom.setText("手机内存");
                }else {
                    ((ItemAppInfoViewHolder)holder).tv_rom.setText("SD卡");
                }

            }
            final View itemView = ((ItemAppInfoViewHolder)holder).itemView;
            ((ItemAppInfoViewHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindowListener != null){
                        popupWindowListener.onItemClick(itemView,position);
                        Log.i(TAG, "onClick: ---------------appInfo:"+position);
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        int length = list[0].size()+list[1].size()+2;
        Log.i(TAG, "getItemCount: -------------"+length);
        return length;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == list[0].size()+1){
            return ITEM_TITLE;
        }
        return ITEM_CONTENT;
    }

    public boolean deleteData(int position){
            list[0].remove(position);
            notifyDataSetChanged();
            return true;

    }

    public void setOnItemClickListener(OnItemViewClickPopupWindowListener listener) {
        popupWindowListener = listener;
    }
}
