package com.cff.mobilesafe.domain;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/27.
 */

public class VirusInfo {
    String md5;
    int type;
    String name;
    String desc;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VirusInfo{" +
                "md5='" + md5 + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
