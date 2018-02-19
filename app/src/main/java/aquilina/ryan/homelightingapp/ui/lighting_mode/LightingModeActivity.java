/*
 * Created by Ryan Aquilina on 10/18/17 4:51 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/10/17 1:29 PM
 */

package aquilina.ryan.homelightingapp.ui.lighting_mode;

import com.google.gson.Gson;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aquilina.ryan.homelightingapp.Application;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllMacros;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.Macro;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.model.OnlineDevices;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Common;
import aquilina.ryan.homelightingapp.utils.Constants;

public class LightingModeActivity extends MainActivity {

    private TextView mHintTextView;
    private RecyclerView mRecyclerView;
    private Menu mMenu;

    private ArrayList<Preset> mPresets;
    private ArrayList<Integer> mSelectedPresets;
    private ArrayList<Macro> mMacros;
    private ArrayList<Integer> mSelectedMacros;
    private boolean isAddMacroAvailable;

    private PresetAdapter mAdapter;
    private Common common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_mode);
        common = new Common();

        // Set views.
        mRecyclerView = findViewById(R.id.presets_recycler_list);
        mHintTextView = findViewById(R.id.text_view_hint);
        mTitleTextView.setText(R.string.lighting_mode_title);

        // Set view's data/design
        mPresets = new ArrayList<>();
        mSelectedPresets = new ArrayList<>();
        mMacros = new ArrayList<>();
        mSelectedMacros = new ArrayList<>();
        mAdapter = new PresetAdapter();
        isAddMacroAvailable = false;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Load presets and macros
        mPresets = common.loadPresets(getApplicationContext());
        mMacros = common.loadMacros(this);
        if(!mPresets.isEmpty() || !mMacros.isEmpty()){
            mHintTextView.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();

        mNavigationView.setCheckedItem(R.id.nav_lighting_mode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_lights, menu);
        mMenu = menu;
        enableSelectionMenuItem(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mAdapter.setSelectionMode(false);
                return true;
            case R.id.add_macro_button:
                if(isAddMacroAvailable){
                    createNewMacro();
                }
                return true;
            case R.id.delete_button:
                deleteCheckedItems();
                return true;
            case R.id.refresh:
                switchOffLights();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mAdapter.isSelectionMode()){
            mAdapter.setSelectionMode(false);
        }
        else{
            super.onBackPressed();
        }
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
     * Switch off a list of online devices.
     */
    private void switchOffLights(){
        ArrayList<Device> onlineDevices = getOnlineDevice();

        if(onlineDevices != null){
            ExecutorService executorService = Executors.newFixedThreadPool(onlineDevices.size());
            for(Device device : onlineDevices){
                Runnable worker = new PostRequest("blank", device.getIpAddress());
                executorService.execute(worker);
            }
            executorService.shutdown();
        }
    }

    /**
     * Send a post request with the command in the body.
     */
    private static class PostRequest implements Runnable{
        String command;
        String ipAddress;

        public PostRequest(String command, String ipAddresses) {
            this.command = command;
            this.ipAddress = ipAddresses;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            HttpURLConnection urlConnection;
            URL url;
            OutputStream os;
            try{
                url = new URL("http://" + ipAddress + "/play");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(command.getBytes().length));
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(false);
                urlConnection.setDoInput(true);
                urlConnection.setConnectTimeout(100);
                urlConnection.setReadTimeout(100);
                os = urlConnection.getOutputStream();
                os.write(command.getBytes("UTF-8"));
                os.close();
                urlConnection.getInputStream();
                urlConnection.disconnect();
                Log.d("PostCommand", "Success to http://" + ipAddress + "/play");
            } catch (Exception e){
                Log.d("PostCommand", "Fail to http://" + ipAddress + "/play");
            }
        }
    }

    /**
     * Deletes all items in mSelected Items.
     */
    private void deleteCheckedItems(){

        // Delete macros from memory
        if(!mSelectedMacros.isEmpty()){
            ArrayList<Macro> macros;
            macros = common.loadMacros(this);

            if(macros != null){
                for(Integer i: mSelectedMacros){
                    for(Macro macro: macros){
                        if(macro.getId() == i){
                            macros.remove(macro);
                            break;
                        }
                    }
                }
            }
            mMacros = macros;
            common.saveMacros(macros, this);
            mSelectedMacros.clear();
        }

        //Delete Presets from memory
        if(!mSelectedPresets.isEmpty()){
            ArrayList<Preset> presets;
            presets = common.loadPresets(this);

            if(presets != null){
                for(Integer i: mSelectedPresets){
                    for(Preset preset: presets){
                        if(preset.getId() == i){
                            presets.remove(preset);
                            presets = common.arrangePresetsIds(presets, i);
                            mSelectedPresets = common.arrangeArrayOfIdsIntegers(mSelectedPresets, i);
                            break;
                        }
                    }
                }
            }
            mMacros = common.removePresetsFromMacros(mSelectedPresets,this);
            mPresets = presets;
            common.savePresetList(presets,this);
            mSelectedPresets.clear();
        }



        mAdapter.setSelectionMode(false);
        mAdapter.notifyDataSetChanged();
        if(mMacros.isEmpty() && mPresets.isEmpty()){
            mHintTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Enable or disable selection menu in app bar.
     */
    private void enableSelectionMenuItem(boolean enable){
        if(enable){
            mMenu.findItem(R.id.delete_button).setVisible(true);
            mMenu.findItem(R.id.add_macro_button).setVisible(true);
        }
        else{
            mMenu.findItem(R.id.delete_button).setVisible(false);
            mMenu.findItem(R.id.add_macro_button).setVisible(false);
        }
    }

    /**
     * Refresh the recycler view to un-check all checked items.
     */
    private void removeCheckedItems(){
        mSelectedPresets.clear();
        mSelectedMacros.clear();
        mAdapter = new PresetAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Create a new Macro
     */
    private void createNewMacro(){
        DialogFragment dialogFragment = AddMacroDialog.newInstance();
        dialogFragment.show(getFragmentManager(), "AddMacroDialog");
    }

    /**
     * Save presets as macro.
     */
    public void saveMacro(String macroName){
        SharedPreferences mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllMacros allMacros;
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_MACROS, null);

        if(json == null){
            allMacros = new AllMacros();
        } else {
            allMacros = gson.fromJson(json, AllMacros.class);
        }

        ArrayList<Preset> presets = new ArrayList<>();
        for(int id: mSelectedPresets){
            for(Preset preset: mPresets){
                if(preset.getId() == id){
                    presets.add(preset);
                    break;
                }
            }
        }

        Macro macro = new Macro(allMacros.getMacros().size() + 1, macroName, presets);
        allMacros.addMacro(macro);

        json = gson.toJson(allMacros);
        prefsEditor.putString(Constants.GROUP_OF_MACROS, json);
        prefsEditor.apply();

        mMacros.add(macro);
        mAdapter.setSelectionMode(false);
        mAdapter.notifyDataSetChanged();
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;

        private TitleViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.header_title);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener{
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
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            aSwitch.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardViewClick(view);
        }

        @Override
        public boolean onLongClick(View view) {
            return mListener.onCardViewLongClick(view);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mListener.onCardGestureListener(view, motionEvent);
        }

        private interface ViewHolderClickListener{
            void onCardViewClick(View view);
            boolean onCardViewLongClick(View view);
            boolean onCardGestureListener(View view, MotionEvent motionEvent);
        }
    }

    private class PresetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int MACRO_TITLE = 0;
        private static final int PRESETS_TITLE = 1;
        private static final int MACRO_ITEM = 2;
        private static final int PRESET_ITEM = 3;

        private boolean isSelectionMode = false;

        private ItemViewHolder.ViewHolderClickListener mListener = new ItemViewHolder.ViewHolderClickListener(){

            @Override
            public void onCardViewClick(View view) {
                itemOnClick(view);
            }

            @Override
            public boolean onCardGestureListener(View view, MotionEvent motionEvent) {
                return itemOnGestureListener(view, motionEvent);
            }

            @Override
            public boolean onCardViewLongClick(View view) {
                if(!isSelectionMode){
                    setSelectionMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    String groupType = (String) (view.findViewById(R.id.preset_switch)).getTag(R.id.groupType);
                    if(groupType.equals(Constants.MACRO)){
                        mSelectedMacros.add((Integer)(view.findViewById(R.id.preset_switch)).getTag(R.id.ID));
                    } else{
                        mSelectedPresets.add((Integer)(view.findViewById(R.id.preset_switch)).getTag(R.id.ID));
                    }

                    // Check if creating a macro is available
                    checkNewMacroAvailability();
                }
                return true;
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

            // Create a list of two separate types, with
            // titles separating each type.
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

                    // Set the tags to identify the macro.
                    macroItemViewHolder.cardView.setTag(R.id.ID, macro.getId());
                    macroItemViewHolder.cardView.setTag(R.id.groupType, Constants.MACRO);
                    macroItemViewHolder.aSwitch.setTag(R.id.ID, macro.getId());
                    macroItemViewHolder.aSwitch.setTag(R.id.groupType, Constants.MACRO);
                    macroItemViewHolder.nameTextView.setText(macro.getName());
                    macroItemViewHolder.nameTextView.setTypeface(mTextTypeFace);
                    macroItemViewHolder.groupTextView.setText(getMacroItemSubString(macro));
                    macroItemViewHolder.groupTextView.setTypeface(mSubTextTypeFace);
                    if(isSelectionMode){
                        macroItemViewHolder.checkBox.setVisibility(View.VISIBLE);
                        macroItemViewHolder.aSwitch.setVisibility(View.INVISIBLE);
                    } else{
                        macroItemViewHolder.checkBox.setVisibility(View.GONE);
                        macroItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    }
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

                    // Set the tags to identify the preset.
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
                    if(isSelectionMode){
                        presetItemViewHolder.checkBox.setVisibility(View.VISIBLE);
                        presetItemViewHolder.aSwitch.setVisibility(View.INVISIBLE);
                    } else{
                        presetItemViewHolder.checkBox.setVisibility(View.GONE);
                        presetItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    }
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

        private boolean isSelectionMode() {
            return isSelectionMode;
        }

        private void setSelectionMode(boolean selectionMode) {
            isSelectionMode = selectionMode;
            mAdapter.notifyDataSetChanged();
            if(selectionMode){
                enableSelectionMenuItem(true);
            }
            else{
                enableSelectionMenuItem(false);
                removeCheckedItems();
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

            // Local class containing the ip and command of a device.
            class DeviceIPAndCommand {
                String deviceIp;
                String command;

                public DeviceIPAndCommand(String deviceIp, String command) {
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

            // Send macro commands
            ArrayList<DeviceIPAndCommand> commandsList = new ArrayList<>();
            OnlineDevices onlineDevices = ((Application) getApplication()).getScannedDevices();

            if(!macro.getPresetList().isEmpty()){
                for (Preset preset : macro.getPresetList()) {
                    for (String deviceIP : preset.getDevicesGroup().getDeviceIPArrayList()) {
                        Device device = onlineDevices.getDeviceByIP(deviceIP);
                        if(device != null){
                            commandsList.add(new DeviceIPAndCommand(device.getIpAddress(), preset.getCommand()));
                        }
                    }
                }

                if(commandsList.size() > 0){
                    ExecutorService executorService = Executors.newFixedThreadPool(commandsList.size());
                    for(DeviceIPAndCommand deviceAndCommand :commandsList){
                        Runnable worker = new PostRequest(deviceAndCommand.getCommand(), deviceAndCommand.getDeviceIp());
                        executorService.execute(worker);
                    }
                    executorService.shutdown();
                }
            }
        }

        /**
         * Send the preset command to the devices
         * @param presetClicked the preset that has been activated
         */
        private void sendPresetCommandToDevices(Preset presetClicked){
            if(presetClicked != null){
                ArrayList<String> devicesIPs = presetClicked.getDevicesGroup().getDeviceIPArrayList();
                ArrayList<Device> onlineDevices = ((Application) getApplication()).getScannedDevices().getDevicesList();
                ArrayList<String> devicesIpAddresses = new ArrayList<>();

                // Get a list of the online devices inside the presets list.
                if(!onlineDevices.isEmpty()){
                    for(String deviceIP : devicesIPs){
                        for(Device device : onlineDevices){
                            if(device.getIpAddress().equals(deviceIP)){
                                devicesIpAddresses.add(device.getIpAddress());
                            }
                        }
                    }

                    // Send command to each online device.
                    if(!devicesIpAddresses.isEmpty()){
                        ExecutorService executorService = Executors.newFixedThreadPool(devicesIpAddresses.size());
                        for(String ipAddress: devicesIpAddresses){
                            Runnable worker = new PostRequest(presetClicked.getCommand(), ipAddress);
                            executorService.execute(worker);
                        }
                        executorService.shutdown();
                    }
                }
            }
        }

        /**
         * Turn on preset.
         * @param view the turn on preset switch
         * @param motionEvent type of motion event
         * @return event captured or not
         */
        private boolean itemOnGestureListener(View view, MotionEvent motionEvent){
            ImageView imageView = view.findViewById(R.id.preset_switch);
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled_black_36dp));
                    switchOnDevices((int) view.getTag(R.id.ID), (String) view.getTag(R.id.groupType));
                    return true;
                case MotionEvent.ACTION_UP:
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled_white_36dp));
                    return true;
                case MotionEvent.ACTION_MOVE:
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled_white_36dp));
                    return true;
            }
            return false;
        }

        /**
         * Add/remove a preset or macro from the list of selected items.
         * @param view the child view inside the recyclerview.
         */
        private void itemOnClick(View view){
            if(isSelectionMode){
                CheckBox cb = view.findViewById(R.id.item_checkbox);
                if(cb.isChecked()){
                    cb.setChecked(false);
                    if((view.getTag(R.id.groupType)).equals(Constants.PRESET)){
                        for(int i = 0; i < mSelectedPresets.size(); i++){
                            if(mSelectedPresets.get(i) == ((int) view.getTag(R.id.ID))){
                                mSelectedPresets.remove(i);
                            }
                        }
                    } else {
                        for(int i = 0; i < mSelectedMacros.size(); i++){
                            if(mSelectedMacros.get(i) == ((int) view.getTag(R.id.ID))){
                                mSelectedMacros.remove(i);
                            }
                        }
                    }
                }
                else{
                    cb.setChecked(true);
                    String type = (String)view.getTag(R.id.groupType);
                    if(type.equals(Constants.MACRO)){
                        mSelectedMacros.add((int) view.getTag(R.id.ID));
                    }
                    else{
                        mSelectedPresets.add((int) view.getTag(R.id.ID));
                    }
                }
            }
            checkNewMacroAvailability();
        }

        private void checkNewMacroAvailability(){
            if(mSelectedMacros.isEmpty() && !mSelectedPresets.isEmpty()){
                enableAddMacroMenuButton(true);
            } else {
                enableAddMacroMenuButton(false);
            }
        }

        private void enableAddMacroMenuButton(boolean enable){
            if(enable){
                mMenu.findItem(R.id.add_macro_button).setIcon(R.drawable.ic_add_circle_outline_white_24dp);
                isAddMacroAvailable = true;
            } else{
                mMenu.findItem(R.id.add_macro_button).setIcon(R.drawable.ic_add_circle_outline_black_24dp);
                isAddMacroAvailable = false;
            }
        }

        /**
         * Create and return the preset names in a Macro.
         */
        @NonNull
        private String getMacroItemSubString(Macro macro){
            StringBuffer stringBuffer = new StringBuffer();
            for(int i = 0; i < macro.getPresetList().size(); i++){
                stringBuffer.append((macro.getPresetList().get(i)).getPresetName());
                if(i != macro.getPresetList().size() - 1){
                    stringBuffer.append(", ");
                }
            }

            if(stringBuffer.length() > 50){
                stringBuffer = new StringBuffer(stringBuffer.substring(0, 50));
                stringBuffer.append(".....");
            }

            return stringBuffer.toString();
        }
    }
}
