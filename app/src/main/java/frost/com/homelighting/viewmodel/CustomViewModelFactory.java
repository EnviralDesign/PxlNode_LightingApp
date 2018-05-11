package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import frost.com.homelighting.MainActivity;
import frost.com.homelighting.Repository;

public class CustomViewModelFactory implements ViewModelProvider.Factory {
    private final Repository repository;

    @Inject
    public CustomViewModelFactory(Repository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(GroupViewModel.class)){
            return (T) new GroupViewModel(repository);
        } else if(modelClass.isAssignableFrom(ScanViewModel.class)){
            return (T) new ScanViewModel(repository);
        } else if(modelClass.isAssignableFrom(PresetViewModel.class)){
            return (T) new PresetViewModel(repository);
        } else if(modelClass.isAssignableFrom(MacroViewModel.class)){
            return (T) new MacroViewModel(repository);
        } else if(modelClass.isAssignableFrom(DesignViewModel.class)){
            return (T) new DesignViewModel(repository);
        } else if(modelClass.isAssignableFrom(MainViewModel.class)){
            return (T) new MainViewModel(repository);
        } else if(modelClass.isAssignableFrom(LightingViewModel.class)){
            return (T) new LightingViewModel(repository);
        } else if(modelClass.isAssignableFrom(ConfigurationViewModel.class)){
            return (T) new ConfigurationViewModel(repository);
        }else {
            throw new IllegalArgumentException("ViewModel Not Found");
        }
    }
}
