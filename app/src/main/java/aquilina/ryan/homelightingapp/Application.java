package aquilina.ryan.homelightingapp;

import android.content.SharedPreferences;
import android.content.res.Configuration;

import aquilina.ryan.homelightingapp.utils.Constants;

/**
 * Created by SterlingRyan on 9/7/2017.
 */

public class Application extends android.app.Application {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        SharedPreferences preferences = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(Constants.GROUP_OF_SINGLE_DEVICES);
        editor.apply();
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
