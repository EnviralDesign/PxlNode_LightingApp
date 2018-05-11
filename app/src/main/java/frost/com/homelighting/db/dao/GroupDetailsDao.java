package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupDetailsEntity;

@Dao
public interface GroupDetailsDao {

    @Query("SELECT device_ip FROM group_details WHERE group_id = :groupId")
    List<String> loadDevicesIpAddress(int groupId);

    @Query("SELECT d.* FROM devices d JOIN group_details gd ON gd.device_ip = d.ip_address AND gd.group_id = :groupId")
    LiveData<List<DeviceEntity>> loadDevicesInGroup(int groupId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long saveGroupDetails(GroupDetailsEntity groupDetailsEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> saveAllGroupDetails(List<GroupDetailsEntity> groupDetailsEntities);

    @Query("SELECT d.name FROM devices d JOIN group_details gd ON gd.device_ip = d.ip_address WHERE gd.group_id = :groupId")
    List<String> loadGroupDeviceNames(int groupId);
}
