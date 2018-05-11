package frost.com.homelighting.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import frost.com.homelighting.model.Devices;

@Entity(tableName = "devices",
        indices = {@Index(value = "ip_address")})
public class DeviceEntity implements Devices{
    @PrimaryKey
    @ColumnInfo(name = "ip_address")
    @NonNull
    private String ipAddress;
    private String name;
    private int pixelsPerStrip;
    private int chunkSize;
    private int maPerPixel;
    private int UDPStreamingPort;
    private float ampsLimit;
    private String warmUpColor;

    @Ignore
    public DeviceEntity(String name,@NonNull String ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public DeviceEntity(String name,@NonNull String ipAddress, int pixelsPerStrip, int chunkSize, int maPerPixel, int UDPStreamingPort, float ampsLimit, String warmUpColor) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.pixelsPerStrip = pixelsPerStrip;
        this.chunkSize = chunkSize;
        this.maPerPixel = maPerPixel;
        this.UDPStreamingPort = UDPStreamingPort;
        this.ampsLimit = ampsLimit;
        this.warmUpColor = warmUpColor;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(@NonNull String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public int getPixelsPerStrip() {
        return pixelsPerStrip;
    }

    public void setPixelsPerStrip(int pixelsPerStrip) {
        this.pixelsPerStrip = pixelsPerStrip;
    }

    @Override
    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

    @Override
    public int getMaPerPixel() {
        return maPerPixel;
    }

    public void setMaPerPixel(int maPerPixel) {
        this.maPerPixel = maPerPixel;
    }

    @Override
    public int getUDPStreamingPort() {
        return UDPStreamingPort;
    }

    public void setUDPStreamingPort(int UDPStreamingPort) {
        this.UDPStreamingPort = UDPStreamingPort;
    }

    @Override
    public float getAmpsLimit() {
        return ampsLimit;
    }

    public void setAmpsLimit(float ampsLimit) {
        this.ampsLimit = ampsLimit;
    }

    @Override
    public String getWarmUpColor() {
        return warmUpColor;
    }

    public void setWarmUpColor(String warmUpColor) {
        this.warmUpColor = warmUpColor;
    }
}
