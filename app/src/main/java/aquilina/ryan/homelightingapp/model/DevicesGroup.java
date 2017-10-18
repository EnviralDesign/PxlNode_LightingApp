/*
 * Created by Ryan Aquilina on 10/18/17 4:39 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/29/17 1:42 PM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

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
