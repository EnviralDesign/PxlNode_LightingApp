package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.ViewModel;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.DeviceEntity;

public class ConfigurationViewModel extends ViewModel {
    Repository repository;

    public ConfigurationViewModel(Repository repository) {
        this.repository = repository;
    }

    public void insertDevice(DeviceEntity deviceEntity){
        repository.insertDevice(deviceEntity);
    }
}
