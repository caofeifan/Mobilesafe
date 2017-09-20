package com.cff.mobilesafe.domain;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/28.
 */

public class AppLock implements Parcelable{
    String packageName;
    Bitmap appIcon;
    String appName;
    boolean isLock;

    public AppLock() {
    }

    public AppLock(String packageName, String appName, boolean isLock,Bitmap appIcon) {
        this.packageName = packageName;
        this.appName = appName;
        this.isLock = isLock;
        this.appIcon = appIcon;
    }

    protected AppLock(Parcel in) {
        packageName = in.readString();
        appName = in.readString();
        isLock = in.readByte() != 0;
    }


    public static final Creator<AppLock> CREATOR = new Creator<AppLock>() {
        @Override
        public AppLock createFromParcel(Parcel in) {
            return new AppLock(in);
        }

        @Override
        public AppLock[] newArray(int size) {
            return new AppLock[size];
        }
    };

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public Bitmap getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Bitmap appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "AppLock{" +
                "appName='" + appName + '\'' +
                ", isLock=" + isLock +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeParcelable(appIcon, flags);
        dest.writeString(appName);
        dest.writeByte((byte) (isLock ? 1 : 0));
    }

    public AppLock readFromParcel(Parcel in){
        return new AppLock(in);
    }
}
