/*
 * Created by Ryan Aquilina on 10/18/17 4:37 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/12/17 9:07 AM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AllGroups implements Serializable{
    private ArrayList<DevicesGroup> groups;

    public AllGroups() {
        groups = new ArrayList<>();
    }

    public AllGroups(ArrayList<DevicesGroup> groups) {
        this.groups = groups;
    }

    public ArrayList<DevicesGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<DevicesGroup> groups) {
        this.groups = groups;
    }

    public void addGroup(DevicesGroup group){
        groups.add(group);
    }

    public void removeGroup(DevicesGroup group){
        groups.remove(group);
    }
}
