/*
 * Created by Ryan Aquilina on 10/18/17 5:15 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/16/17 3:50 PM
 */

package aquilina.ryan.homelightingapp.utils;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import aquilina.ryan.homelightingapp.model.AllDevices;
import aquilina.ryan.homelightingapp.model.AllGroups;
import aquilina.ryan.homelightingapp.model.AllMacros;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.model.Macro;
import aquilina.ryan.homelightingapp.model.Preset;

import static android.content.Context.MODE_PRIVATE;

public class Common {
    SharedPreferences mPrefs;

    public void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /*
    * **********************************************************************************
    * DATA LOADING
    * **********************************************************************************
    */

    /**
     * Load presets from SharedPreferences.
     */
    public ArrayList<Preset> loadPresets(Context context){
        mPrefs = context.getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_PRESETS, null);

        AllPresets allPresets = gson.fromJson(json, AllPresets.class);
        ArrayList<Preset> presets = new ArrayList<>();

        if(allPresets != null){
            presets.addAll(allPresets.getAllPresets());
        }
        return presets;
    }

    /**
     * Load all saved macros.
     */
    public ArrayList<Macro> loadMacros(Context context){
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_MACROS, null);

        if(json == null){
            return new ArrayList<>();
        } else {
            return gson.fromJson(json, AllMacros.class).getMacros();
        }
    }

    /**
     * Load groups from SharedPreferences.
     */
    public ArrayList<DevicesGroup> loadGroups(Context context){
        Gson gson = new Gson();
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        String json = mPrefs.getString(Constants.GROUP_OF_DEVICES_GROUPS, null);
        AllGroups allGroups = gson.fromJson(json, AllGroups.class);

        if(allGroups != null){
            return allGroups.getGroups();
        }
        return null;
    }

    /**
     * Load all saved devices
     */
    public HashMap<String, Device> loadDevices(Context context){
        Gson gson = new Gson();
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);

        String json = mPrefs.getString(Constants.ALL_DEVICES, null);
        AllDevices allDevices = gson.fromJson(json, AllDevices.class);

        if(allDevices != null){
            return allDevices.getDeviceHashMap();
        }
        return new HashMap<>();
    }

    /*
    * **********************************************************************************
    *   SAVING DATA
    * **********************************************************************************
    */

    /**
     * Save group list.
     */
    public void saveGroupList(ArrayList<DevicesGroup> groups, Context context){
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        AllGroups allGroups = new AllGroups(groups);

        String json = gson.toJson(allGroups);
        prefsEditor.putString(Constants.GROUP_OF_DEVICES_GROUPS, json);
        prefsEditor.apply();
    }

    /**
     * Save a macro in the macro group.
     */
    public void saveMacros(ArrayList<Macro> macros, Context context){
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllMacros allMacros = new AllMacros();
        Gson gson = new Gson();

        allMacros.setMacros(macros);

        String json = gson.toJson(allMacros);
        prefsEditor.putString(Constants.GROUP_OF_MACROS, json);
        prefsEditor.apply();
    }

    /**
     * Save preset list.
     */
    public ArrayList<Preset> savePresetList(ArrayList<Preset> presets, Context context){
        mPrefs = context.getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        AllPresets allPresets = new AllPresets(presets);

        String json = gson.toJson(allPresets);
        prefsEditor.putString(Constants.GROUP_OF_PRESETS, json);
        prefsEditor.apply();
        return presets;
    }

    /**
     * Save presets as macro.
     */
    public ArrayList<Macro> savePresetsInMacro(ArrayList<Macro> macros, Context context){
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllMacros allMacros;
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_MACROS, null);

        if(json == null){
            allMacros = new AllMacros();
        } else {
            allMacros = gson.fromJson(json, AllMacros.class);
        }

        allMacros.setMacros(macros);

        json = gson.toJson(allMacros);
        prefsEditor.putString(Constants.GROUP_OF_MACROS, json);
        prefsEditor.apply();
        return macros;
    }

    /**
     * Save device in AllDevices
     */
    public boolean saveDevice(Device device, Context context){
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllDevices allDevices;
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.ALL_DEVICES, null);

        if(json == null){
            allDevices = new AllDevices(new HashMap<String, Device>());
        } else {
            allDevices = gson.fromJson(json, AllDevices.class);
        }

        // Check if there is a device with the same ID
        if(allDevices.getDeviceHashMap().get(device.getIpAddress()) == null){
            allDevices.getDeviceHashMap().put(device.getIpAddress(), device);
            json = gson.toJson(allDevices);
            prefsEditor.putString(Constants.ALL_DEVICES, json);
            prefsEditor.apply();
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Saves devices HashMap
     */
    public void saveDeviceHashMap(HashMap<String, Device> deviceHashMap, Context context){
        mPrefs = context.getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllDevices allDevices = new AllDevices(deviceHashMap);

        Gson gson = new Gson();
        String json = gson.toJson(allDevices);

        prefsEditor.putString(Constants.ALL_DEVICES, json);
        prefsEditor.apply();
    }

    /*
    * **********************************************************************************
    *   DELETING DATA
    * **********************************************************************************
    */

    /**
     * Remove presets from macros.
     */
    public ArrayList<Macro> removePresetsFromMacros(ArrayList<Integer> presetsToDelete, Context context){
        ArrayList<Macro> macros = loadMacros(context);
        Iterator<Macro> macroIterator = macros.iterator();

        while(macroIterator.hasNext()){
            Macro macro = macroIterator.next();
            ArrayList<Preset> presets = macro.getPresetList();
            Iterator<Preset> presetsIterator = presets.iterator();

            for (int id: presetsToDelete) {
                while (presetsIterator.hasNext()){
                    Preset preset = presetsIterator.next();
                    if(preset.getId() == id){
                        presetsIterator.remove();
                        if(macro.getPresetList().isEmpty()){
                            macroIterator.remove();
                        }
                        break;
                    }
                }
            }
        }

        for (int id: presetsToDelete){
            macroIterator = macros.iterator();
            Macro macro;
            while (macroIterator.hasNext()){
                macro = macroIterator.next();
                macro.setPresetList(arrangePresetsIdsInMacro(macro.getPresetList(), id));
            }
        }
        saveMacros(macros, context);
        return macros;
    }

    /**
     * Remove groups from presets and macros.
     */
    public ArrayList<DevicesGroup> deleteGroups(ArrayList<Integer> toDeleteGroupsID, Context context){
        ArrayList<DevicesGroup> groups;
        groups = loadGroups(context);

        ArrayList<Preset> presets = loadPresets(context);
        Iterator<Preset> presetIterator = presets.iterator();

        if(groups != null){
            for(Integer i: toDeleteGroupsID){
                for(DevicesGroup group: groups){
                    if(group.getId() == i){
                        groups.remove(group);
                        while(presetIterator.hasNext()){
                            Preset preset = presetIterator.next();
                            if(preset.getDevicesGroup().getName() != null){
                                if(preset.getDevicesGroup().getName().equals(group.getName())){
                                    ArrayList<Integer> presetsToDelete = new ArrayList<>();
                                    presetsToDelete.add(preset.getId());
                                    removePresetsFromMacros(presetsToDelete, context);
                                    presetIterator.remove();
                                }
                            }
                        }
                        groups = arrangeGroupsIds(groups, i);
                        toDeleteGroupsID = arrangeArrayOfIdsIntegers(toDeleteGroupsID, i);
                        break;
                    }
                }
            }
        }

        savePresetList(presets, context);
        saveGroupList(groups, context);

        return groups;
    }

     /*
    * **********************************************************************************
    * HELPER METHODS
    * **********************************************************************************
    */

    /**
     * Arrange ids of presets inside other macros.
     */
    public ArrayList<Preset> arrangePresetsIdsInMacro(ArrayList<Preset> presets, int removedPresetId){
        int currentId;
        for(int i = 0; i < presets.size(); i ++){
            currentId = presets.get(i).getId();
            if(currentId > removedPresetId){
                presets.get(i).setId(currentId - 1);
            }
        }
        return presets;
    }

    /**
     * Arranges the id of the remaining presets.
     */
    public ArrayList<Preset> arrangePresetsIds(ArrayList<Preset> presets, int i){
        for(Preset preset : presets){
            if(preset.getId() > i){
                preset.setId(preset.getId() - 1);
            }
        }
        return presets;
    }

    /**
     * Arrange an array of ids.
     */
    public ArrayList<Integer> arrangeArrayOfIdsIntegers(ArrayList<Integer> idIntegers, int i){
        for(int j = 0; j < idIntegers.size(); j++){
            if(idIntegers.get(j) > i){
                idIntegers.set(j, idIntegers.get(j) - 1);
            }
        }
        return idIntegers;
    }

    /**
     * Arranges the id of the remaining macros.
     */
    public ArrayList<Macro> arrangeMacrosIds(ArrayList<Macro> macros, int i){
        for (int j = i; j < macros.size(); j++ ){
            macros.get(j).setId(j);
        }
        return macros;
    }

    /**
     * Arranges the id of the remaining groups.
     */
    public ArrayList<DevicesGroup> arrangeGroupsIds(ArrayList<DevicesGroup> groups, int i){
        for(int j = 0; j <groups.size(); j++){
            if(groups.get(j).getId() > i){
                groups.get(j).setId(groups.get(j).getId() - 1);
            }
        }
        return groups;
    }
}
