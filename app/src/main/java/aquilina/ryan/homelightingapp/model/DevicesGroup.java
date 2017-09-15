package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/4/2017.
 */

public class DevicesGroup implements Serializable{
    private String name;
    private ArrayList<Device> deviceArrayList;

    public DevicesGroup() {
        this.deviceArrayList = new ArrayList<>();
    }

    public DevicesGroup(String name, ArrayList<Device> deviceArrayList) {
        this.name = name;
        this.deviceArrayList = deviceArrayList;
    }

    public String getName() {
        if(this.name == null){
            return deviceArrayList.get(0).getName();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Device> getDeviceArrayList() {
        return deviceArrayList;
    }

    public void setDeviceArrayList(ArrayList<Device> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
    }
}
