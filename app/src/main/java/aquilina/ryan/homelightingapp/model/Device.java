package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;

/**
 * Created by SterlingRyan on 9/4/2017.
 */

public class Device implements Serializable{
    private String ipAddress;
    private String name;

    public Device(String ipAddress, String name) {
        this.ipAddress = ipAddress;
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
