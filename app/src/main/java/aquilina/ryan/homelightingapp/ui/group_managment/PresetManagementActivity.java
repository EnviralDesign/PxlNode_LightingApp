/*
 * Created by Ryan Aquilina on 11/2/17 10:43 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 11/2/17 10:43 AM
 */

package aquilina.ryan.homelightingapp.ui.group_managment;

import com.google.gson.Gson;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllMacros;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.Macro;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.ui.group_managment.AddMacroDialog;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Common;
import aquilina.ryan.homelightingapp.utils.Constants;

public class PresetManagementActivity extends MainActivity {

    private Menu mMenu;
    private LinearLayout mHintTextView;
    private RecyclerView mGroupsRecyclerView;
    private ArrayList<Preset> mPresets;
    private ArrayList<Integer> mSelectedPresets;
    private HashMap<String, Device> mDevicesSparseArray;
    private GroupsAdapter mAdapter;
    private Button mSaveButton;
    private Common common;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        // Set views
        mGroupsRecyclerView = findViewById(R.id.groups_recycler_list);
        mHintTextView = findViewById(R.id.text_view_hint);
        mSaveButton = findViewById(R.id.save_macro_button);
        mTitleTextView.setText(R.string.presets_title);

        // Set view's data
        mPresets = new ArrayList<>();
        mSelectedPresets = new ArrayList<>();
        mAdapter = new GroupsAdapter();
        mDevicesSparseArray = new HashMap<>();
        common = new Common();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = AddMacroDialog.newInstance();
                dialogFragment.show(getFragmentManager(), "AddMacroDialog");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresets = loadPresets();
        mDevicesSparseArray = common.loadDevices(this);
        mAdapter.notifyDataSetChanged();

        mNavigationView.setCheckedItem(R.id.nav_group_presets);
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
        getMenuInflater().inflate(R.menu.edit_mode, menu);
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
                deleteCheckedGroups();
                mAdapter.setDeleteMode(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Delete checked groups.
     */
    private void deleteCheckedGroups(){
        ArrayList<Preset> presets;
        presets = loadPresets();

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

        common.removePresetsFromMacros(mSelectedPresets,this);
        mSelectedPresets.clear();

        mPresets = common.savePresetList(presets, this);
        if(mPresets != null){
            if(mPresets.isEmpty()){
                mHintTextView.setVisibility(View.VISIBLE);
            } else {
                mHintTextView.setVisibility(View.GONE);
            }
        } else {
            mHintTextView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Enables/Disables the delete menu item.
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
     * Load presets from SharedPreferences.
     */
    private ArrayList<Preset> loadPresets(){
        Gson gson = new Gson();
        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);
        String json = mPrefs.getString(Constants.GROUP_OF_PRESETS, null);
        AllPresets allPresets = gson.fromJson(json, AllPresets.class);

        if(allPresets != null){
            if(!allPresets.getAllPresets().isEmpty()){
                mHintTextView.setVisibility(View.GONE);
            }
            return allPresets.getAllPresets();
        }
        return null;
    }

    /**
     * Save presets as macro.
     */
    public void saveMacro(String macroName){
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
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
        mAdapter.setDeleteMode(false);
        mAdapter.notifyDataSetChanged();
        common.showToast(this, getString(R.string.toast_macro_saved));
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView textView;
        TextView subTextView;
        CardView cardView;
        CheckBox checkBox;
        ViewHolder.ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolder.ViewHolderClickListener mListener) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.group_name);
            this.subTextView = itemView.findViewById(R.id.associated_devices);
            this.cardView = itemView.findViewById(R.id.item_card_view);
            this.checkBox = itemView.findViewById(R.id.item_checkbox);
            this.mListener = mListener;
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
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

        public interface ViewHolderClickListener{
            void onCardViewClick(View view);
            void onCardViewLongClick(View view);
        }
    }

    private class GroupsAdapter extends RecyclerView.Adapter<ViewHolder>{

        private boolean isDeleteMode = false;

        public boolean isDeleteMode() {
            return isDeleteMode;
        }

        public void setDeleteMode(boolean deleteMode) {
            isDeleteMode = deleteMode;
            mAdapter.notifyDataSetChanged();
            if(deleteMode){
                enableDeleteMenuItem(true);
            }
            else{
                enableDeleteMenuItem(false);
                mSelectedPresets.clear();
                mAdapter = new GroupsAdapter();
                mGroupsRecyclerView.setAdapter(mAdapter);
                mSaveButton.setVisibility(View.GONE);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new ViewHolder(itemView, new ViewHolder.ViewHolderClickListener() {
                @Override
                public void onCardViewClick(View view) {
                    if(isDeleteMode){
                        CheckBox cb = view.findViewById(R.id.item_checkbox);
                        if(cb.isChecked()){
                            cb.setChecked(false);
                            for(int i = 0; i < mSelectedPresets.size(); i++){
                                if(mSelectedPresets.get(i) == view.getTag()){
                                    mSelectedPresets.remove(i);
                                }
                            }
                            if(mSelectedPresets.isEmpty()){
                                mSaveButton.setVisibility(View.GONE);
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mSelectedPresets.add((Integer) view.getTag());
                            mSaveButton.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mSelectedPresets.add((Integer) view.getTag());
                    mSaveButton.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Preset preset = mPresets.get(position);

            holder.cardView.setTag(preset.getId());
            holder.textView.setText(preset.getPresetName());
            holder.textView.setTypeface(mTextTypeFace);
            holder.subTextView.setText(getPresetItemsSubString(preset));
            if(isDeleteMode){
                holder.checkBox.setVisibility(View.VISIBLE);
            } else{
                holder.checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if(mPresets == null){
                return 0;
            }
            return mPresets.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        /**
         * Create and return the device name in a preset.
         */
        private String getPresetItemsSubString(Preset preset){
            String subText;
            ArrayList<String> devicesIPList = preset.getDevicesGroup().getDeviceIPArrayList();
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < devicesIPList.size(); i++){
                String deviceIP = devicesIPList.get(i);
                if(deviceIP != null){
                    String name = mDevicesSparseArray.get(deviceIP).getName();
                    if(name != null){
                        builder.append(name);
                        if(i != devicesIPList.size() - 1){
                            builder.append(", ");
                        }
                    }
                }
            }

            subText = builder.toString();
            if(subText.length() > 40){
                subText = subText.substring(0, 40);
                subText += ".....";
            }

            return subText;
        }
    }
}
