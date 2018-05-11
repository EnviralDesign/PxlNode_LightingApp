package frost.com.homelighting;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import frost.com.homelighting.dependencyinjection.ApplicationComponent;
import frost.com.homelighting.dependencyinjection.ApplicationModule;


import frost.com.homelighting.dependencyinjection.DaggerApplicationComponent;
import frost.com.homelighting.dependencyinjection.DatabaseModule;
import frost.com.homelighting.dependencyinjection.RepositoryModule;
import frost.com.homelighting.util.Constants;

public class HomeLightingApplication extends Application implements Constants {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Delete design configurations that may have been saved.
        SharedPreferences Prefs = getSharedPreferences(DESIGN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = Prefs.edit();
        prefsEditor.putString(DESIGN_CONFIGURATION, null);
        prefsEditor.apply();


        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .repositoryModule(new RepositoryModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
