package frost.com.homelighting.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.VisibleForTesting;

import frost.com.homelighting.db.dao.DeviceDao;
import frost.com.homelighting.db.dao.GroupDao;
import frost.com.homelighting.db.dao.GroupDetailsDao;
import frost.com.homelighting.db.dao.MacroDao;
import frost.com.homelighting.db.dao.MacroDetailsDao;
import frost.com.homelighting.db.dao.OnlineDevicesDao;
import frost.com.homelighting.db.dao.PresetAndGroupDetailsDao;
import frost.com.homelighting.db.dao.PresetDao;
import frost.com.homelighting.db.dao.PresetDetailsDao;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupDetailsEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.MacroDetailsEntity;
import frost.com.homelighting.db.entity.MacroEntity;
import frost.com.homelighting.db.entity.OnlineDeviceEntity;
import frost.com.homelighting.db.entity.PresetAndGroupDetailsEntity;
import frost.com.homelighting.db.entity.PresetDetailsEntity;
import frost.com.homelighting.db.entity.PresetEntity;
import frost.com.homelighting.model.PresetAndGroupDetails;

@Database(entities = {
                DeviceEntity.class,
                GroupDetailsEntity.class,
                GroupEntity.class,
                MacroDetailsEntity.class,
                MacroEntity.class,
                PresetDetailsEntity.class,
                PresetEntity.class,
                OnlineDeviceEntity.class,
                PresetAndGroupDetailsEntity.class
            },
            version = 10,
            exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase{
    @VisibleForTesting
    public static final String DATABASE_NAME = "home-lighting-db";

    public abstract DeviceDao deviceDao();

    public abstract GroupDetailsDao groupDetailsDao();

    public abstract GroupDao groupDao();

    public abstract MacroDetailsDao macroDetailsDao();

    public abstract MacroDao macroDao();

    public abstract PresetDetailsDao presetDetailsDao();

    public abstract PresetDao presetDao();

    public abstract OnlineDevicesDao onlineDevicesDao();

    public abstract PresetAndGroupDetailsDao presetAndGroupDetailsDao();
}
