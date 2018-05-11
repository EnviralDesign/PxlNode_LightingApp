package frost.com.homelighting.dependencyinjection;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import frost.com.homelighting.MainActivity;
import frost.com.homelighting.ui.macros.MacroFragment;
import frost.com.homelighting.ui.presets.PresetFragment;
import frost.com.homelighting.ui.configuration.ConfigurationFragment;
import frost.com.homelighting.ui.designmode.DesignFragment;
import frost.com.homelighting.ui.groups.GroupFragment;
import frost.com.homelighting.ui.lighting.LightingFragment;
import frost.com.homelighting.ui.scan.ScanFragment;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class, RepositoryModule.class})
public interface ApplicationComponent {

    void inject(GroupFragment groupFragment);
    void inject(MacroFragment macroFragment);
    void inject(PresetFragment presetFragment);
    void inject(ScanFragment scanFragment);
    void inject(DesignFragment designFragment);
    void inject(MainActivity mainActivity);
    void inject(LightingFragment lightingFragment);
    void inject(ConfigurationFragment configurationFragment);

    Application application();
}
