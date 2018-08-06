package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupDetailsEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.OnlineDeviceEntity;
import frost.com.homelighting.model.OnlineDevices;

public class ScanViewModel extends ViewModel{

    private Repository repository;
    private List<String> groupNames;

    public ScanViewModel(Repository repository) {
        this.repository = repository;
        groupNames = new ArrayList<>();

        new loadGroupNames().execute();
    }

    public LiveData<List<DeviceEntity>> getOnlineDevices(){
        return repository.loadOnlineDevices();
    }

    public void saveOnlineDevices(List<OnlineDeviceEntity> onlineDevices){
       new SaveOnlineDevicesTask().execute(new ArrayList<>(onlineDevices));
    }

    public void insertAllDevices(List<DeviceEntity> deviceEntities){
        new InsertAllDevicesTasks().execute(new ArrayList<>(deviceEntities));
    }

    public LiveData<DeviceEntity> getDevice(String ipAddress){
        return repository.loadDevice(ipAddress);
    }

    public Long saveGroup(GroupEntity groupEntity){
        return repository.insertGroup(groupEntity);
    }

    public void saveAllGroupDetails(List<GroupDetailsEntity> groupDetailsEntities){
        repository.insertAllGroupDetails(groupDetailsEntities);
    }

    public void deleteAllOnlineDevices(){
        repository.deleteAllOnlineDevices();
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    private class loadGroupNames extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            return repository.loadAllGroupsNames();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if(strings == null){
                return;
            }
            groupNames.addAll(strings);
        }
    }

    private class SaveOnlineDevicesTask extends AsyncTask<List<OnlineDeviceEntity>, Void, Void>{
        @Override
        protected Void doInBackground(List<OnlineDeviceEntity>... lists) {
            repository.saveOnlineDevices(lists[0]);
            return null;
        }
    }

    private class InsertAllDevicesTasks extends AsyncTask<List<DeviceEntity>, Void, Void>{
        @Override
        protected Void doInBackground(List<DeviceEntity>... lists) {
           repository.insertAllDevices(lists[0]);
           return null;
        }
    }
}
