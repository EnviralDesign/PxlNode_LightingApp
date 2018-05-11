package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.DeviceEntity;

@Dao
public interface DeviceDao {
    @Query("SELECT * FROM devices")
    LiveData<List<DeviceEntity>> loadAllDevices();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertAll(List<DeviceEntity> devices);

    @Query("SELECT * FROM devices WHERE ip_address = :ipAddress")
    LiveData<DeviceEntity> loadDevice(String ipAddress);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertDevice(DeviceEntity device);

    @Delete
    void deleteDevice(DeviceEntity device);
}
