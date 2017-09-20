package com.cff.mobilesafe.domain;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 在此写用途
 * Created by caofeifan on 2017/4/1.
 */

public class AppCache implements Parcelable{
    Bitmap icon;
    String appName;
    long appSize;

    public AppCache() {
    }

    public AppCache(Bitmap icon, String appName, long appSize) {
        this.icon = icon;
        this.appName = appName;
        this.appSize = appSize;
    }

    protected AppCache(Parcel in) {
        icon = in.readParcelable(Bitmap.class.getClassLoader());
        appName = in.readString();
        appSize = in.readLong();
    }

    public static final Creator<AppCache> CREATOR = new Creator<AppCache>() {
        @Override
        public AppCache createFromParcel(Parcel in) {
            return new AppCache(in);
        }

        @Override
        public AppCache[] newArray(int size) {
            return new AppCache[size];
        }
    };

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    @Override
    public String toString() {
        return "AppCache{" +
                " appName='" + appName + '\'' +
                ", appSize=" + appSize +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeLong(appSize);
        dest.writeParcelable(icon,flags);
    }

    public AppCache readFromParcel(Parcel in){
        return new AppCache(in);
    }
}
