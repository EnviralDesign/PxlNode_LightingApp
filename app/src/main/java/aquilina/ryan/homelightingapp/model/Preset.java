package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/12/2017.
 */

public class Preset implements Serializable{
    private String presetName;
    private ArrayList<Device> devicesList;

    public Preset(String presetName) {
        this.presetName = presetName;
        devicesList = new ArrayList<>();
    }

    public Preset(String presetName, ArrayList<Device> devicesList) {
        this.presetName = presetName;
        this.devicesList = devicesList;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public ArrayList<Device> getDevicesList() {
        return devicesList;
    }

    public void setDevicesList(ArrayList<Device> devicesList) {
        this.devicesList = devicesList;
    }
}
