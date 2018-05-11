package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.PresetAndGroupDetailsEntity;
import frost.com.homelighting.ui.lighting.PresetAndGroupNames;
@Dao
public interface PresetAndGroupDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPresetAndGroupDetails(PresetAndGroupDetailsEntity presetAndGroupDetailsEntity);

    @Query("SELECT pg.group_id FROM preset_and_group_details pg WHERE pg.group_id = :presetId")
    List<Integer> loadAllGroupIdsForPreset(int presetId);

    @Query("DELETE FROM preset_and_group_details WHERE group_id = :groupId")
    void deleteGroupPresetDetailEntities(int groupId);

    @Query("DELETE FROM preset_and_group_details WHERE preset_id= :presetId")
    void deletePresetGroupDetailEntities(int presetId);

    @Query("SELECT g.name FROM devices_group g JOIN preset_and_group_details gp ON gp.group_id = g.id WHERE gp.preset_id = :presetId")
    String loadPresetGroupName(int presetId);
}
