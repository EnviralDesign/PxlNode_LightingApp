package aquilina.ryan.homelightingapp;

import com.google.gson.Gson;

import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.ScannedDevices;
import aquilina.ryan.homelightingapp.utils.Constants;

/**
 * Created by SterlingRyan on 9/7/2017.
 */

public class Application extends android.app.Application {

    private ScannedDevices mScannedDevices;
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
    }

    public ScannedDevices getmScannedDevices() {
        return mScannedDevices;
    }

    public void setmScannedDevices(ScannedDevices mScannedDevices) {
        this.mScannedDevices = mScannedDevices;
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
}
