package frost.com.homelighting.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewDebug;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import frost.com.homelighting.R;
import frost.com.homelighting.Repository;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.PresetAndGroupDetailsEntity;
import frost.com.homelighting.db.entity.PresetDetailsEntity;
import frost.com.homelighting.db.entity.PresetEntity;

public class DesignViewModel extends ViewModel{

    private Repository repository;
    private MutableLiveData<List<String>> selectedDevices;
    private MutableLiveData<List<String>> sprites;
    private List<String> presetNames;

    public DesignViewModel(Repository repository) {
        this.repository = repository;
        selectedDevices = new MutableLiveData<>();
        sprites = new MutableLiveData<>();
        presetNames = new ArrayList<>();
        new loadPresetNames().execute();
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

    public MutableLiveData<List<String>> getSprites() {
        return sprites;
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

    public void setSpritesFromDevice(String ipAddress){
        new LoadDeviceSpritesTask(ipAddress).execute();
    }

    public List<String> getPresetNames(){
        return presetNames;
    }

    public void setSpritesFromGroups(int groupID){
        new LoadGroupDeviceSpritesTask(groupID).execute();
    }

    private class loadPresetNames extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            return repository.loadAllPresetsNames();
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if(strings == null){
                return;
            }
            presetNames.addAll(strings);
        }
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

    /**
     * Load sprites found in device into a mutableLiveData list.
     */
    private class LoadDeviceSpritesTask extends AsyncTask<Void, Void, List<String>>{
        private static final String JSON_SPRITE_ARRAY_KEY = "SPRITES";

        private String ipAddress;

        public LoadDeviceSpritesTask(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            HttpURLConnection urlConnection;
            URL url;
            String strLine;
            String spriteName;
            List<String> sprites = new ArrayList<>();
            sprites.add("SPRITE"); // Used so that user has the option to remove Sprite.
            JSONArray jsonArray;
            try{
                url = new URL("http://" + ipAddress + "/list?dir=/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                strLine = convertStreamToString(inputStream);
                jsonArray = new JSONArray(strLine);
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject element = jsonArray.getJSONObject(i);
                    spriteName = element.getString("name");
                    if(spriteName.contains(".sprite")) {
                        sprites.add(spriteName);
                    }
                }
                Log.w("LoadDeviceSpritesTask", "Get device sprites success for device :" + ipAddress);
            } catch (Exception e){
                Log.w("LoadDeviceSpritesTask", "Get device sprites failure for device :" + ipAddress);
            }


            return sprites;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            sprites.setValue(strings);
        }
    }

    /**
     * Load sprites found in device into a mutableLiveData list.
     */
    private class LoadGroupDeviceSpritesTask extends AsyncTask<Void, Void, List<String>>{
        private static final String JSON_SPRITE_ARRAY_KEY = "SPRITES";

        private List<String> ipAddresses;
        private int groupID;

        public LoadGroupDeviceSpritesTask(int groupID) {
            this.groupID = groupID;
            this.ipAddresses = new ArrayList<>();
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            this.ipAddresses = repository.loadDevicesIpAddressInGroup(groupID);
            HttpURLConnection urlConnection;
            URL url;
            String strLine;
            String spriteName;
            List<String> sprites = new ArrayList<>();
            sprites.add("SPRITE"); // Used so that user has the option to remove Sprite.
            JSONArray jsonArray;

            for(int i = 0; i < ipAddresses.size(); i++){
                try{
                    url = new URL("http://" + ipAddresses.get(i) + "/list?dir=/");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    strLine = convertStreamToString(inputStream);
                    jsonArray = new JSONArray(strLine);
                    for(int j = 0; j < jsonArray.length(); j++){
                        JSONObject element = jsonArray.getJSONObject(j);
                        spriteName = element.getString("name");
                        if(spriteName.contains(".sprite")) {
                            sprites.add(spriteName);
                        }
                    }
                    Log.w("LoadDeviceSpritesTask", "Get device sprites success for device :" + ipAddresses.get(i));
                } catch (Exception e){
                    Log.w("LoadDeviceSpritesTask", "Get device sprites failure for device :" + ipAddresses.get(i));
                }
            }

            return sprites;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            sprites.setValue(strings);
        }
    }

    /**
     * Convert the InputStream to a String.
     */
    private static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
