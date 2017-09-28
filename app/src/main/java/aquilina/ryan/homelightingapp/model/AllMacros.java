package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by SterlingRyan on 9/18/2017.
 */

public class AllMacros implements Serializable {
    private ArrayList<Macro> macros;

    public AllMacros() {
        macros = new ArrayList<>();
    }

    public AllMacros(ArrayList<Macro> macros) {
        this.macros = macros;
    }

    public ArrayList<Macro> getMacros() {
        return macros;
    }

    public void setMacros(ArrayList<Macro> macros) {
        this.macros = macros;
    }

    public void addMacro(Macro macro){
        macros.add(macro);
    }

    public void removeMacro(Macro macro){
        macros.remove(macro);
    }
}
