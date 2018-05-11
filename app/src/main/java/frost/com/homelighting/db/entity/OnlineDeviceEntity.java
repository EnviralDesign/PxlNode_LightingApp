package frost.com.homelighting.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import frost.com.homelighting.model.OnlineDevices;

@Entity(tableName = "online_devices")
public class OnlineDeviceEntity implements OnlineDevices{
    @PrimaryKey
    @NonNull
    private String ipAddress;

    public OnlineDeviceEntity(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
