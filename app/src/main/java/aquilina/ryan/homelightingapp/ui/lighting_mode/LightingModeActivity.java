package aquilina.ryan.homelightingapp.ui.lighting_mode;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
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

import java.util.ArrayList;
import java.util.Iterator;

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
 * Created by SterlingRyan on 9/5/2017.
 */

public class LightingModeActivity extends MainActivity {
    private RecyclerView mPresetsRecyclerView;
    private Menu mMenu;

    private ArrayList<Preset> mPresets;
    private ArrayList<Integer> mSelectedPresets;
    private ArrayList<Macro> mMacros;
    private ArrayList<Integer> mSelectedMacros;
    private PresetAdapter mAdapter;

    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        // Set views.
        mPresetsRecyclerView = (RecyclerView) findViewById(R.id.presets_recycler_list);

        // Set view's data/design
        mPresets = new ArrayList<>();
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
        mPresets = loadPresets();
        mMacros = loadMacros();
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
                //TODO send all lights blank command
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes all items in mSelected Items.
     */
    private void deleteCheckedItems(){
        if(!mSelectedPresets.isEmpty()){
            ArrayList<Preset> presets = loadPresets();

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
            mSelectedPresets.clear();
            savePresetList(presets);
        }

        if(!mSelectedMacros.isEmpty()){
            ArrayList<Macro> macros = loadMacros();

            for(int id: mSelectedMacros){
                for(int i = 0; i <= macros.size(); i++){
                    Macro macro = macros.get(i);
                    if(macro.getId() == id){
                        macros.remove(macro);
                        macros = arrangeMacrosIds(macros, i);
                        break;
                    }
                }
            }

            mSelectedMacros.clear();
            saveMacroList(macros);
        }

        mAdapter.setDeleteMode(false);
        mAdapter.notifyDataSetChanged();
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
     * Arranges the id of the remaining macros
     */
    private ArrayList<Macro> arrangeMacrosIds(ArrayList<Macro> macros, int i){
        for (int j = i; j < macros.size(); j++ ){
            macros.get(j).setId(j);
        }
        return macros;
    }

    /**
     * Remove presets from macros
     */
    private void removePresetsFromMacros(ArrayList<Integer> presetsToDelete){
        Macro currentMacro;
        ArrayList<Macro> macros = loadMacros();
        Iterator<Macro> macroIterator = macros.iterator();

        while(macroIterator.hasNext()){
            Macro macro = macroIterator.next();
            ArrayList<Preset> presets = macro.getPresetList();
            Iterator<Preset> presetsIterator = presets.iterator();

            for (int id: presetsToDelete) {
                while (presetsIterator.hasNext()){
                    Preset preset = presetsIterator.next();
                    if(preset.getId() == id){
                        macro.removePreset(preset);
                        currentMacro = macro;
                        saveMacro(currentMacro);
                        if(macro.getPresetList().isEmpty()){
                            macroIterator.remove();
                        }
                        break;
                    }
                }
            }
        }
        mMacros = macros;
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


    /**
     * Save presets list after it has been edited.
     * @param presets
     */
    private void savePresetList(ArrayList<Preset> presets){
        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        AllPresets allPresets = new AllPresets(presets);

        String json = gson.toJson(allPresets);
        prefsEditor.putString(Constants.GROUP_OF_PRESETS, json);
        prefsEditor.apply();
        mPresets = presets;
    }

    /**
     * Save presets as macro
     */
    protected void saveMacroList(ArrayList<Macro> macros){
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

        allMacros.setMacros(macros);

        json = gson.toJson(allMacros);
        prefsEditor.putString(Constants.GROUP_OF_MACROS, json);
        prefsEditor.apply();
        mMacros = macros;
    }

    /**
     * Enable or disable delete button in app bar.
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

        public TitleViewHolder(View itemView) {
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

        public ItemViewHolder(View itemView, ViewHolderClickListener mListener) {
            super(itemView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.preset_name);
            this.groupTextView = (TextView) itemView.findViewById(R.id.associated_group);
            this.aSwitch = (ImageView) itemView.findViewById(R.id.preset_switch);
            this.cardView = (LinearLayout) itemView.findViewById(R.id.item_card_view);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
            this.mListener = mListener;
            aSwitch.setOnClickListener(this);
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

        public interface ViewHolderClickListener{
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

        public boolean isDeleteMode() {
            return isDeleteMode;
        }

        public void setDeleteMode(boolean deleteMode) {
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
                    macroTitleViewHolder.titleTextView.setText(getString(R.string.recyclerview_macros_title));
                    macroTitleViewHolder.titleTextView.setTypeface(mHeaderTypeFace);
                    break;
                }
                case MACRO_ITEM:{
                    ItemViewHolder macroItemViewHolder = (ItemViewHolder) holder;
                    Macro macro = mMacros.get(position - 1);
                    macroItemViewHolder.cardView.setTag(R.id.ID, macro.getId());
                    macroItemViewHolder.cardView.setTag(R.id.groupType, Constants.MACRO);
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
                    presetTitleViewHolder.titleTextView.setText(getString(R.string.recyclerview_presets_title));
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
                    presetItemViewHolder.nameTextView.setText(preset.getPresetName());
                    presetItemViewHolder.nameTextView.setTypeface(mTextTypeFace);
                    String name = preset.getDevicesGroup().getName();
                    if(name == null){
                        int id = (int) preset.getDevicesGroup().getDeviceArrayList().get(0);
                        Device device = (Device) ((Application)getApplicationContext()).getDeviceById(id);
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
                CheckBox cb = (CheckBox) view.findViewById(R.id.item_checkbox);
                if(cb.isChecked()){
                    cb.setChecked(false);
                    if(((String)view.getTag(R.id.groupType)).equals(Constants.PRESET)){
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
            } else{
                //TODO switch on devices
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
            ImageView imageView = (ImageView) view.findViewById(R.id.preset_switch);
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled_black_36dp));
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

        private String getMacroItemSubString(Macro macro){
            String subText = "";
            for(int i = 0; i < macro.getPresetList().size(); i++){
                subText += ((Preset) macro.getPresetList().get(i)).getPresetName();
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
