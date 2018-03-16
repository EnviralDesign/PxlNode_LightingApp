/*
 * Created by Ryan Aquilina on 10/18/17 4:42 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/29/17 10:36 AM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OnlineDevices implements Serializable{
    private ArrayList<Device> devicesList;

    public OnlineDevices(ArrayList<Device> devicesList) {
        this.devicesList = devicesList;
    }

    public ArrayList<Device> getDevicesList() {
        return devicesList;
    }

    public void setDevicesList(ArrayList<Device> devicesList) {
        this.devicesList = devicesList;
    }

    public Device getDeviceByIP(String ip){
        for (Device device: devicesList) {
            if(device.getIpAddress().equals(ip)){
                return device;
            }
        }
        return null;
    }

    public void setDeviceByIP(String ip, Device newDevice) {
        for(Device device: devicesList){
            if(device.getIpAddress().equals(ip)){
                device = newDevice;
            }
        }
    }
}
