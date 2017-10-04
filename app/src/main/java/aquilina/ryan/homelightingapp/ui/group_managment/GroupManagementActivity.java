package aquilina.ryan.homelightingapp.ui.group_managment;

import com.google.gson.Gson;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import aquilina.ryan.homelightingapp.Application;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllMacros;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.Macro;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Constants;

/**
 * Created by SterlingRyan on 9/15/2017.
 */

public class GroupManagementActivity extends MainActivity {
    private RecyclerView mGroupsRecyclerView;
    private Menu mMenu;
    private Button mSaveMacroButton;

    private ArrayList<Preset> mPresets;
    private ArrayList<Integer> mSelectedPresets;
    private GroupsAdapter mAdapter;

    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Set views
        mGroupsRecyclerView = (RecyclerView) findViewById(R.id.groups_recycler_list);
        mSaveMacroButton = (Button) findViewById(R.id.save_macro_button);

        // Set view's data
        mPresets = new ArrayList<>();
        mSelectedPresets = new ArrayList<>();
        mSelectedPresets = new ArrayList<>();
        mAdapter = new GroupsAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);

        // Set view's functionality
        mSaveMacroButton.setOnClickListener(new View.OnClickListener() {
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
        mPresets = loadGroups();
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
        presets = loadGroups();

        for(int id: mSelectedPresets){
            for(int i = 0; i <= presets.size(); i++){
                Preset preset = presets.get(i);
                if(preset.getId() == id){
                    presets.remove(preset);
                    presets = arrangePresetsIds(presets, i);
                    break;
                }
            }
        }

        removePresetsFromMacros(mSelectedPresets);
        saveGroupList(presets);
    }

    /**
     * Arranges the id of the remaining presets
     */
    private ArrayList<Preset> arrangePresetsIds(ArrayList<Preset> presets, int i){
        for (int j = i; j < presets.size(); j++ ){
            presets.get(j).setId(j);
        }
        return presets;
    }

    /**
     * Remove presets from macros
     */
    private void removePresetsFromMacros(ArrayList<Integer> presetsToDelete){
        Macro currentMacro;
        ArrayList<Macro> macros = loadMacros();
        for (Macro macro: macros) {
            ArrayList<Preset> presets = macro.getPresetList();
            for (int id: presetsToDelete) {
                for (Preset preset: presets) {
                    if(preset.getId() == id){
                        macro.removePreset(preset);
                        currentMacro = macro;
                        saveMacro(currentMacro);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Load all saved macros
     */
    private ArrayList<Macro> loadMacros(){
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_MACROS, null);

        if(json == null){
            return new ArrayList<>();
        } else {
            return ((AllMacros) gson.fromJson(json, AllMacros.class)).getMacros();
        }
    }

    /**
     * Once the checked groups are deleted, save the changes.
     * @param presets
     */
    private void saveGroupList(ArrayList<Preset> presets){
        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        AllPresets allPresets = new AllPresets(presets);

        String json = gson.toJson(allPresets);
        prefsEditor.putString(Constants.GROUP_OF_PRESETS, json);
        prefsEditor.apply();
        mPresets = presets;
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Enables/Disables the delete menu item.
     * @param enable
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
     * Load presets from SharedPreferences
     */
    private ArrayList<Preset> loadGroups(){
        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_PRESETS, null);

        AllPresets allPresets = (AllPresets) gson.fromJson(json, AllPresets.class);
        ArrayList<Preset> presets = new ArrayList<>();

        if(allPresets != null){
            return allPresets.getAllPresets();
        }
        return presets;
    }

    /**
     * Save presets as macro
     */
    protected void saveMacro(String macroName){
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllMacros allMacros;
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_MACROS, null);

        if(json == null){
            allMacros = new AllMacros();
        } else {
            allMacros = (AllMacros) gson.fromJson(json, AllMacros.class);
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
    }

    protected void saveMacro(Macro editedMacro){
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllMacros allMacros;
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_MACROS, null);

        if(json == null){
            allMacros = new AllMacros();
        } else {
            allMacros = (AllMacros) gson.fromJson(json, AllMacros.class);
        }

        if(allMacros != null){
            for (Macro macro: allMacros.getMacros()) {
                if(macro.getId() == editedMacro.getId()){
                    macro.setPresetList(editedMacro.getPresetList());
                    if(macro.getPresetList().isEmpty()){
                        allMacros.removeMacro(macro);
                    }
                    break;
                }
            }
        }
        json = gson.toJson(allMacros);
        prefsEditor.putString(Constants.GROUP_OF_MACROS, json);
        prefsEditor.apply();
    }

    private void removeCheckedItems(){
        mSelectedPresets.clear();
        mAdapter = new GroupsAdapter();
        mGroupsRecyclerView.setAdapter(mAdapter);
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView textView;
        TextView subTextView;
        CardView cardView;
        CheckBox checkBox;
        ViewHolder.ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolder.ViewHolderClickListener mListener) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.group_name);
            this.subTextView = (TextView) itemView.findViewById(R.id.associated_devices);
            this.cardView = (CardView) itemView.findViewById(R.id.item_card_view);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
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
                mSaveMacroButton.setVisibility(View.VISIBLE);
                enableBackButton(true);
            }
            else{
                enableDeleteMenuItem(false);
                mSaveMacroButton.setVisibility(View.GONE);
                enableBackButton(false);
                removeCheckedItems();
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new ViewHolder(itemView, new ViewHolder.ViewHolderClickListener() {
                @Override
                public void onCardViewClick(View view) {
                    if(isDeleteMode){
                        CheckBox cb = (CheckBox) view.findViewById(R.id.item_checkbox);
                        if(cb.isChecked()){
                            cb.setChecked(false);
                            for(int i = 0; i < mSelectedPresets.size(); i++){
                                if(mSelectedPresets.get(i) == ((int) view.getTag())){
                                    mSelectedPresets.remove(i);
                                }
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mSelectedPresets.add((int) view.getTag());
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mSelectedPresets.add((int) view.getTag());
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Preset preset = mPresets.get(position);

            holder.cardView.setTag(preset.getId());
            holder.textView.setText(preset.getPresetName());
            holder.textView.setTypeface(mTextTypeFace);
            String name = preset.getDevicesGroup().getName();
            if(name == null){
                int id =  preset.getDevicesGroup().getDeviceArrayList().get(0);
                Device device = (Device) ((Application) getApplicationContext()).getDeviceById(id);
                name = device.getName();
            }
            holder.subTextView.setText(name);
            holder.subTextView.setTypeface(mSubTextTypeFace);
            if(isDeleteMode){
                holder.checkBox.setVisibility(View.VISIBLE);
            } else{
                holder.checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
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
    }
}
