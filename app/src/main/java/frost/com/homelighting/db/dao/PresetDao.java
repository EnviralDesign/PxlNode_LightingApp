package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.PresetEntity;

@Dao
public interface PresetDao {
    @Query("SELECT * FROM preset")
    LiveData<List<PresetEntity>> loadAllPresets();

    @Query("SELECT id FROM preset")
    List<Integer> loadAllPresetsIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PresetEntity> presets);

    @Query("SELECT * FROM preset WHERE id = :presetId")
    LiveData<PresetEntity> loadPreset(int presetId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertPreset(PresetEntity preset);

    @Delete
    void deletePreset(PresetEntity preset);

    @Query("DELETE FROM preset WHERE id = :presetId")
    void deletePreset(int presetId);
}