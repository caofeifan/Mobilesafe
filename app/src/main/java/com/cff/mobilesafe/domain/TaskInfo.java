package com.cff.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/24.
 */

public class TaskInfo {

    private Drawable icon;
    private String packageName;
    private String appName;
    private String memeorySize;

    /**
     * 是否是用户进程
     * @return
     */
    private boolean userApp;
    /**
     * 是否被勾选
     */
    private boolean checked;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMemeorySize() {
        return memeorySize;
    }

    public void setMemeorySize(String memeorySize) {
        this.memeorySize = memeorySize;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "icon=" + icon +
                ", packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", memeorySize='" + memeorySize + '\'' +
                ", userApp=" + userApp +
                ", checked=" + checked +
                '}';
    }
}
