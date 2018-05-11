package frost.com.homelighting.dependencyinjection;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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

@Module
public class DatabaseModule {

    private final AppDatabase database;

    public DatabaseModule(Application application) {
        this.database = Room.databaseBuilder(
                application,
                AppDatabase.class,
                "AppDatabase.db"
        )
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Application application){
        return database;
    }

    @Provides
    @Singleton
    DeviceDao provideDeviceDao(){
        return database.deviceDao();
    }

    @Provides
    @Singleton
    GroupDao provideGroupDao(){
        return database.groupDao();
    }

    @Provides
    @Singleton
    GroupDetailsDao provideGroupDetailsDao(){
        return database.groupDetailsDao();
    }

    @Provides
    @Singleton
    MacroDao provideMacroDao(){
        return database.macroDao();
    }

    @Provides
    @Singleton
    MacroDetailsDao provideMacroDetialsDao(){
        return database.macroDetailsDao();
    }

    @Provides
    @Singleton
    PresetDao providePresetDao(){
        return database.presetDao();
    }

    @Provides
    @Singleton
    PresetDetailsDao providePresetDetailsDao(){
        return database.presetDetailsDao();
    }

    @Provides
    @Singleton
    OnlineDevicesDao provideOnlineDevicesDao(){
        return database.onlineDevicesDao();
    }

    @Provides
    @Singleton
    PresetAndGroupDetailsDao providePresetAndGroupDetailsDao(){
        return database.presetAndGroupDetailsDao();
    }
}
