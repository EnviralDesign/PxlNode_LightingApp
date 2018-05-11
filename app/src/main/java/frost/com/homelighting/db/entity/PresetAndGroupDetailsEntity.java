package frost.com.homelighting.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;

import java.util.List;

import frost.com.homelighting.model.PresetAndGroupDetails;

@Entity(tableName = "preset_and_group_details",
        primaryKeys = {"preset_id", "group_id"},
        foreignKeys = {@ForeignKey(entity = PresetEntity.class,
                parentColumns = "id",
                childColumns = "preset_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = GroupEntity.class,
                        parentColumns = "id",
                        childColumns = "group_id",
                        onDelete = ForeignKey.CASCADE)},
        indices = {
                @Index(value = "preset_id"),
                @Index(value = "group_id")})
public class PresetAndGroupDetailsEntity implements PresetAndGroupDetails{
    @ColumnInfo(name = "preset_id")
    private int presetId;

    @ColumnInfo(name = "group_id")
    private int groupId;

    public PresetAndGroupDetailsEntity(int presetId, int groupId) {
        this.presetId = presetId;
        this.groupId = groupId;
    }

    @Override
    public int getPresetId() {
        return presetId;
    }

    public void setPresetId(int presetId) {
        this.presetId = presetId;
    }

    @Override
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
