package aquilina.ryan.homelightingapp.ui.presets_mode;

import com.google.gson.Gson;

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
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Constants;

/**
 * Created by SterlingRyan on 9/5/2017.
 */

public class PresetsActivity extends MainActivity {
    private RecyclerView mPresetsRecyclerView;
    private ArrayList<Preset> mPresets;
    private ArrayList<String> mToDeletePresets;
    private PresetAdapter mAdapter;
    private Menu mMenu;

    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        mPresets = new ArrayList<>();
        mToDeletePresets = new ArrayList<>();
        mPresetsRecyclerView = (RecyclerView) findViewById(R.id.presets_recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mPresetsRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PresetAdapter();
        mPresetsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresets = loadPresets();
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
                deleteCheckedItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCheckedItems(){
        ArrayList<Preset> presets;
        presets = loadPresets();

        for(String string: mToDeletePresets){
            for(Preset preset: presets){
                if(preset.getPresetName().equals(string)){
                    presets.remove(preset);
                    break;
                }
            }
        }

        savePresetList(presets);
    }

    private void savePresetList(ArrayList<Preset> presets){
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
    private ArrayList<Preset> loadPresets(){
        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_PRESETS, null);

        AllPresets allPresets = (AllPresets) gson.fromJson(json, AllPresets.class);
        ArrayList<Preset> presets = new ArrayList<>();

        if(allPresets != null){
            for (int i = 0; i < allPresets.getAllPresets().size(); i++){
                presets.add(allPresets.getAllPresets().get(i));
            }
        }
        return presets;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView nameTextView;
        TextView groupTextView;
        Switch aSwitch;
        CardView cardView;
        CheckBox checkBox;
        ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolderClickListener mListener) {
            super(itemView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.preset_name);
            this.groupTextView = (TextView) itemView.findViewById(R.id.associated_group);
            this.aSwitch = (Switch) itemView.findViewById(R.id.preset_switch);
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

    private class PresetAdapter extends RecyclerView.Adapter<ViewHolder>{

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
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset, parent, false);
            return new ViewHolder(itemView, new ViewHolder.ViewHolderClickListener() {
                @Override
                public void onCardViewClick(View view) {
                    if(isDeleteMode){
                        CheckBox cb = (CheckBox) view.findViewById(R.id.item_checkbox);
                        if(cb.isChecked()){
                            cb.setChecked(false);
                            for(int i = 0; i < mToDeletePresets.size(); i++){
                                if(mToDeletePresets.get(i).equals((String) view.getTag())){
                                    mToDeletePresets.remove(i);
                                }
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mToDeletePresets.add(((TextView)view.findViewById(R.id.preset_name)).getText().toString());
                        }
                    } else{
                        Switch sw = (Switch) view.findViewById(R.id.preset_switch);
                        if(sw.isChecked()){
                            sw.setChecked(false);
                            //TODO switch off devices
                        }
                        else{
                            sw.setChecked(true);
                            //TODO switch on devices
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mToDeletePresets.add((String) view.getTag());
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Preset preset = mPresets.get(position);

            holder.cardView.setTag(preset.getPresetName());
            holder.nameTextView.setText(preset.getPresetName());
            holder.nameTextView.setTypeface(mTextTypeFace);
            holder.groupTextView.setText(preset.getDevicesGroup().getName());
            holder.groupTextView.setTypeface(mSubTextTypeFace);
            if(isDeleteMode){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.aSwitch.setVisibility(View.INVISIBLE);
            } else{
                holder.checkBox.setVisibility(View.GONE);
                holder.aSwitch.setVisibility(View.VISIBLE);
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
