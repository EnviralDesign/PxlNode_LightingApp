/*
 * Created by Ryan Aquilina on 10/18/17 4:37 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/28/17 3:28 PM
 */

package aquilina.ryan.homelightingapp.model;

import java.io.Serializable;
import java.util.ArrayList;

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

    public Macro getMacroById(int id){
        for (Macro macro: macros) {
            if(macro.getId() == id){
                return macro;
            }
        }
        return null;
    }
}
