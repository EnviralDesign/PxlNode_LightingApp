package aquilina.ryan.homelightingapp.ui.design_mode;

import com.google.gson.Gson;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;

import org.w3c.dom.Text;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllGroups;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Constants;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by SterlingRyan on 9/5/2017.
 */

public class DesignActivity extends MainActivity {

    private ArrayList<Device> mSingleItemList;
    private ArrayList<DevicesGroup> mGroupedItemList;
    private SharedPreferences mPrefs;

    private MaterialSpinner mSpinner;
    private Button mBlinkButton;
    private Button mHueButton;
    private Button mHueTwoButton;
    private Button mPulseButton;
    private NumberPickerView mDurationPicker;
    private NumberPickerView mRepetitionPicker;
    private Button mSavePresetButton;
    private EffectsTimelineView mEffectsTimeLineView;
    private LinearLayout mEffectsControlLayout;
    private ColorPicker mColorPicker;
    private RelativeLayout mHoloPickerControls;
    private SVBar mSvBar;
    private OpacityBar mOpBar;

    private View.OnClickListener mOnClickListener;

    private CustomSpinnerAdapter mCustomSpinnerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        getWindow().setBackgroundDrawable(null);
        // Set up views
        super.setSelectedNavMenuItem(R.id.nav_design);
        mColorPicker = (ColorPicker) findViewById(R.id.picker);
        mSvBar = (SVBar) findViewById(R.id.svbar);
        mOpBar = (OpacityBar) findViewById(R.id.opacitybar);
        mColorPicker.setShowOldCenterColor(false);
        mSpinner = (MaterialSpinner) findViewById(R.id.item_spinner);
        mBlinkButton = (Button) findViewById(R.id.blink_button);
        mHueButton = (Button) findViewById(R.id.hue_button);
        mHueTwoButton = (Button) findViewById(R.id.hue2_button);
        mPulseButton = (Button) findViewById(R.id.pulse_button);
        mDurationPicker = (NumberPickerView) findViewById(R.id.duration_picker);
        mRepetitionPicker = (NumberPickerView) findViewById(R.id.repetitions_picker);
        mEffectsTimeLineView = (EffectsTimelineView) findViewById(R.id.effects_timeline);
        mSavePresetButton = (Button) findViewById(R.id.save_preset_button);
        mEffectsControlLayout = (LinearLayout) findViewById(R.id.effects_controls_linear_layout);
        mHoloPickerControls = (RelativeLayout) findViewById(R.id.holo_picker_controls);
        TextView repetitionText = (TextView) findViewById(R.id.repetitions_textview);
        TextView durationText = (TextView) findViewById(R.id.duration_textview);

        // Load data
        mSingleItemList = new ArrayList<>();
        mGroupedItemList = new ArrayList<>();
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        setPickerProperties();

        // Set up view's functionality & design
        repetitionText.setTypeface(mSubTextTypeFace);
        durationText.setTypeface(mSubTextTypeFace);
        mColorPicker.addSVBar(mSvBar);
        mColorPicker.addOpacityBar(mOpBar);
        mColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if(mEffectsTimeLineView.getStopCircleViewFocus()){
                    mEffectsTimeLineView.changeStopCircleColor(color);
                }

                if(mEffectsTimeLineView.getStartCircleViewFocus()) {
                    mEffectsTimeLineView.changeStartCircleColor(color);
                }

