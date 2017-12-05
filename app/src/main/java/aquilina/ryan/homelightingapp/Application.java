/*
 * Created by Ryan Aquilina on 10/18/17 5:04 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/4/17 10:07 AM
 */

package aquilina.ryan.homelightingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.OnlineDevices;
import aquilina.ryan.homelightingapp.utils.Constants;

public class Application extends android.app.Application {

    private OnlineDevices mOnlineDevices;
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences Prefs = getSharedPreferences(Constants.DESIGN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = Prefs.edit();
        prefsEditor.putString(Constants.DESIGN_CONFIGURATION, null);
        prefsEditor.apply();

        // Check if the application version is installed for the first time
        Prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        if (Prefs.getBoolean("firstrun", true)) {
           // Remove any data that might interfere.
            Prefs.getAll().clear();
            Prefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            Prefs.getAll().clear();
            Prefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, Context.MODE_PRIVATE);
            Prefs.getAll().clear();
            Prefs.edit().putBoolean("firstrun", false).apply();
        }
    }

    public void setScannedDevices(OnlineDevices mOnlineDevices) {
        this.mOnlineDevices = mOnlineDevices;
    }

    public OnlineDevices getScannedDevices() {
        return mOnlineDevices;
    }

    public Device getDeviceByIP(String ip){
        if(mOnlineDevices != null){
            if(!mOnlineDevices.getDevicesList().isEmpty()){
                for (Device device: mOnlineDevices.getDevicesList()) {
                    if(ip.equals(device.getIpAddress())){
                        return device;
                    }
                }
            }
        }
        return null;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
