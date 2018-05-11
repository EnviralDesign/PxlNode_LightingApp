package frost.com.homelighting.dependencyinjection;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import frost.com.homelighting.HomeLightingApplication;

@Module
public class ApplicationModule {
    private final HomeLightingApplication application;

    public ApplicationModule(HomeLightingApplication application){
        this.application = application;
    }

    @Provides
    HomeLightingApplication provideHomeLightingApplication(){
        return application;
    }

    @Provides
    Application provideApplication(){
        return application;
    }
}
