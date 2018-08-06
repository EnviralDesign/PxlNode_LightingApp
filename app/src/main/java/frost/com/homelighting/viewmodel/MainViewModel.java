package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupDetailsEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.OnlineDeviceEntity;

public class MainViewModel extends ViewModel{

    private Repository repository;
    private LiveData<List<OnlineDeviceEntity>> onlineDevices;

    public MainViewModel(Repository repository) {
        this.repository = repository;
    }

    public LiveData<List<DeviceEntity>> getOnlineDevices(){
        return repository.loadOnlineDevices();
    }
}
