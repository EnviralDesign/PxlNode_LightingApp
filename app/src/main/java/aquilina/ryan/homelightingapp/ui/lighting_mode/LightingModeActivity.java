/*
 * Created by Ryan Aquilina on 10/18/17 4:51 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/10/17 1:29 PM
 */

package aquilina.ryan.homelightingapp.ui.lighting_mode;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.internal.ANRequestQueue;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aquilina.ryan.homelightingapp.Application;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.Macro;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.model.OnlineDevices;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Common;
import aquilina.ryan.homelightingapp.utils.Constants;

public class LightingModeActivity extends MainActivity {
    private TextView mHintTextView;

    private ArrayList<Preset> mPresets;
    private ArrayList<Macro> mMacros;
    private PresetAdapter mAdapter;

    private Common common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_mode);
        common = new Common();

        // Set views.
        mNavigationView.setCheckedItem(R.id.nav_lighting_mode);
        RecyclerView mPresetsRecyclerView = findViewById(R.id.presets_recycler_list);
        mHintTextView = findViewById(R.id.text_view_hint);
        mTitleTextView.setText(R.string.lighting_mode_title);

        // Set view's data/design
        mPresets = new ArrayList<>();
        mMacros = new ArrayList<>();
        mAdapter = new PresetAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mPresetsRecyclerView.setLayoutManager(layoutManager);
        mPresetsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresets = common.loadPresets(getApplicationContext());
        mMacros = common.loadMacros(this);
        if(!mPresets.isEmpty() || !mMacros.isEmpty()){
            mHintTextView.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_lights, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.switch_off_devices_button:
                switchOffLights();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get a list of online devices.
     */
    private ArrayList<Device> getOnlineDevice(){
        OnlineDevices onlineDevices = ((Application)getApplication()).getScannedDevices();

        if(onlineDevices != null){
            return onlineDevices.getDevicesList();
        }
        return null;
    }

    /**
     * Switch off a list of devices.
     */
    private void switchOffLights(){
        ArrayList<Device> onlineDevices = getOnlineDevice();

        if(onlineDevices != null){
            for(Device device : onlineDevices){
                AndroidNetworking.post("http://" + device.getIpAddress() + "/play")
                        .addByteBody(("blank").getBytes())
                        .setPriority(Priority.IMMEDIATE)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                // do anything with response
                            }
                            @Override
                            public void onError(ANError error) {
                                // handle error
                            }
                        });
            }
        }
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;

        private TitleViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.header_title);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener{
        TextView nameTextView;
        TextView groupTextView;
        ImageView aSwitch;
        LinearLayout cardView;
        CheckBox checkBox;
        ViewHolderClickListener mListener;

        private ItemViewHolder(View itemView, ViewHolderClickListener mListener) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.preset_name);
            this.groupTextView = itemView.findViewById(R.id.associated_group);
            this.aSwitch = itemView.findViewById(R.id.preset_switch);
            this.cardView = itemView.findViewById(R.id.item_card_view);
            this.checkBox = itemView.findViewById(R.id.item_checkbox);
            this.mListener = mListener;
            aSwitch.setOnClickListener(this);
            cardView.setOnClickListener(this);
            aSwitch.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardViewClick(view);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mListener.onCardGestureListener(view, motionEvent);
        }

        private interface ViewHolderClickListener{
            void onCardViewClick(View view);
            boolean onCardGestureListener(View view, MotionEvent motionEvent);
        }
    }

    private class PresetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int MACRO_TITLE = 0;
        private static final int PRESETS_TITLE = 1;
        private static final int MACRO_ITEM = 2;
        private static final int PRESET_ITEM = 3;

        private ItemViewHolder.ViewHolderClickListener mListener = new ItemViewHolder.ViewHolderClickListener(){

            @Override
            public void onCardViewClick(View view) {
//                itemOnClick(view);
            }

            @Override
            public boolean onCardGestureListener(View view, MotionEvent motionEvent) {
                return itemOnGestureListener(view, motionEvent);
            }
        };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == MACRO_TITLE || viewType == PRESETS_TITLE){
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lighting_mode_header, parent, false);
                return new TitleViewHolder(itemView);
            } else {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset, parent, false);
                return new ItemViewHolder(itemView, mListener);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()){
                case MACRO_TITLE:{
                    TitleViewHolder macroTitleViewHolder = (TitleViewHolder) holder;
                    macroTitleViewHolder.titleTextView.setText(getString(R.string.recycler_view_macros_title));
                    macroTitleViewHolder.titleTextView.setTypeface(mHeaderTypeFace);
                    break;
                }
                case MACRO_ITEM:{
                    ItemViewHolder macroItemViewHolder = (ItemViewHolder) holder;
                    Macro macro = mMacros.get(position - 1);
                    macroItemViewHolder.cardView.setTag(R.id.ID, macro.getId());
                    macroItemViewHolder.cardView.setTag(R.id.groupType, Constants.MACRO);
                    macroItemViewHolder.aSwitch.setTag(R.id.ID, macro.getId());
                    macroItemViewHolder.aSwitch.setTag(R.id.groupType, Constants.MACRO);
                    macroItemViewHolder.nameTextView.setText(macro.getName());
                    macroItemViewHolder.nameTextView.setTypeface(mTextTypeFace);
                    macroItemViewHolder.groupTextView.setText(getMacroItemSubString(macro));
                    macroItemViewHolder.groupTextView.setTypeface(mSubTextTypeFace);
                    macroItemViewHolder.checkBox.setVisibility(View.GONE);
                    macroItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    break;
                }
                case PRESETS_TITLE:{
                    TitleViewHolder presetTitleViewHolder = (TitleViewHolder) holder;
                    presetTitleViewHolder.titleTextView.setText(getString(R.string.recycler_view_presets_title));
                    presetTitleViewHolder.titleTextView.setTypeface(mHeaderTypeFace);
                    break;
                }
                case PRESET_ITEM:{
                    ItemViewHolder presetItemViewHolder = (ItemViewHolder) holder;
                    Preset preset;
                    if(mMacros.isEmpty()){
                        preset = mPresets.get(position - 1);
                    } else {
                        preset = mPresets.get(position - (mMacros.size() + 2));
                    }
                    presetItemViewHolder.cardView.setTag(R.id.ID, preset.getId());
                    presetItemViewHolder.cardView.setTag(R.id.groupType, Constants.PRESET);
                    presetItemViewHolder.aSwitch.setTag(R.id.ID, preset.getId());
                    presetItemViewHolder.aSwitch.setTag(R.id.groupType, Constants.PRESET);
                    presetItemViewHolder.nameTextView.setText(preset.getPresetName());
                    presetItemViewHolder.nameTextView.setTypeface(mTextTypeFace);
                    String name = preset.getDevicesGroup().getName();
                    if(name == null){
                        String ip = preset.getDevicesGroup().getDeviceIPArrayList().get(0);
                        Device device = ((Application)getApplicationContext()).getDeviceByIP(ip);
                        if(device != null){
                            name = device.getName();
                        }
                        else{
                            name = getString(R.string.device_not_online);
                        }
                    }
                    presetItemViewHolder.groupTextView.setText(name);
                    presetItemViewHolder.groupTextView.setTypeface(mSubTextTypeFace);
                    presetItemViewHolder.checkBox.setVisibility(View.GONE);
                    presetItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mMacros.isEmpty() && mPresets.isEmpty()) {
                return 0;
            }
            else if (mMacros.isEmpty()) {
                return mPresets.size() + 1;
            }
            else if (mPresets.isEmpty()){
                return mMacros.size() + 1;
            }
            else{
                return mMacros.size() + mPresets.size() + 2;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mMacros.isEmpty() && mPresets.isEmpty()) {
                return -1;
            }
            else if (mMacros.isEmpty()) {
                if(position == 0 ){
                    return PRESETS_TITLE;
                }
                else {
                    return PRESET_ITEM;
                }
            }
            else if (mPresets.isEmpty()){
                if(position == 0 ){
                    return MACRO_TITLE;
                }
                else {
                    return MACRO_ITEM;
                }
            }
            else{
                if(position == 0){
                    return MACRO_TITLE;
                }
                else if (position <= mMacros.size()){
                    return MACRO_ITEM;
                }
                else if (position == mMacros.size() + 1){
                    return PRESETS_TITLE;
                }
                else {
                    return PRESET_ITEM;
                }
            }
        }

        /**
         * Switch on a preset or a macro
         * @param id is the id number of the Macro/Preset
         * @param groupType is the type of group either Macro or Preset
         */
        private void switchOnDevices(int id, String groupType){
            if(groupType.equals(Constants.PRESET)){
                Preset presetClicked = null;
                for(Preset preset : mPresets){
                    if (preset.getId() == id){
                        presetClicked = preset;
                        break;
                    }
                }
                sendPresetCommandToDevices(presetClicked);
            }
            else {
                Macro clickedMacro = null;
                for(Macro macro: mMacros){
                    if(id == macro.getId()){
                        clickedMacro = macro;
                    }
                }

                if(clickedMacro != null){
                    sendMacroCommands(clickedMacro);
                }
            }
        }

        /**
         * Switch on the presets inside the macro.
         * @param macro macro chosen to be switched on.
         */
        private void sendMacroCommands(Macro macro){
            ArrayList<DeviceAndCommand> commandsList = new ArrayList<>();
            OnlineDevices onlineDevices = ((Application) getApplication()).getScannedDevices();

            if(!macro.getPresetList().isEmpty()){
                for (Preset preset : macro.getPresetList()) {
                    for (String deviceIP : preset.getDevicesGroup().getDeviceIPArrayList()) {
                        commandsList.add(new DeviceAndCommand(onlineDevices.getDeviceByIP(deviceIP).getIpAddress(), preset.getCommand()));
                    }
                }

                ExecutorService executorService = Executors.newFixedThreadPool(commandsList.size());
                for(DeviceAndCommand deviceAndCommand :commandsList){
                    Runnable worker = new WorkerThread(deviceAndCommand.getCommand(), deviceAndCommand.getDeviceIp());
                    executorService.execute(worker);
                }
                executorService.shutdown();
            }
        }

        /**
         * Send the preset command to the devices
         * @param presetClicked the preset that has been activated
         */
        private void sendPresetCommandToDevices(Preset presetClicked){
            if(presetClicked != null){
                ArrayList<String> devicesIPs = presetClicked.getDevicesGroup().getDeviceIPArrayList();
                ArrayList<Device> devices = ((Application) getApplication()).getScannedDevices().getDevicesList();
                ArrayList<String> devicesIpAddresses = new ArrayList<>();

                if(!devices.isEmpty()){
                    for(String deviceIP : devicesIPs){
                        for(Device device : devices){
                            if(device.getIpAddress().equals(deviceIP)){
                                devicesIpAddresses.add(device.getIpAddress());
                            }
                        }
                    }

                    if(!devicesIpAddresses.isEmpty()){
                        ExecutorService executorService = Executors.newFixedThreadPool(devicesIpAddresses.size());
                        for(String ipAddress: devicesIpAddresses){
                            Runnable worker = new WorkerThread(presetClicked.getCommand(), ipAddress);
                            executorService.execute(worker);
                        }
                        executorService.shutdown();
                    }
                }
            }
        }

        private void sendPresetCommandToDeviceInstant(String command, ArrayList<String> ipAdresses){
            ANRequestQueue queue = new ANRequestQueue();
            for (String ipAdress: ipAdresses) {
                ANRequest request = AndroidNetworking.post("http://" + ipAdress + "/play")
                        .addByteBody(command.getBytes())
                        .build();

                queue.addRequest(request);
            }
        }

        /**
         * Asynchronous sending of post commands
         */
        private class WorkerThread implements Runnable{
            String command;
            String ipAddress;

            public WorkerThread(String command, String ipAddress) {
                this.command = command;
                this.ipAddress = ipAddress;
            }

            @Override
            public void run() {
                AndroidNetworking.post("http://" + ipAddress + "/play")
                        .addByteBody(command.getBytes())
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("PostCommand", "Success to " + ipAddress + " with command : " + command);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("PostCommand", "Fail to " + ipAddress + " with command : " + command);
                            }
                        });
            }
        }

        private boolean itemOnGestureListener(View view, MotionEvent motionEvent){
            ImageView imageView = view.findViewById(R.id.preset_switch);
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled_black_36dp));
                    switchOnDevices((int) view.getTag(R.id.ID), (String) view.getTag(R.id.groupType));
                    return false;
                case MotionEvent.ACTION_UP:
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled_white_36dp));
                    return false;
                case MotionEvent.ACTION_MOVE:
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled_white_36dp));
                    return false;
            }
            return false;
        }

        /**
         * Create and return the preset names in a Macro.
         */
        private String getMacroItemSubString(Macro macro){
            String subText = "";
            for(int i = 0; i < macro.getPresetList().size(); i++){
                subText += (macro.getPresetList().get(i)).getPresetName();
                if(i != macro.getPresetList().size() - 1){
                    subText += ", ";
                }
            }

            if(subText.length() > 50){
                subText = subText.substring(0, 50);
                subText += ".....";
            }

            return subText;
        }

        private class DeviceAndCommand {
            String deviceIp;
            String command;

            public DeviceAndCommand(String deviceIp, String command) {
                this.deviceIp = deviceIp;
                this.command = command;
            }

            public String getDeviceIp() {
                return deviceIp;
            }

            public String getCommand() {
                return command;
            }

        }
    }
}
