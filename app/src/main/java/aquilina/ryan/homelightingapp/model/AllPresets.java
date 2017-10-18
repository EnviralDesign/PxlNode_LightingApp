/*
 * Created by Ryan Aquilina on 10/18/17 4:38 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/28/17 3:26 PM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AllPresets implements Serializable{
    private ArrayList<Preset> allPresets;

    public AllPresets() {
        allPresets = new ArrayList<>();
    }

    public AllPresets(ArrayList<Preset> allPresets) {
        this.allPresets = allPresets;
    }

    public ArrayList<Preset> getAllPresets() {
        return allPresets;
    }

    public void setAllPresets(ArrayList<Preset> allPresets) {
        this.allPresets = allPresets;
    }

    public void addPreset(Preset preset){
        allPresets.add(preset);
    }

    public void removePreset(Preset preset){
        allPresets.remove(preset);
    }
}
