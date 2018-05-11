package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.OnlineDeviceEntity;
import frost.com.homelighting.model.OnlineDevices;

@Dao
public interface OnlineDevicesDao {
    @Query("SELECT d.* FROM devices d JOIN online_devices od ON d.ip_address = od.ipAddress")
    LiveData<List<DeviceEntity>> loadOnlineDevices();

    @Query("SELECT d.* FROM devices d JOIN online_devices od ON d.ip_address = od.ipAddress AND od.ipAddress = :ipAddress")
    DeviceEntity loadOnlineDevice(String ipAddress);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveOnlineDevices(List<OnlineDeviceEntity> onlineDevices);

    @Query("DELETE FROM online_devices")
    void deleteOnlineDevices();
}
