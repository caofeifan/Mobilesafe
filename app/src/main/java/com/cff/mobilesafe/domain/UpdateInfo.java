package com.cff.mobilesafe.domain;

/**
 * Created by 80755 on 2017/2/24.
 */

public class UpdateInfo {
    private String versionName;
    private int versionCode;
    private String description;
    private String downloadUrl;

    public UpdateInfo(String versionName, int versionCode, String description, String downloadUrl) {
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.description = description;
        this.downloadUrl = downloadUrl;
    }

    public UpdateInfo() {}

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
