package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.PresetEntity;

public class PresetViewModel extends ViewModel {
    Repository repository;

    public PresetViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<PresetEntity>> getPresets(){
        return repository.loadAllPresets();
    }

    public void removePreset(int presetId){
        repository.deletePreset(presetId);
        repository.deletePresetDetails(presetId);
        repository.deletePresetGroupDetailsEntity(presetId);
    }

    public List<String> loadPresetDeviceNames(int presetId){
        return repository.loadPresetDeviceNames(presetId);
    }

    public String loadPresetGroupName(int presetId){
        return repository.loadPresetGroupName(presetId);
    }
}