                //TODO send post command changing color of the light in realtime
            }
        });
        mCustomSpinnerAdapter = new CustomSpinnerAdapter();
        mSpinner.setAdapter(mCustomSpinnerAdapter);
        mSpinner.setTypeface(mTextTypeFace);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Button) view).isSelected()){
                    ((Button) view).setSelected(false);
                    enableEffectsControl(false);
                    mEffectsTimeLineView.refreshView();
                }
                else {
                    mBlinkButton.setSelected(false);
                    mHueButton.setSelected(false);
                    mHueTwoButton.setSelected(false);
                    mPulseButton.setSelected(false);
                    ((Button) view).setSelected(true);
                    enableEffectsControl(true);
                }
                switch (view.getId()){
                    //TODO send post commands according to the button pressed
                }
            }
        };
        mBlinkButton.setOnClickListener(mOnClickListener);
        mBlinkButton.setTypeface(mHeaderTypeFace);
        mHueButton.setOnClickListener(mOnClickListener);
        mHueButton.setTypeface(mHeaderTypeFace);
        mHueTwoButton.setOnClickListener(mOnClickListener);
        mHueTwoButton.setTypeface(mHeaderTypeFace);
        mPulseButton.setOnClickListener(mOnClickListener);
        mPulseButton.setTypeface(mHeaderTypeFace);
        mSavePresetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSavePresetDialog();
            }
        });
        mSavePresetButton.setTypeface(mHeaderTypeFace);
        mEffectsTimeLineView.setmTypeface(mSubTextTypeFace);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadListsWithData();
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == -1){
                    hideAllLightingView();
                }
                else if (i == mGroupedItemList.size()){
                    hideAllLightingView();
                }
                else{
                    showAllLightingViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Shows the views
     */
    private void showAllLightingViews(){
        mHoloPickerControls.setVisibility(View.VISIBLE);
        mHoloPickerControls.setAlpha(0);
        mSvBar.setVisibility(View.VISIBLE);
        mSvBar.setAlpha(0);
        mOpBar.setVisibility(View.VISIBLE);
        mOpBar.setAlpha(0);
        mSavePresetButton.setVisibility(View.VISIBLE);
        mSavePresetButton.setAlpha(0);

        mHoloPickerControls.animate().setDuration(500).alpha(1).translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHoloPickerControls.setVisibility(View.VISIBLE);
            }
        });
        mSvBar.animate().setDuration(500).alpha(1).translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSvBar.setVisibility(View.VISIBLE);
            }
        });

        mOpBar.animate().setDuration(500).alpha(1).translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mOpBar.setVisibility(View.VISIBLE);
            }
        });
        mSavePresetButton.animate().setDuration(500).alpha(1).translationY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSavePresetButton.setVisibility(View.VISIBLE);
            }
        });
        enableEffectsControl(false);
    }

    /**
     * Hides all lighting views
     */
    private void hideAllLightingView(){
        mHoloPickerControls.setVisibility(View.GONE);
        mHoloPickerControls.animate().setDuration(500).translationY(-mSpinner.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mHoloPickerControls.setVisibility(View.GONE);
            }
        });
        mSvBar.setVisibility(View.GONE);
        mSvBar.animate().setDuration(500).translationY(-mSpinner.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSvBar.setVisibility(View.GONE);
            }
        });
        mOpBar.setVisibility(View.GONE);
        mOpBar.animate().setDuration(500).translationY(-mSpinner.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mOpBar.setVisibility(View.GONE);
            }
        });
        mSavePresetButton.setVisibility(View.GONE);
        mSavePresetButton.animate().setDuration(500).translationY(-mSpinner.getHeight()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSavePresetButton.setVisibility(View.GONE);
            }
        });
        enableEffectsControl(false);
    }

    /**
     *  Show save group dialog
     *  fragment.
     */
    private void showSavePresetDialog(){
        DialogFragment dialogFragment = AddPresetDialog.newInstance();
        dialogFragment.show(getFragmentManager(), "AddPresetDialog");
    }

    /**
     * Saves the preset locally
     */
    protected void savePresetLocally(String presetName){
        AllPresets allPresets;

        Preset preset = new Preset(presetName);

        int i = mSpinner.getSelectedItemPosition();
        if(i == 0){
            // TODO SNACKBAR
            return;
        } else if (i > 0 && i <= mGroupedItemList.size()){
            DevicesGroup devicesGroup = (DevicesGroup) mSpinner.getSelectedItem();
            for (int j = 0; j < devicesGroup.getDeviceArrayList().size(); j ++){
                preset.getDevicesList().add(devicesGroup.getDeviceArrayList().get(j));
            }
        } else if (i == (mGroupedItemList.size() + 1)){
            // TODO SNACKBAR
            return;
        } else {
            Device device = (Device) mSpinner.getSelectedItem();
            preset.getDevicesList().add(device);
        }

        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_PRESETS, null);

        if(json == null){
            allPresets = new AllPresets();
        } else {
            allPresets = gson.fromJson(json, AllPresets.class);
        }

        allPresets.addPreset(preset);
        json = gson.toJson(allPresets);
        prefsEditor.putString(Constants.GROUP_OF_PRESETS, json);
        prefsEditor.apply();
        refreshLayout();
        // TODO SNACKBAR
    }

    private void refreshLayout(){
        mEffectsTimeLineView.refreshView();
        mBlinkButton.setSelected(false);
        mHueButton.setSelected(false);
        mHueTwoButton.setSelected(false);
        mPulseButton.setSelected(false);
        enableEffectsControl(false);
        mSpinner.setSelection(0);
    }

    /**
     * Loads groups and fixtures from file
     * to lists
     */
    private void loadListsWithData(){
        Gson gson = new Gson();
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        String json = mPrefs.getString(Constants.GROUP_OF_DEVICES_GROUPS, null);
        AllGroups allGroups = (AllGroups) gson.fromJson(json, AllGroups.class);

        if(allGroups != null){
            mGroupedItemList = allGroups.getGroups();
        }

        json = mPrefs.getString(Constants.GROUP_OF_SINGLE_DEVICES, null);
        DevicesGroup singleGroup = gson.fromJson(json, DevicesGroup.class);

        if(singleGroup != null){
            mSingleItemList = singleGroup.getDeviceArrayList();
        }
    }

    /**
     * Set the number picker's properties.
     */
    private void setPickerProperties(){
        setPickerData(mDurationPicker, 1, 100, 1);
        setPickerData(mRepetitionPicker, 1, 100, 1);
    }

    private void setPickerData(NumberPickerView picker, int minValue, int maxValue, int value){
        String[] displayValues;
        ArrayList<String> valuesList = new ArrayList<>();
        for(int i = 0; i < 100; i++){
            valuesList.add(Integer.toString(i));
        }
        displayValues = valuesList.toArray(new String[valuesList.size()]);

        picker.setDisplayedValues(displayValues);
        picker.setMinValue(minValue);
        picker.setMaxValue(maxValue);
        picker.setValue(value);
    }

    /**
     * Sets the effects controls to clickable
     * or unclickable
     */
    private void enableEffectsControl(boolean enable){
        if(!enable){
            mSavePresetButton.animate().translationY(- mEffectsControlLayout.getHeight()).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mEffectsControlLayout.setVisibility(View.INVISIBLE);
                    mEffectsTimeLineView.setmStartCircleViewFocus(false);
                    mEffectsTimeLineView.setmStopCircleViewFocus(false);
                }
            });

        } else{
            mSavePresetButton.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mEffectsControlLayout.setVisibility(View.VISIBLE);
                    mEffectsTimeLineView.setmStopCircleViewFocus(true);
                }
            });
        }
    }

    /**
     * Customize the Spinner Adapter to allow for
     * two separate lists to be loaded under two
     * section headers.
     */
    private class CustomSpinnerAdapter implements SpinnerAdapter{
        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            if(i == 0){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_section_header, viewGroup, false);
                if(mGroupedItemList.isEmpty()){
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0,0);
                    view.findViewById(R.id.spinner_section_header_text_view).setLayoutParams(layoutParams);
                    view.setBackground(null);
                } else{
                    TextView textView = (TextView) view.findViewById(R.id.spinner_section_header_text_view);
                    textView.setTypeface(mTextTypeFace);
                    textView.setText(getString(R.string.spinner_group_section_header));
                }
                return view;
            } else if (i > 0 && i < mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mGroupedItemList.get(i - 1).getName());
                textView.setTypeface(mTextTypeFace);
                return view;
            } else if (i == mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                view.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
                textView.setTypeface(mTextTypeFace);
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else if (i == (mGroupedItemList.size() + 1)){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_section_header, viewGroup, false);
                if(mSingleItemList.isEmpty()) {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                    view.findViewById(R.id.spinner_section_header_text_view).setLayoutParams(layoutParams);
                    view.setBackground(null);
                } else {
                    TextView textView = (TextView) view.findViewById(R.id.spinner_section_header_text_view);
                    textView.setTypeface(mTextTypeFace);
                    textView.setText(getString(R.string.spinner_fixture_section_header));
                }
                return view;
            } else {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
                textView.setTypeface(mTextTypeFace);
                textView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getName());
                return view;
            }
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return mSingleItemList.size() + mGroupedItemList.size() + 2;
        }

        @Override
        public Object getItem(int i) {
            if(i > mGroupedItemList.size()){
                return mSingleItemList.get(i - mGroupedItemList.size());
            }
            return mGroupedItemList.get(i - 1);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_hint, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.spinner_hint);
            textView.setTypeface(mTextTypeFace);
            if(i == 0 || i == (mGroupedItemList.size() + 1)){
                textView.setText(getString(R.string.spinner_hint));
                return view;
            } else if (i > 0 && i <= mGroupedItemList.size()){
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else {
                textView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getName());
                return view;
            }
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }


    }
}
