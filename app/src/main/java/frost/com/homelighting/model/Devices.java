package frost.com.homelighting.model;

public interface Devices {
    String getName();
    String getIpAddress();
    int getPixelsPerStrip();
    int getChunkSize();
    int getUDPStreamingPort();
    int getMaPerPixel();
    float getAmpsLimit();
    String getWarmUpColor();
}
