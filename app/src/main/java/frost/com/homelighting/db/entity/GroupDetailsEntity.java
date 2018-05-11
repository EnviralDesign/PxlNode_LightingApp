package frost.com.homelighting.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import dagger.Provides;
import frost.com.homelighting.model.GroupDetails;

@Entity(tableName = "group_details",
        primaryKeys = {"group_id", "device_ip"},
        foreignKeys = {
            @ForeignKey(entity = GroupEntity.class,
                        parentColumns = "id",
                        childColumns = "group_id",
                        onDelete = ForeignKey.CASCADE),
            @ForeignKey(entity = DeviceEntity.class,
                        parentColumns = "ip_address",
                        childColumns = "device_ip",
                        onDelete = ForeignKey.CASCADE)},
        indices = {
                @Index(value = "group_id"),
                @Index(value = "device_ip")
        })
public class GroupDetailsEntity implements GroupDetails{
    @ColumnInfo(name = "group_id")
    private int groupId;
    @ColumnInfo(name = "device_ip")
    @NonNull
    private String deviceIp;

    public GroupDetailsEntity(int groupId, String deviceIp) {
        this.groupId = groupId;
        this.deviceIp = deviceIp;
    }

    @Override
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
}
