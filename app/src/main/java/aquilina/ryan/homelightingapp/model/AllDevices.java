/*
 * Created by Ryan Aquilina on 11/28/17 10:20 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 11/28/17 10:20 AM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class AllDevices implements Serializable {
    private HashMap<String, Device> deviceHashMap;

    public AllDevices(HashMap<String, Device> devicesList) {
        this.deviceHashMap = devicesList;
    }

    public HashMap<String, Device> getDeviceHashMap() {
        return deviceHashMap;
    }

    public void setDeviceHashMap(HashMap<String, Device> deviceHashMap) {
        this.deviceHashMap = deviceHashMap;
    }
}
