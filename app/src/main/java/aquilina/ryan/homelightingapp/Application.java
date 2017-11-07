/*
 * Created by Ryan Aquilina on 10/18/17 5:04 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/4/17 10:07 AM
 */

package aquilina.ryan.homelightingapp;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.ScannedDevices;
import aquilina.ryan.homelightingapp.ui.design_mode.DesignConfiguration;
import aquilina.ryan.homelightingapp.utils.Constants;

public class Application extends android.app.Application {

    private ScannedDevices mScannedDevices;
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!

        SharedPreferences Prefs = getSharedPreferences(Constants.DESIGN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = Prefs.edit();
        prefsEditor.putString(Constants.DESIGN_CONFIGURATION, null);
        prefsEditor.apply();
    }

    public void setScannedDevices(ScannedDevices mScannedDevices) {
        this.mScannedDevices = mScannedDevices;
    }

    public ScannedDevices getScannedDevices() {
        return mScannedDevices;
    }

    public Device getDeviceById(int id){
        if(mScannedDevices != null){
            if(!mScannedDevices.getDevicesList().isEmpty()){
                for (Device device: mScannedDevices.getDevicesList()) {
                    if(id == device.getId()){
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
