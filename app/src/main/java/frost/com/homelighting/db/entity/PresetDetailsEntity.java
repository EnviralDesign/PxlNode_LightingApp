package frost.com.homelighting.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import frost.com.homelighting.model.PresetDetails;

@Entity(tableName = "preset_details",
        primaryKeys = {"preset_id", "device_ip"},
        foreignKeys = {@ForeignKey(entity = PresetEntity.class,
                parentColumns = "id",
                childColumns = "preset_id",
                onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = DeviceEntity.class,
                        parentColumns = "ip_address",
                        childColumns = "device_ip",
                        onDelete = ForeignKey.CASCADE)},
        indices = {
            @Index(value = "preset_id"),
            @Index(value = "device_ip")})
public class PresetDetailsEntity implements PresetDetails {

    @ColumnInfo(name = "preset_id")
    private int presetId;

    @ColumnInfo(name = "device_ip")
    @NonNull
    private String deviceIp;

    public PresetDetailsEntity(int presetId, String deviceIp) {
        this.presetId = presetId;
        this.deviceIp = deviceIp;
    }

    @Override
    public int getPresetId() {
        return presetId;
    }

    public void setPresetId(int presetId) {
        this.presetId = presetId;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
}
