package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.MacroDetailsEntity;
import frost.com.homelighting.db.entity.PresetEntity;

@Dao
public interface MacroDetailsDao {
    @Query("SELECT preset_id FROM macro_details WHERE macro_id = :macroId")
    List<Integer> loadPresetsIds(int macroId);

    @Query("SELECT macro_id FROM macro_details WHERE preset_id = :presetId")
    List<Integer> loadMacrosIds(int presetId);

    @Query("SELECT p.* FROM preset p JOIN macro_details md ON md.preset_id = p.id WHERE md.macro_id = :macroId")
    LiveData<List<PresetEntity>> loadPresetsInMacro(int macroId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMacroDetails(List<MacroDetailsEntity> macroDetailsEntities);

    @Query("DELETE FROM macro_details WHERE macro_id = :macroId")
    void deleteMacroDetails(int macroId);

    @Query("SELECT p.name FROM preset p JOIN macro_details md ON md.preset_id = p.id WHERE md.macro_id = :macroId")
    List<String> loadMacroPresetNames(int macroId);
}
