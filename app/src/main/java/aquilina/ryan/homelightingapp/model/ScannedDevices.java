package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/29/2017.
 */

public class ScannedDevices implements Serializable{
    private ArrayList<Device> devicesList;

    public ScannedDevices(ArrayList<Device> devicesList) {
        this.devicesList = devicesList;
    }

    public ArrayList<Device> getDevicesList() {
        return devicesList;
    }

    public void setDevicesList(ArrayList<Device> devicesList) {
        this.devicesList = devicesList;
    }
}
