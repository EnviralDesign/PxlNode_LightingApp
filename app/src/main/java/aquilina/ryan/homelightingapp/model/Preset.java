package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/12/2017.
 */

public class Preset implements Serializable{
    private int id;
    private String presetName;
    private DevicesGroup devicesGroup;

    public Preset(int id, String presetName) {
        this.id = id;
        this.presetName = presetName;
        this.devicesGroup = new DevicesGroup();
    }

    public Preset(int id, String presetName, DevicesGroup devicesGroup) {
        this.id = id;
        this.presetName = presetName;
        this.devicesGroup = devicesGroup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
    }

    public DevicesGroup getDevicesGroup() {
        return devicesGroup;
    }

    public void setDevicesGroup(DevicesGroup devicesGroup) {
        this.devicesGroup = devicesGroup;
    }
}
