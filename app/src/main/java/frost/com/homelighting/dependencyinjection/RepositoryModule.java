package frost.com.homelighting.dependencyinjection;

import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import frost.com.homelighting.Repository;
import frost.com.homelighting.db.AppDatabase;
import frost.com.homelighting.db.dao.DeviceDao;
import frost.com.homelighting.db.dao.GroupDao;
import frost.com.homelighting.db.dao.GroupDetailsDao;
import frost.com.homelighting.db.dao.MacroDao;
import frost.com.homelighting.db.dao.MacroDetailsDao;
import frost.com.homelighting.db.dao.OnlineDevicesDao;
import frost.com.homelighting.db.dao.PresetAndGroupDetailsDao;
import frost.com.homelighting.db.dao.PresetDao;
import frost.com.homelighting.db.dao.PresetDetailsDao;
import frost.com.homelighting.viewmodel.CustomViewModelFactory;
import frost.com.homelighting.webservice.NodeMCUAPI;

@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public Repository provideRepository(AppDatabase appDatabase, DeviceDao deviceDao, GroupDao groupDao, MacroDao macroDao, PresetDao presetDao, GroupDetailsDao groupDetailsDao, MacroDetailsDao macroDetailsDao, PresetDetailsDao presetDetailsDao, OnlineDevicesDao onlineDevicesDao, PresetAndGroupDetailsDao presetAndGroupDetailsDao){
        return new Repository(appDatabase, deviceDao, groupDao, macroDao, presetDao, groupDetailsDao, macroDetailsDao, presetDetailsDao, onlineDevicesDao, presetAndGroupDetailsDao);
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(Repository repository){
        return new CustomViewModelFactory(repository);
    }
}