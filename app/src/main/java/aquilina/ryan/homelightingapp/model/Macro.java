package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/18/2017.
 */

public class Macro implements Serializable {
    private String name;
    private ArrayList<Preset> presetList;

    public Macro() {
    }

    public Macro(String name, ArrayList<Preset> presetList) {
        this.name = name;
        this.presetList = presetList;
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

    public void removePreser(Preset preset){
        presetList.remove(preset);
    }
}
