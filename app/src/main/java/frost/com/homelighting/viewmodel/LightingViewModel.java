package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.MacroDetailsEntity;
import frost.com.homelighting.db.entity.MacroEntity;
import frost.com.homelighting.db.entity.PresetEntity;
import frost.com.homelighting.ui.lighting.DeviceIPAndCommand;

public class LightingViewModel extends ViewModel {
    Repository repository;

    public LightingViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<PresetEntity>> getPresets(){
        return repository.loadAllPresets();
    }

    public LiveData<List<MacroEntity>> getMacros(){
        return repository.loadAllMacros();
    }

    public void deleteMacroEntity(int macroId){
        repository.deleteMacro(macroId);
        repository.deleteMacroDetails(macroId);
    }

    public void deletePresetEntity(int presetId){
        repository.deletePreset(presetId);
        repository.deletePresetDetails(presetId);
        repository.deletePresetGroupDetailsEntity(presetId);
    }

    public Long insertMacroEntity(MacroEntity macroEntity){
        return repository.insertMacro(macroEntity);
    }

    public void insertAllMacroDetailsEntity(List<MacroDetailsEntity> macroDetailsEntity){
        repository.insertAllMacroDetails(macroDetailsEntity);
    }

    public List<DeviceIPAndCommand> loadMacroDeviceIdAndCommand(int macroId){
        return repository.loadMacroDeviceIPAndCommand(macroId);
    }

    public List<String> loadPresetDeviceId(int presetId){
        return repository.loadPresetDeviceIP(presetId);
    }

    public List<Integer> loadAllMacroIds(){
        return repository.loadAllMacroIds();
    }

    public List<Integer> loadAllPresetIdsForMacro(int macroId){
        return repository.loadMacroPresetIps(macroId);
    }

    public List<String> loadMacroPresetNames(int macroId){
        return repository.loadMacroPresetNames(macroId);
    }

    public List<String> loadPresetDeviceNames(int presetId){
        return repository.loadPresetDeviceNames(presetId);
    }

    public String loadPresetGroupName(int presetId){
        return repository.loadPresetGroupName(presetId);
    }
}
