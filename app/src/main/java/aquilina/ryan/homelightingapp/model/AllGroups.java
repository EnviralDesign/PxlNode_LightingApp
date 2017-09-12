package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/7/2017.
 */

public class AllGroups implements Serializable{
    ArrayList<DevicesGroup> groups;

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
