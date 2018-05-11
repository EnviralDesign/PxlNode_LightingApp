package frost.com.homelighting.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import frost.com.homelighting.model.MacroDetails;

@Entity(tableName = "macro_details",
        primaryKeys = {"macro_id", "preset_id"},
        foreignKeys = {@ForeignKey(entity = MacroEntity.class,
                parentColumns = "id",
                childColumns = "macro_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = PresetEntity.class,
                        parentColumns = "id",
                        childColumns = "preset_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
            @Index(value = "macro_id"),
            @Index(value = "preset_id")
        })
public class MacroDetailsEntity implements MacroDetails{
    @ColumnInfo(name = "macro_id")
    private int macroId;

    @ColumnInfo(name = "preset_id")
    private int presetId;

    public MacroDetailsEntity(int macroId, int presetId) {
        this.macroId = macroId;
        this.presetId = presetId;
    }

    @Override
    public int getMacroId() {
        return macroId;
    }

    public void setMacroId(int macroId) {
        this.macroId = macroId;
    }

    @Override
    public int getPresetId() {
        return presetId;
    }

    public void setPresetId(int presetId) {
        this.presetId = presetId;
    }
}
