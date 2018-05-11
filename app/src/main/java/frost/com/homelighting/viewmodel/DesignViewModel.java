package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.PresetAndGroupDetailsEntity;
import frost.com.homelighting.db.entity.PresetDetailsEntity;
import frost.com.homelighting.db.entity.PresetEntity;

public class DesignViewModel extends ViewModel{

    private Repository repository;
    private ExecutorService databaseAccessExecutor;
    private MutableLiveData<List<String>> selectedDevices;

    public DesignViewModel(Repository repository) {
        this.repository = repository;
        databaseAccessExecutor = Executors.newSingleThreadExecutor();
        selectedDevices = new MutableLiveData<List<String>>();
    }

    public LiveData<List<String>> getSelectedDevices() {
        return selectedDevices;
    }

    public LiveData<List<DeviceEntity>> getOnlineDevices(){
        return repository.loadOnlineDevices();
    }

    public LiveData<List<GroupEntity>> getGroups(){
        return repository.loadAllGroups();
    }

    public LiveData<List<DeviceEntity>> getDevicesInGroup(int groupId){
        return repository.loadDevicesInGroup(groupId);
    }

    public Long savePreset(PresetEntity presetEntity){
        return repository.insertPreset(presetEntity);
    }

    public void savePresetDetails(List<PresetDetailsEntity> presetDetailsEntities){
        repository.insertAllPresetDetails(presetDetailsEntities);
    }

    public void getDevicesIpAddressInGroup(int groupId){
        new loadDevicesIpAddressInGroupTask().execute(groupId);
    }

    public DeviceEntity getOnlineDevice(String ipAddress){
        return repository.loadOnlineDevice(ipAddress);
    }

    public void insertPresetAndGroupDetails(PresetAndGroupDetailsEntity presetAndGroupDetailsEntity){
        repository.insertPresetGroupDetails(presetAndGroupDetailsEntity);
    }

    private class loadDevicesIpAddressInGroupTask extends AsyncTask<Integer, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Integer... integers) {
            return repository.loadDevicesIpAddressInGroup(integers[0]);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            selectedDevices.setValue(strings);
        }
    }
}
