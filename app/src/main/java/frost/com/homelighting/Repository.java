package frost.com.homelighting;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import javax.inject.Inject;

import frost.com.homelighting.db.AppDatabase;
import frost.com.homelighting.db.dao.DeviceDao;
import frost.com.homelighting.db.dao.GroupDao;
import frost.com.homelighting.db.dao.GroupDetailsDao;
import frost.com.homelighting.db.dao.MacroDao;
import frost.com.homelighting.db.dao.MacroDetailsDao;
import frost.com.homelighting.db.dao.OnlineDevicesDao;
import frost.com.homelighting.db.dao.PresetAndGroupDetailsDao;
import frost.com.homelighting.db.dao.PresetDao;
import frost.com.homelighting.db.dao.PresetDetailsDao;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupDetailsEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.MacroDetailsEntity;
import frost.com.homelighting.db.entity.MacroEntity;
import frost.com.homelighting.db.entity.OnlineDeviceEntity;
import frost.com.homelighting.db.entity.PresetAndGroupDetailsEntity;
import frost.com.homelighting.db.entity.PresetDetailsEntity;
import frost.com.homelighting.db.entity.PresetEntity;
import frost.com.homelighting.model.OnlineDevices;
import frost.com.homelighting.ui.lighting.DeviceIPAndCommand;
import frost.com.homelighting.ui.lighting.PresetAndGroupNames;
import frost.com.homelighting.webservice.NodeMCUAPI;
import retrofit2.Call;
import retrofit2.Retrofit;

import static android.support.v4.os.LocaleListCompat.create;

public class Repository {
    private final DeviceDao deviceDao;
    private final GroupDao groupDao;
    private final MacroDao macroDao;
    private final PresetDao presetDao;
    private final GroupDetailsDao groupDetailsDao;
    private final MacroDetailsDao macroDetailsDao;
    private final PresetDetailsDao presetDetailsDao;
    private final OnlineDevicesDao onlineDevicesDao;
    private final PresetAndGroupDetailsDao presetAndGroupDetailsDao;
    private final AppDatabase appDatabase;
    private NodeMCUAPI nodeMCUAPI;

    @Inject
    public Repository(AppDatabase appDatabase, DeviceDao deviceDao, GroupDao groupDao, MacroDao macroDao, PresetDao presetDao, GroupDetailsDao groupDetailsDao, MacroDetailsDao macroDetailsDao, PresetDetailsDao presetDetailsDao, OnlineDevicesDao onlineDevicesDao, PresetAndGroupDetailsDao presetAndGroupDetailsDao) {
        this.appDatabase = appDatabase;
        this.deviceDao = deviceDao;
        this.groupDao = groupDao;
        this.macroDao = macroDao;
        this.presetDao = presetDao;
        this.groupDetailsDao = groupDetailsDao;
        this.macroDetailsDao = macroDetailsDao;
        this.presetDetailsDao = presetDetailsDao;
        this.onlineDevicesDao = onlineDevicesDao;
        this.presetAndGroupDetailsDao = presetAndGroupDetailsDao;
    }

    public NodeMCUAPI getNodeMCUAPI() {
        return nodeMCUAPI;
    }

    public void setNodeMCUAPI(NodeMCUAPI nodeMCUAPI) {
        this.nodeMCUAPI = nodeMCUAPI;
    }

    /** Device related functions **/
    public LiveData<List<DeviceEntity>> loadAllDevices(){
        return deviceDao.loadAllDevices();
    }

    public LiveData<DeviceEntity> loadDevice(String ipAddress){
        return deviceDao.loadDevice(ipAddress);
    }

    public void insertDevice(DeviceEntity deviceEntity){
        deviceDao.insertDevice(deviceEntity);
    }

    public List<Long> insertAllDevices(List<DeviceEntity> deviceEntity){
        return deviceDao.insertAll(deviceEntity);
    }

    public void deleteDevice(DeviceEntity deviceEntity){
        deviceDao.deleteDevice(deviceEntity);
    }

    /** Group related functions **/
    public LiveData<List<GroupEntity>> loadAllGroups(){
        return groupDao.loadAllGroups();
    }

    public LiveData<GroupEntity> loadGroup(int groupId){
        return groupDao.loadGroup(groupId);
    }

    public Long insertGroup(GroupEntity groupEntity){
        return groupDao.insertGroup(groupEntity);
    }

    public void deleteGroup(GroupEntity groupEntity){
        groupDao.deleteGroup(groupEntity);
    }

    public void deleteGroup(int groupId){
        groupDao.deleteGroup(groupId);
    }

    public void insertGroupDetails(GroupDetailsEntity groupDetailsEntity){
        groupDetailsDao.saveGroupDetails(groupDetailsEntity);
    }

    public List<Long> insertAllGroupDetails(List<GroupDetailsEntity> groupDetailsEntities){
        return groupDetailsDao.saveAllGroupDetails(groupDetailsEntities);
    }

    public LiveData<List<DeviceEntity>> loadDevicesInGroup(int groupId){
        return groupDetailsDao.loadDevicesInGroup(groupId);
    }

