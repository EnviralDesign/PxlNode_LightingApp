package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.List;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.GroupEntity;

public class GroupViewModel extends ViewModel{

    private Repository repository;

    public GroupViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<GroupEntity>> getGroups(){
        return repository.loadAllGroups();
    }

    public void deleteGroup(int groupId){
        repository.deleteGroup(groupId);
        repository.deleteGroupPresetDetailsEntity(groupId);
    }

    public void deletePreset(int presetId){
        repository.deletePreset(presetId);
        repository.deletePresetDetails(presetId);
    }

    public void deleteMacroEntity(int macroId){
        repository.deleteMacro(macroId);
        repository.deleteMacroDetails(macroId);
    }

    public List<Integer> loadAllGroupIdsForPreset(int presetId){
        return repository.loadAllGroupIdsForPreset(presetId);
    }

    public List<Integer> loadAllPresetsIds() {
        return repository.loadAllPresetIds();
    }

    public List<Integer> loadAllMacroIds(){
        return repository.loadAllMacroIds();
    }

    public List<Integer> loadAllPresetIdsForMacro(int macroId){
        return repository.loadMacroPresetIps(macroId);
    }

    public List<String> loadGroupDeviceNames(int groupId){
        return repository.loadGroupDeviceNames(groupId);
    }
}
