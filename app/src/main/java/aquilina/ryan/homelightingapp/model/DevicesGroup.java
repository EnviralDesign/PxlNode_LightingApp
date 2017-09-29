package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/4/2017.
 */

public class DevicesGroup implements Serializable{
    private int id;
    private String name;
    private ArrayList<Integer> deviceArrayList;

    public DevicesGroup() {
        this.deviceArrayList = new ArrayList<>();
    }

    public DevicesGroup(int id, String name, ArrayList<Integer> deviceArrayList) {
        this.id = id;
        this.name = name;
        this.deviceArrayList = deviceArrayList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getDeviceArrayList() {
        return deviceArrayList;
    }

    public void setDeviceArrayList(ArrayList<Integer> deviceArrayList) {
        this.deviceArrayList = deviceArrayList;
    }
}
