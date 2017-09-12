package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/12/2017.
 */

public class AllPresets implements Serializable{
    ArrayList<Preset> allPresets;

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
