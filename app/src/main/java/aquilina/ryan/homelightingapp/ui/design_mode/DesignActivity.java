package aquilina.ryan.homelightingapp.ui.design_mode;

import com.google.gson.Gson;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllGroups;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
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

    private View.OnClickListener mOnClickListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        // Set up views
        super.setSelectedNavMenuItem(R.id.nav_design);
        ColorPicker colorPicker = (ColorPicker) findViewById(R.id.picker);
        SVBar saturationValueBar = (SVBar) findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        colorPicker.setShowOldCenterColor(false);
        mSpinner = (MaterialSpinner) findViewById(R.id.item_spinner);
        mBlinkButton = (Button) findViewById(R.id.blink_button);
        mHueButton = (Button) findViewById(R.id.hue_button);
        mHueTwoButton = (Button) findViewById(R.id.hue2_button);
        mPulseButton = (Button) findViewById(R.id.pulse_button);
        mDurationPicker = (NumberPickerView) findViewById(R.id.duration_picker);
        mRepetitionPicker = (NumberPickerView) findViewById(R.id.repetitions_picker);

        // Load data
        mSingleItemList = new ArrayList<>();
        mGroupedItemList = new ArrayList<>();
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        loadListsWithData();
        setPickerProperties();

        // Set up view's functionality
        colorPicker.addSVBar(saturationValueBar);
        colorPicker.addOpacityBar(opacityBar);
        colorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                //TODO send post command changing color of the light in realtime
            }
        });
        mSpinner.setAdapter(new CustomSpinnerAdapter());
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((Button) view).isSelected()){
                    ((Button) view).setSelected(false);
                }
                else {
                    mBlinkButton.setSelected(false);
                    mHueButton.setSelected(false);
                    mHueTwoButton.setSelected(false);
                    mPulseButton.setSelected(false);
                    ((Button) view).setSelected(true);
                }
                switch (view.getId()){
                    //TODO send post commands according to the button pressed
                }
            }
        };
        mBlinkButton.setOnClickListener(mOnClickListener);
        mHueButton.setOnClickListener(mOnClickListener);
        mHueTwoButton.setOnClickListener(mOnClickListener);
        mPulseButton.setOnClickListener(mOnClickListener);
    }


    /**
     * Loads groups and fixtures from file
     * to lists
     */
    private void loadListsWithData(){
        Gson gson = new Gson();
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

    private void setPickerProperties(){
        setData(mDurationPicker, 1, 100, 1);
        setData(mRepetitionPicker, 1, 100, 1);
    }

    private void setData(NumberPickerView picker, int minValue, int maxValue, int value){
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
                    textView.setText(getString(R.string.spinner_group_section_header));
                }
                return view;
            } else if (i > 0 && i < mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else if (i == mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                view.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
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
                    textView.setText(getString(R.string.spinner_fixture_section_header));
                }
                return view;
            } else {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
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
            return mGroupedItemList.get(i);
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

            if(i == 0 || i == (mGroupedItemList.size() + 1)){;
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
