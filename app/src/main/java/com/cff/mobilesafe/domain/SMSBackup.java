package com.cff.mobilesafe.domain;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/23.
 */

public class SMSBackup {
    String address;
    String date;
    String type;
    String body;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
