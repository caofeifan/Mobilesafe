package com.cff.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

/**
 * 在此写用途
 * Created by caofeifan on 2017/3/1.
 */

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    SharedPreferences mPref;
    LocationManager lm;
    MylocationListener myLocationListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPref = getSharedPreferences("config", Context.MODE_PRIVATE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//是否允许付费
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria,true);
        myLocationListener = new MylocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != 
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != 
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(bestProvider, 3000, 5, myLocationListener);
        Log.i(TAG, "onCreate: -----------------------------");
    }

    class MylocationListener implements  LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: ===============================");
            float longtitude = (float) location.getLongitude();//经度
            float latitude = (float) location.getLatitude();//纬度
            mPref.edit().putString("location", latitude + "," + longtitude).commit();
            Log.i(TAG, "onLocationChanged: " + "GPS信息已经写入到SharedPreference:" + longtitude + "," + latitude);
            stopSelf();//停掉Service
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: ===========");
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
