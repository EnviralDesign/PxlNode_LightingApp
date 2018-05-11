package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.MacroEntity;

public class MacroViewModel extends ViewModel {

    Repository repository;

    public MacroViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<MacroEntity>> getMacros(){
        return repository.loadAllMacros();
    }

    public void removeMacroList(int macroId){
        repository.deleteMacro(macroId);
        repository.deleteMacroDetails(macroId);
    }

    public List<String> loadMacroPresetNames(int macroId){
        return repository.loadMacroPresetNames(macroId);
    }
}
