package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.DeviceEntity;

public class ConfigurationViewModel extends ViewModel {
    Repository repository;

    public ConfigurationViewModel(Repository repository) {
        this.repository = repository;
    }

    public void updateDevice(DeviceEntity deviceEntity){
        new UpdateDeviceInfoTask().execute(deviceEntity);
    }

    private class UpdateDeviceInfoTask extends AsyncTask<DeviceEntity, Void, Void> {

        @Override
        protected Void doInBackground(DeviceEntity... deviceEntities) {
            repository.insertDevice(deviceEntities[0]);
            return null;
        }
    }
}
