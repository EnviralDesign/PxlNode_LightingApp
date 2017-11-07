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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aquilina.ryan.homelightingapp.Application;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllMacros;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.Macro;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.model.ScannedDevices;
import aquilina.ryan.homelightingapp.ui.design_mode.DesignActivity;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Common;
import aquilina.ryan.homelightingapp.utils.Constants;

public class LightingModeActivity extends MainActivity {
    private RecyclerView mPresetsRecyclerView;
    private Menu mMenu;
    private TextView mHintTextView;

    private ArrayList<Preset> mPresets;
    private ArrayList<Integer> mSelectedPresets;
    private ArrayList<Macro> mMacros;
    private ArrayList<Integer> mSelectedMacros;
    private PresetAdapter mAdapter;

    private Common common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lighting_mode);
        common = new Common();

        // Set views.
        mPresetsRecyclerView = findViewById(R.id.presets_recycler_list);
        mHintTextView = findViewById(R.id.text_view_hint);

        // Set view's data/design
        mPresets = new ArrayList<>();
        mMacros = new ArrayList<>();
        mSelectedPresets = new ArrayList<>();
        mSelectedMacros = new ArrayList<>();
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
    public void onBackPressed() {
        if(mAdapter.isDeleteMode()){
            mAdapter.setDeleteMode(false);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh_lights, menu);
        mMenu = menu;
        enableDeleteMenuItem(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mAdapter.setDeleteMode(false);
                return true;
            case R.id.delete_button:
                deleteCheckedItems();
                return true;
            case R.id.refresh_button:
                switchOffLights();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get a list of online devices.
     */
    private ArrayList<Device> getOnlineDevice(){
        ScannedDevices scannedDevices = ((Application)getApplication()).getScannedDevices();

        if(scannedDevices != null){
            return scannedDevices.getDevicesList();
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

    /**
     * Deletes all items in mSelected Items.
     */
    private void deleteCheckedItems(){
        if(!mSelectedPresets.isEmpty()){
            ArrayList<Preset> presets = common.loadPresets(this);

            for(int id: mSelectedPresets){
                for(int i = 0; i <= presets.size(); i++){
                    Preset preset = presets.get(i);
                    if(preset.getId() == id){
                        presets.remove(preset);
                        presets = common.arrangePresetsIds(presets, i);
                        break;
                    }
                }
            }

            mMacros = common.removePresetsFromMacros(mSelectedPresets,this);
            mSelectedPresets.clear();
            mPresets = common.savePresetList(presets, this);
        }

        if(!mSelectedMacros.isEmpty()){
            ArrayList<Macro> macros = common.loadMacros(this);

            for(int id: mSelectedMacros){
                for(int i = 0; i <= macros.size(); i++){
                    Macro macro = macros.get(i);
                    if(macro.getId() == id){
                        macros.remove(macro);
                        macros = common.arrangeMacrosIds(macros, i);
                        break;
                    }
                }
            }

            mSelectedMacros.clear();
            mMacros = common.savePresetsInMacro(macros,this);;
        }

        mAdapter.setDeleteMode(false);
        mAdapter.notifyDataSetChanged();
        if(mMacros.isEmpty() && mPresets.isEmpty()){
            mHintTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Enable or disable delete button in app bar.
     */
    private void enableDeleteMenuItem(boolean enable){
        if(enable){
            mMenu.findItem(R.id.delete_button).setVisible(true);
        }
        else{
            mMenu.findItem(R.id.delete_button).setVisible(false);
        }
    }

    /**
     * Refresh the recycler view to un-check all checked items.
     */
    private void removeCheckItems(){
        mSelectedPresets.clear();
        mSelectedMacros.clear();
        mAdapter = new PresetAdapter();
        mPresetsRecyclerView.setAdapter(mAdapter);
    }



    private static class TitleViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;

        private TitleViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.header_title);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener{
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
            cardView.setOnLongClickListener(this);
            aSwitch.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardViewClick(view);
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onCardViewLongClick(view);
            return true;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mListener.onCardGestureListener(view, motionEvent);
        }

        private interface ViewHolderClickListener{
            void onCardViewClick(View view);
            void onCardViewLongClick(View view);
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
                itemOnClick(view);
            }

            @Override
            public void onCardViewLongClick(View view) {
                itemOnLongClick(view);
            }

            @Override
            public boolean onCardGestureListener(View view, MotionEvent motionEvent) {
                return itemOnGestureListener(view, motionEvent);
            }
        };

        private boolean isDeleteMode = false;

        private boolean isDeleteMode() {
            return isDeleteMode;
        }

        private void setDeleteMode(boolean deleteMode) {
            isDeleteMode = deleteMode;
            mAdapter.notifyDataSetChanged();
            if(deleteMode){
                enableDeleteMenuItem(true);
                enableBackButton(true);
            }
            else{
                enableDeleteMenuItem(false);
                enableBackButton(false);
                removeCheckItems();
            }
        }

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
                    if(isDeleteMode){
                        macroItemViewHolder.checkBox.setVisibility(View.VISIBLE);
                        macroItemViewHolder.aSwitch.setVisibility(View.INVISIBLE);
                    } else{
                        macroItemViewHolder.checkBox.setVisibility(View.GONE);
                        macroItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    }
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
                        int id = preset.getDevicesGroup().getDeviceArrayList().get(0);
                        Device device = ((Application)getApplicationContext()).getDeviceById(id);
                        name = device.getName();
                    }
                    presetItemViewHolder.groupTextView.setText(name);
                    presetItemViewHolder.groupTextView.setTypeface(mSubTextTypeFace);
                    if(isDeleteMode){
                        presetItemViewHolder.checkBox.setVisibility(View.VISIBLE);
                        presetItemViewHolder.aSwitch.setVisibility(View.INVISIBLE);
                    } else{
                        presetItemViewHolder.checkBox.setVisibility(View.GONE);
                        presetItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    }
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

        private void itemOnClick(View view){
            if(isDeleteMode){
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
                    for(Preset preset : clickedMacro.getPresetList()){
                        sendPresetCommandToDevices(preset);
                    }
                }
            }
        }

        /**
         * Send the preset command to the devices
         * @param presetClicked the preset that has been activated
         */
        private void sendPresetCommandToDevices(Preset presetClicked){
            ArrayList<Integer> devicesIds = presetClicked.getDevicesGroup().getDeviceArrayList();
            ArrayList<Device> devices = ((Application) getApplication()).getScannedDevices().getDevicesList();
            ArrayList<String> devicesIpAddresses = new ArrayList<>();

            for(int deviceID: devicesIds){
                for(Device device : devices){
                    if(device.getId() == deviceID){
                       devicesIpAddresses.add(device.getIpAddress());
                    }
                }
            }

            ExecutorService executorService = Executors.newFixedThreadPool(devicesIpAddresses.size());
            for(String ipAddress: devicesIpAddresses){
                Runnable worker = new WorkerThread(presetClicked.getCommand(), ipAddress);
                executorService.execute(worker);
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
                Log.d("PostCommand", "to " + ipAddress + " with command : " + command);
                AndroidNetworking.post("http://" + ipAddress + "/play")
                        .addByteBody(command.getBytes())
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

        private void itemOnLongClick(View view){
            setDeleteMode(true);
            ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);

            String type = (String)view.getTag(R.id.groupType);
            if(type.equals(Constants.MACRO)){
                mSelectedMacros.add((int) view.getTag(R.id.ID));
            }
            else{
                mSelectedPresets.add((int) view.getTag(R.id.ID));
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
    }
}
