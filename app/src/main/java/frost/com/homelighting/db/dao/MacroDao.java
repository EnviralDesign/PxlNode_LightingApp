package frost.com.homelighting.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import frost.com.homelighting.db.entity.MacroEntity;
import frost.com.homelighting.ui.lighting.DeviceIPAndCommand;

@Dao
public interface MacroDao {
    @Query("SELECT * FROM macro")
    LiveData<List<MacroEntity>> loadAllMacros();

    @Query("SELECT id FROM macro")
    List<Integer> loadAllMacrosIds();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MacroEntity> macros);

    @Query("SELECT * FROM macro WHERE id = :id")
    LiveData<MacroEntity> loadMacro(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertMacro(MacroEntity macro);

    @Delete
    void deleteMacro(MacroEntity macro);

    @Query("DELETE FROM macro WHERE id = :macroId")
    void deleteMacro(int macroId);

    @Query("SELECT p.name FROM preset p JOIN macro_details md ON md.macro_id = :macroId AND md.preset_id = p.id")
    List<String> getMacrosPresetsNames(int macroId);

    @Query("SELECT " +
            "p.command,d.ip_address " +
            "FROM macro_details md " +
                "JOIN preset p ON md.preset_id = p.id " +
                "JOIN preset_details pd ON pd.preset_id = p.id " +
                "JOIN devices d ON pd.device_ip = d.ip_address " +
            "WHERE md.macro_id = :macroId")
    List<DeviceIPAndCommand> getCommandAndDevices(int macroId);
}