    public List<String> loadDevicesIpAddressInGroup(int groupId){
        return groupDetailsDao.loadDevicesIpAddress(groupId);
    }

    public void insertPresetGroupDetails(PresetAndGroupDetailsEntity presetAndGroupDetailsEntity){
        presetAndGroupDetailsDao.insertPresetAndGroupDetails(presetAndGroupDetailsEntity);
    }

    public void deleteGroupPresetDetailsEntity(int groupId){
        presetAndGroupDetailsDao.deleteGroupPresetDetailEntities(groupId);
    }

    public List<String> loadGroupDeviceNames(int groupId){
        return groupDetailsDao.loadGroupDeviceNames(groupId);
    }

    /** Preset related functions **/
    public LiveData<List<PresetEntity>> loadAllPresets(){
        return presetDao.loadAllPresets();
    }

    public LiveData<PresetEntity> loadPreset(int presetId){
        return presetDao.loadPreset(presetId);
    }

    public Long insertPreset(PresetEntity presetEntity){
        return presetDao.insertPreset(presetEntity);
    }

    public void deletePreset(PresetEntity presetEntity){
        presetDao.deletePreset(presetEntity);
    }

    public void deletePreset(int presetId){
        presetDao.deletePreset(presetId);
    }

    public void insertPresetDetails(PresetDetailsEntity presetDetailsEntity){
        presetDetailsDao.savePresetDetails(presetDetailsEntity);
    }

    public void insertAllPresetDetails(List<PresetDetailsEntity> presetDetailsEntities){
        presetDetailsDao.insertAll(presetDetailsEntities);
    }

    public void deletePresetDetails(int presetId){
        presetDetailsDao.deletePresetDetails(presetId);
    }

    public List<String> loadPresetDeviceIP(int presetId){
        return presetDetailsDao.loadPresetDeviceIP(presetId);
    }

    public List<Integer> loadAllGroupIdsForPreset(int presetId){
        return presetAndGroupDetailsDao.loadAllGroupIdsForPreset(presetId);
    }

    public List<Integer> loadAllPresetIds(){
        return presetDao.loadAllPresetsIds();
    }

    public void deletePresetGroupDetailsEntity(int presetId){
        presetAndGroupDetailsDao.deletePresetGroupDetailEntities(presetId);
    }

    public List<String> loadPresetDeviceNames(int presetId){
        return presetDetailsDao.loadPresetDeviceNames(presetId);
    }

    public String loadPresetGroupName(int presetId){
        return presetAndGroupDetailsDao.loadPresetGroupName(presetId);
    }

    /** Macro related functions **/
    public LiveData<List<MacroEntity>> loadAllMacros(){
        return macroDao.loadAllMacros();
    }

    public LiveData<MacroEntity> loadMacro(int macroId){
        return macroDao.loadMacro(macroId);
    }

    public Long insertMacro(MacroEntity macroEntity){
        return macroDao.insertMacro(macroEntity);
    }

    public void insertAllMacroDetails(List<MacroDetailsEntity> macroDetailsEntities){
        macroDetailsDao.insertAllMacroDetails(macroDetailsEntities);
    }

    public void deleteMacro(MacroEntity macroEntity){
        macroDao.deleteMacro(macroEntity);
    }

    public void deleteMacro(int macroId){
        macroDao.deleteMacro(macroId);
    }

    public void deleteMacroDetails(int macroId){
        macroDetailsDao.deleteMacroDetails(macroId);
    }

    public List<DeviceIPAndCommand> loadMacroDeviceIPAndCommand(int macroId){
        return macroDao.getCommandAndDevices(macroId);
    }

    public List<Integer> loadMacroPresetIps(int macroId){
        return macroDetailsDao.loadPresetsIds(macroId);
    }

    public List<Integer> loadAllMacroIds(){
        return macroDao.loadAllMacrosIds();
    }

    public List<String> loadMacroPresetNames(int macroId){
        return macroDetailsDao.loadMacroPresetNames(macroId);
    }

    /** Online devices related functions **/
    public LiveData<List<DeviceEntity>> loadOnlineDevices(){
        return onlineDevicesDao.loadOnlineDevices();
    }

    public DeviceEntity loadOnlineDevice(String ipAddress){
        return onlineDevicesDao.loadOnlineDevice(ipAddress);
    }

    public List<Long> saveOnlineDevices(List<OnlineDeviceEntity> onlineDevices){
        return onlineDevicesDao.saveOnlineDevices(onlineDevices);
    }

    public void deleteAllOnlineDevices(){
        onlineDevicesDao.deleteOnlineDevices();
    }

    /** Web services related functions **/
    public void playPreset(String ipAddress, String command){
        nodeMCUAPI.playPreset(ipAddress, command);
    }

    public Call<String> getDeviceInfo(String ipAddress){
       return nodeMCUAPI.getDeviceInfo(ipAddress);
    }

    public void updateDevice(String ipAddress, DeviceEntity deviceEntity){
        nodeMCUAPI.updateDevice(ipAddress, deviceEntity);
    }

    public DeviceEntity getDeviceConfiguration(String ipAddress){
        return nodeMCUAPI.getDeviceConfiguration(ipAddress);
    }
}
