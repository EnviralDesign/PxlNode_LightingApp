/*
 * Created by Ryan Aquilina on 10/18/17 4:39 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/2/17 9:59 AM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Macro implements Serializable {
    private int id;
    private String name;
    private ArrayList<Preset> presetList;

    public Macro() {
    }

    public Macro(int id, String name, ArrayList<Preset> presetList) {
        this.id = id;
        this.name = name;
        this.presetList = presetList;
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

    public ArrayList<Preset> getPresetList() {
        return presetList;
    }

    public void setPresetList(ArrayList<Preset> presetList) {
        this.presetList = presetList;
    }

    public void addPreset(Preset preset){
        presetList.add(preset);
    }

    public void removePreset(Preset preset){
        presetList.remove(preset);
    }
}
