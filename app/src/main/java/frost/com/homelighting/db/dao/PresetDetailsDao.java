package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupDetailsEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.PresetDetailsEntity;
import frost.com.homelighting.db.entity.PresetEntity;
import frost.com.homelighting.model.PresetDetails;

@Dao
public interface PresetDetailsDao {
    @Query("SELECT d.* FROM devices d JOIN preset_details pd ON pd.preset_id = d.ip_address AND pd.preset_id = :presetId")
    LiveData<List<DeviceEntity>> loadDevicesInPreset(int presetId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void savePresetDetails(PresetDetailsEntity presetDetailsEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<PresetDetailsEntity> presetDetails);

    @Query("DELETE FROM preset_details WHERE preset_id = :presetId")
    void deletePresetDetails(int presetId);

    @Query("SELECT pd.device_ip FROM preset_details pd WHERE pd.preset_id = :presetId")
    List<String> loadPresetDeviceIP(int presetId);

    @Query("SELECT d.name FROM devices d JOIN preset_details pd ON pd.device_ip = d.ip_address WHERE pd.preset_id = :presetId")
    List<String> loadPresetDeviceNames(int presetId);
}
