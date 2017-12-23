/*
 * Created by Ryan Aquilina on 10/25/17 10:36 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/25/17 10:25 AM
 */

package aquilina.ryan.homelightingapp.ui.design_mode;

import com.google.gson.Gson;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aquilina.ryan.homelightingapp.Application;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllGroups;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.model.OnlineDevices;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Common;
import aquilina.ryan.homelightingapp.utils.Constants;
import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import fr.ganfra.materialspinner.MaterialSpinner;

public class DesignActivity extends MainActivity {

    private static final String DEFAULT_EFFECT = Constants.DESIGN_EFFECT_NONE;
    private static final String DEFAULT_COMMAND = "hue rgb255,255,255 t1 f1";

    private ArrayList<Device> mSingleItemList;
    private ArrayList<DevicesGroup> mGroupedItemList;
    private ArrayList<String> mSelectedDevices;

    private MaterialSpinner mSpinner;
    private ColorPicker mColorPicker;
    private Button mBlinkButton;
    private Button mHueButton;
    private Button mHueTwoButton;
    private Button mPulseButton;
    private Button mHueHsbButton;
    private Button mHueHslButton;
    private NumberPickerView mDurationPicker;
    private NumberPickerView mRepetitionPicker;
    private Button mSavePresetButton;
    private Button mPreviewPresetButton;
    private EffectsTimelineView mEffectsTimeLineView;
    private LinearLayout mEffectsControlLinearLayout;
    private RelativeLayout mHoloPickerControlsRelativeLayout;
    private TextView mHintTextView;
    private SaturationBar mSaturationBar;
    private ValueBar mValueBar;

    private ExecutorService executorService;
    private SharedPreferences mPrefs;
    private HoverThread hoverThread;
    private Common common;

    private int repetition;
    private int duration;
    private int startColor;
    private int stopColor;
    private int currentSpinnerPosition = 0;
    private String currentEffect = DEFAULT_EFFECT;
    private String currentCommand = DEFAULT_COMMAND;

    private long lastTime = 0;
    private Boolean isPreviousEffectAvailable = false;
    private Boolean isFirstChange = true;
    private boolean isHovering = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);
        getWindow().setBackgroundDrawable(null);

        // Set up views
        super.setSelectedNavMenuItem(R.id.nav_design);
        mNavigationView.setCheckedItem(R.id.nav_design);
        mColorPicker = findViewById(R.id.picker);
        mSaturationBar = findViewById(R.id.saturationBar);
        mValueBar = findViewById(R.id.valueBar);
        mColorPicker.setShowOldCenterColor(false);
        mSpinner = findViewById(R.id.item_spinner);
        mBlinkButton = findViewById(R.id.blink_button);
        mHueButton = findViewById(R.id.hue_button);
        mHueTwoButton = findViewById(R.id.hue2_button);
        mPulseButton =  findViewById(R.id.pulse_button);
        mHueHsbButton = findViewById(R.id.huehsb_button);
        mHueHslButton = findViewById(R.id.huehsl_button);
        mDurationPicker = findViewById(R.id.duration_picker);
        mRepetitionPicker = findViewById(R.id.repetitions_picker);
        mEffectsTimeLineView = findViewById(R.id.effects_timeline);
        mSavePresetButton = findViewById(R.id.save_preset_button);
        mPreviewPresetButton = findViewById(R.id.preview_preset_button);
        mEffectsControlLinearLayout = findViewById(R.id.effects_controls_linear_layout);
        mHoloPickerControlsRelativeLayout = findViewById(R.id.holo_picker_controls);
        mHintTextView = findViewById(R.id.text_view_hint);
        mTitleTextView.setText(R.string.design_mode_title);

        final TextView repetitionText = findViewById(R.id.repetitions_textview);
        final TextView durationText = findViewById(R.id.duration_textview);

        // Load data
        mSingleItemList = new ArrayList<>();
        mGroupedItemList = new ArrayList<>();
        mSelectedDevices = new ArrayList<>();
        AndroidNetworking.initialize(getApplicationContext());
        common = new Common();
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        mBlinkButton.setTag(Constants.DESIGN_EFFECT_BLINK);
        mPulseButton.setTag(Constants.DESIGN_EFFECT_PULSE);
        mHueButton.setTag(Constants.DESIGN_EFFECT_HUE);
        mHueTwoButton.setTag(Constants.DESIGN_EFFECT_HUE_TWO);
        mHueHslButton.setTag(Constants.DESIGN_EFFECT_HUE_HSL);
        mHueHsbButton.setTag(Constants.DESIGN_EFFECT_HUE_HSB);
        hoverThread = new HoverThread();

        // Set up view's functionality & design
        repetitionText.setTypeface(mSubTextTypeFace);
        durationText.setTypeface(mSubTextTypeFace);
        mColorPicker.addValueBar(mValueBar);
        mColorPicker.addSaturationBar(mSaturationBar);
        mColorPicker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                updateDeviceColorIncrementally();
            }
        });
        mColorPicker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                updateDeviceColorIncrementally();
            }
        });
        mColorPicker.setOnColorReleasedListener(new ColorPicker.OnColorReleaseListener() {
            @Override
            public void onColorReleased(int color) {
                updateDeviceColorInstant(color);
            }
        });
        CustomSpinnerAdapter mCustomSpinnerAdapter = new CustomSpinnerAdapter();
        mSpinner.setAdapter(mCustomSpinnerAdapter);
        mSpinner.setTypeface(mTextTypeFace);
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.isSelected()){
                    view.setSelected(false);
                    enableEffectsControl(false);
                    mEffectsTimeLineView.refreshView();
                    currentEffect = DEFAULT_EFFECT;
                }
                else {
                    mBlinkButton.setSelected(false);
                    mHueButton.setSelected(false);
                    mHueTwoButton.setSelected(false);
                    mPulseButton.setSelected(false);
                    mHueHsbButton.setSelected(false);
                    mHueHslButton.setSelected(false);
                    view.setSelected(true);
                    enableEffectsControl(true);
                    currentEffect = (String) view.getTag();
                }
            }
        };

        mRepetitionPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                repetition = newVal;
            }
        });

        mDurationPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                duration = newVal;
            }
        });
        mBlinkButton.setOnClickListener(mOnClickListener);
        mBlinkButton.setTypeface(mHeaderTypeFace);
        mHueButton.setOnClickListener(mOnClickListener);
        mHueButton.setTypeface(mHeaderTypeFace);
        mHueTwoButton.setOnClickListener(mOnClickListener);
        mHueTwoButton.setTypeface(mHeaderTypeFace);
        mPulseButton.setOnClickListener(mOnClickListener);
        mPulseButton.setTypeface(mHeaderTypeFace);
        mHueHslButton.setOnClickListener(mOnClickListener);
        mHueHslButton.setTypeface(mHeaderTypeFace);
        mHueHsbButton.setOnClickListener(mOnClickListener);
        mHueHsbButton.setTypeface(mHeaderTypeFace);
        mSavePresetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSavePresetDialog();
            }
        });
        mPreviewPresetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formulateCommand(startColor, stopColor, duration, repetition);
            }
        });
        mPreviewPresetButton.setEnabled(false);
        mSavePresetButton.setTypeface(mHeaderTypeFace);
        mEffectsTimeLineView.setTypeface(mSubTextTypeFace);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSpinnerPosition = i + 1;
                mSelectedDevices.clear();
                if(i == -1){
                    hideAllLightingView();
                } else if (i >= 0 && i < mGroupedItemList.size()){
                    mSelectedDevices = mGroupedItemList.get(i).getDeviceIPArrayList();
                    if(!isPreviousEffectAvailable) {
                        showAllLightingViews();
                        isFirstChange = false;
                    }
                    executorService = Executors.newFixedThreadPool(mSelectedDevices.size());
                } else if (i == (mGroupedItemList.size())){
                    hideAllLightingView();
                } else {
                    mSelectedDevices.add(mSingleItemList.get(i - (mGroupedItemList.size() + 1)).getIpAddress());
                    if(!isPreviousEffectAvailable){
                        showAllLightingViews();
                        isFirstChange = false;
                    }
                    executorService = Executors.newFixedThreadPool(mSelectedDevices.size());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            isPreviousEffectAvailable = true;
            setPreviousEffectVariables(bundle);
        } else {
            setPreviousEffectVariables(savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        loadListsWithData();
        super.onStart();
    }

    @Override
    protected void onPause() {
        if(isPreviousEffectAvailable){
            saveEffectsVariables();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(executorService != null) {
            executorService.shutdownNow();
        }
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.DESIGN_START_COLOR, startColor);
        outState.putInt(Constants.DESIGN_STOP_COLOR, stopColor);
        outState.putInt(Constants.DESIGN_CENTER_COLOR, mColorPicker.getColor());
        outState.putInt(Constants.DESIGN_REPETITION, repetition);
        outState.putInt(Constants.DESIGN_DURATION, duration);
        outState.putString(Constants.DESIGN_CURRENT_EFFECT, currentEffect);
        outState.putString(Constants.DESIGN_CURRENT_COMMAND, currentCommand);
        outState.putInt(Constants.DESIGN_CURRENT_SPINNER_POSITION, currentSpinnerPosition);
        super.onSaveInstanceState(outState);
    }

    private void setPreviousEffectVariables(Bundle bundle){
        setPickerProperties();
        if(bundle != null){
            currentSpinnerPosition = bundle.getInt(Constants.DESIGN_CURRENT_SPINNER_POSITION);
            mSpinner.setSelection(currentSpinnerPosition);
            mHintTextView.setVisibility(View.GONE);
            mColorPicker.setColor(bundle.getInt(Constants.DESIGN_CENTER_COLOR));
            currentCommand = bundle.getString(Constants.DESIGN_CURRENT_COMMAND);
            currentEffect = bundle.getString(Constants.DESIGN_CURRENT_EFFECT);
            mHoloPickerControlsRelativeLayout.setVisibility(View.VISIBLE);
            mSaturationBar.setVisibility(View.VISIBLE);
            mValueBar.setVisibility(View.VISIBLE);
            mSavePresetButton.setAlpha(1);
            mHoloPickerControlsRelativeLayout.setVisibility(View.VISIBLE);
            mSaturationBar.setVisibility(View.VISIBLE);
            mValueBar.setVisibility(View.VISIBLE);
            mSavePresetButton.setVisibility(View.VISIBLE);

            mSelectedDevices = bundle.getStringArrayList(Constants.DESIGN_SELECTED_DEVICES);
            if(mSelectedDevices != null){
                executorService = Executors.newFixedThreadPool(mSelectedDevices.size());
            }

            if(!currentEffect.equals(DEFAULT_EFFECT)){
                switch (currentEffect){
                    case Constants.DESIGN_EFFECT_BLINK:
                        mBlinkButton.setSelected(true);
                        break;
                    case Constants.DESIGN_EFFECT_PULSE:
                        mPulseButton.setSelected(true);
                        break;
                    case Constants.DESIGN_EFFECT_HUE:
                        mHueButton.setSelected(true);
                        break;
                    case Constants.DESIGN_EFFECT_HUE_TWO:
                        mHueTwoButton.setSelected(true);
                        break;
                    case Constants.DESIGN_EFFECT_HUE_HSB:
                        mHueHsbButton.setSelected(true);
                        break;
                    case Constants.DESIGN_EFFECT_HUE_HSL:
                        mHueHslButton.setSelected(true);
                        break;
                }

                mEffectsControlLinearLayout.setVisibility(View.VISIBLE);
                mPreviewPresetButton.setEnabled(true);
                mPreviewPresetButton.setVisibility(View.VISIBLE);
                mPreviewPresetButton.setAlpha(1);
                mEffectsTimeLineView.setStopCircleViewFocus(true);
                duration = bundle.getInt(Constants.DESIGN_DURATION);
                repetition = bundle.getInt(Constants.DESIGN_REPETITION);
                startColor = bundle.getInt(Constants.DESIGN_START_COLOR);
                stopColor = bundle.getInt(Constants.DESIGN_STOP_COLOR);
                mDurationPicker.setValue(duration);
                mRepetitionPicker.setValue(repetition);
                mEffectsTimeLineView.changeStartCircleColor(startColor, true);
                mEffectsTimeLineView.changeStopCircleColor(stopColor);
                isPreviousEffectAvailable = true;
            }
        } else {
            duration = 1;
            repetition = 1;
            startColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
            stopColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimary);
        }
    }

    /**
     * Update command variables in a pre-determined rate
     */
    private void updateDeviceColorIncrementally(){
        long thisTime = System.currentTimeMillis();


        if(!isPreviousEffectAvailable){
            mSavePresetButton.setAlpha(1);
            mSavePresetButton.setEnabled(true);
            isPreviousEffectAvailable = true;
        }

        // Send 10 commands every 1 second
        Long time = thisTime - lastTime;
        if((time) > 100){
            updateDeviceColorInstant(mColorPicker.getColor());
        }

        // Send command if the user is hovering on the holo picker.
        if(hoverThread.getStatus() == AsyncTask.Status.RUNNING || hoverThread.getStatus() == AsyncTask.Status.PENDING){
            isHovering = false;
            hoverThread.cancel(true);
        }
        hoverThread = new HoverThread();
        hoverThread.execute();
    }

    /**
     * Update command variables and send command instantly
     * @param color new color change
     */
    private void updateDeviceColorInstant(int color){
        if(mEffectsTimeLineView.getStopCircleView().isSelected()){
            mEffectsTimeLineView.changeStopCircleColor(color);
            stopColor = color;
            mPreviewPresetButton.setEnabled(true);
            mPreviewPresetButton.setAlpha(1);
        }

        if(mEffectsTimeLineView.getStartCircleView().isSelected()) {
            mEffectsTimeLineView.changeStartCircleColor(color, false);
            startColor = color;
        }

        if(!mSelectedDevices.isEmpty()){
            formulateCommand(color);
        }
        lastTime = System.currentTimeMillis();
    }

    /**
     * Formulate command.
     */
    private void formulateCommand(int color){
        String command = "hue2 rgb" + Integer.toString(Color.red(color)) + "," + Integer.toString(Color.green(color))+ "," + Integer.toString(Color.blue(color)) + " t1 f2";
        currentCommand = command;
        getSelectedIpAddressesAndSendCommands(command);
    }

    private void formulateCommand(int startColor, int endColor, int duration, int repetition){
        Boolean isHSL = false;
        Boolean isHSB = false;
        String button = "";

        if(mHueButton.isSelected()){
            button = "hue";
        } else if (mBlinkButton.isSelected()){
            button = "blink";
        } else if (mHueTwoButton.isSelected()){
            button = "hue2";
        } else if (mPulseButton.isSelected()){
            button = "pulse";
        } else if (mHueHslButton.isSelected()){
            isHSL = true;
            button = "huehsl";
        } else if (mHueHsbButton.isSelected()){
            isHSB = true;
            button = "huehsb";
        }

        // Get the appropriate start and stop color commands
        String startColorString;
        String stopColorString;
        if(isHSL){
            float[] hsv = new float[3];
            Color.RGBToHSV(Color.red(startColor), Color.green(startColor), Color.blue(startColor), hsv);
            startColorString = "hsl" + Integer.toString((int) hsv[0]) + "," + getPercentageValue(Float.toString(hsv[1])) + "," + getPercentageValue(Float.toString(hsv[2]));
            stopColorString = "hsl" + Integer.toString((int) hsv[0])+ "," + getPercentageValue(Float.toString(hsv[1])) + "," + getPercentageValue(Float.toString(hsv[2]));
        } else if (isHSB){
            float[] hsv = new float[3];
            Color.RGBToHSV(Color.red(startColor), Color.green(startColor), Color.blue(startColor), hsv);
            startColorString = "hsb" + Integer.toString((int) hsv[0]) + "," + getPercentageValue(Float.toString(hsv[1])) + "," + getPercentageValue(Float.toString(hsv[2]));
            stopColorString = "hsb" + Integer.toString((int) hsv[0]) + "," + getPercentageValue(Float.toString(hsv[1])) + "," + getPercentageValue(Float.toString(hsv[2]));
        } else {
            startColorString = "rgb" + Integer.toString(Color.red(startColor)) + "," + Integer.toString(Color.green(startColor))+ "," + Integer.toString(Color.blue(startColor));
            stopColorString = "rgb" + Integer.toString(Color.red(endColor)) + "," + Integer.toString(Color.green(endColor))+ "," + Integer.toString(Color.blue(endColor));
        }

        // Check if the start color has changed, else send a command without start color.
        String command;
        if(mEffectsTimeLineView.getStartCircleView().isColorChanged()){
            command = button + " " + startColorString + " " + stopColorString + " t" + repetition + " f" + getFrames(duration);
        } else {
            command = button + " " + stopColorString + " t" + repetition + " f" + getFrames(duration);
        }

        currentCommand = command;
        getSelectedIpAddressesAndSendCommands(command);
    }


    /**
     *  Get the saturation or hue percentage.
     */
    @NonNull
    private String getPercentageValue(String value){
        if(value.charAt(0) == '1'){
            return "100";
        }
        else
        {
            return value.substring(2, 4);
        }
    }

    /**
     * Get the number of frames to be sent in the command.
     */
    private String getFrames(int duration){
        switch (duration){
            case 1:
                return "2";
            case 2:
                return "15";
            case 3:
                return "30";
            case 4:
                return "45";
            default:
                return Integer.toString((duration - 4) * 60);
        }
    }

    /**
     *  Get selected Ip Addresses and send each one a command
     */
    private void getSelectedIpAddressesAndSendCommands(String command){
        for(String ip: mSelectedDevices){
            Device selectedDevice = ((Application)getApplicationContext()).getDeviceByIP(ip);
            if(selectedDevice != null){
                String ipAddress = selectedDevice.getIpAddress();
                Runnable worker = new PostRequest(command, ipAddress);
                executorService.execute(worker);
            }
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
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
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
     * Shows the views
     */
    private void showAllLightingViews(){
        mHintTextView.setVisibility(View.GONE);
        mHoloPickerControlsRelativeLayout.setVisibility(View.VISIBLE);
        mHoloPickerControlsRelativeLayout.setAlpha(0);
        mSaturationBar.setVisibility(View.VISIBLE);
        mSaturationBar.setAlpha(0);
        mValueBar.setVisibility(View.VISIBLE);
        mValueBar.setAlpha(0);
        mSavePresetButton.setVisibility(View.VISIBLE);
        mSavePresetButton.setEnabled(false);

        // Animate the controls to be shown slowly
        mHoloPickerControlsRelativeLayout.animate().setDuration(500).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mHoloPickerControlsRelativeLayout.setVisibility(View.VISIBLE);
            }
        });
        mSaturationBar.animate().setDuration(500).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSaturationBar.setVisibility(View.VISIBLE);
            }
        });
        mValueBar.animate().setDuration(500).alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mValueBar.setVisibility(View.VISIBLE);
            }
        });
        mSavePresetButton.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSavePresetButton.setVisibility(View.VISIBLE);
            }
        });

        if(isFirstChange) {
            enableEffectsControl(false);
        }
    }

    /**
     * Hides all lighting views
     */
    private void hideAllLightingView(){
        mHintTextView.setVisibility(View.VISIBLE);
        mHoloPickerControlsRelativeLayout.setVisibility(View.GONE);
        mHoloPickerControlsRelativeLayout.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mHoloPickerControlsRelativeLayout.setVisibility(View.GONE);
            }
        });
        mSaturationBar.setVisibility(View.GONE);
        mSaturationBar.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSaturationBar.setVisibility(View.GONE);
            }
        });
        mValueBar.setVisibility(View.GONE);
        mValueBar.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mValueBar.setVisibility(View.GONE);
            }
        });
        mSavePresetButton.setVisibility(View.GONE);
        mSavePresetButton.setEnabled(false);
        mSavePresetButton.setAlpha(0.3f);
        mSavePresetButton.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSavePresetButton.setVisibility(View.GONE);
            }
        });
        mPreviewPresetButton.setVisibility(View.GONE);
        mPreviewPresetButton.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mPreviewPresetButton.setVisibility(View.GONE);
            }
        });
        enableEffectsControl(false);
    }

    /**
     *  Show save group dialog fragment.
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

        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_PRESETS, null);

        if(json == null){
            allPresets = new AllPresets();
        } else {
            allPresets = gson.fromJson(json, AllPresets.class);
        }

        Preset preset = new Preset(allPresets.getAllPresets().size() + 1, presetName);

        int i = mSpinner.getSelectedItemPosition();
        if(i == 0){
            common.showToast(this, "Choose a group or a device");
            return;
        } else if (i > 0 && i <= mGroupedItemList.size()){
            DevicesGroup devicesGroup = (DevicesGroup) mSpinner.getSelectedItem();
            if(devicesGroup == null){
                return;
            }
            preset.setDevicesGroup(devicesGroup);
        } else if (i == (mGroupedItemList.size() + 1)){
            common.showToast(this, "Choose a group or a device");
            return;
        } else {
            Device device = mSingleItemList.get(i - (mGroupedItemList.size() + 2));
            if(device == null){
                return;
            }
            preset.getDevicesGroup().getDeviceIPArrayList().add(device.getIpAddress());

        }


        // Get the appropriate chosen command and save it with the preset.
        String startRGB = "rgb" + Integer.toString(Color.red(startColor)) + "," + Integer.toString(Color.green(startColor))+ "," + Integer.toString(Color.blue(startColor));
        String endRGB = "rgb" + Integer.toString(Color.red(stopColor)) + "," + Integer.toString(Color.green(stopColor))+ "," + Integer.toString(Color.blue(stopColor));

        String button = "";
        String command;

        if(mHueButton.isSelected()){
            button = "hue";
        } else if (mBlinkButton.isSelected()){
            button = "blink";
        } else if (mHueTwoButton.isSelected()){
            button = "hue2";
        } else if (mPulseButton.isSelected()){
            button = "pulse";
        } else if (mHueHslButton.isSelected()){
            button = "huehsb";
        } else if (mHueHsbButton.isSelected()){
            button = "huehsl";
        }

        command = button + " " + startRGB + " " + endRGB + " t" + repetition + " f" + getFrames(duration);

        preset.setCommand(command);
        allPresets.addPreset(preset);
        json = gson.toJson(allPresets);
        prefsEditor.putString(Constants.GROUP_OF_PRESETS, json);
        prefsEditor.apply();
        common.showToast(this, "Preset saved");
    }

    /**
     * Stores the configuration variables for later use.
     */
    private void saveEffectsVariables(){
        SharedPreferences Prefs = getSharedPreferences(Constants.DESIGN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = Prefs.edit();

        DesignConfiguration designConfiguration = new DesignConfiguration(startColor, stopColor, mColorPicker.getColor(),repetition, duration, currentEffect, currentCommand, currentSpinnerPosition, mSelectedDevices);

        Gson gson = new Gson();
        String json = gson.toJson(designConfiguration);
        prefsEditor.putString(Constants.DESIGN_CONFIGURATION, json);
        prefsEditor.apply();
    }

    /**
     * Loads groups and fixtures from file to lists
     */
    private void loadListsWithData(){
        Gson gson = new Gson();
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        String json = mPrefs.getString(Constants.GROUP_OF_DEVICES_GROUPS, null);
        AllGroups allGroups = gson.fromJson(json, AllGroups.class);

        if(allGroups != null){
            mGroupedItemList = allGroups.getGroups();
        }

        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        json = mPrefs.getString(Constants.GROUP_OF_SINGLE_DEVICES, null);
        OnlineDevices onlineDevices = gson.fromJson(json, OnlineDevices.class);

        if(onlineDevices != null){
            mSingleItemList = onlineDevices.getDevicesList();
            if(mSingleItemList.isEmpty()){
                setContentView(R.layout.activity_error);
            }
        } else{
            setContentView(R.layout.activity_error);
        }
    }

    /**
     * Set the number picker's properties.
     */
    private void setPickerProperties(){
        // Repetition Picker configuration
        String[] displayValues;
        ArrayList<String> valuesList = new ArrayList<>();
        for(int i = 1; i < 99; i++){
            valuesList.add(Integer.toString(i));
        }
        valuesList.add("∞");
        displayValues = valuesList.toArray(new String[valuesList.size()]);
        mRepetitionPicker.setDisplayedValues(displayValues);
        mRepetitionPicker.setMinValue(1);
        mRepetitionPicker.setMaxValue(99);
        mRepetitionPicker.setValue(1);

        // Duration Picker configuration
        valuesList.clear();
        valuesList.add("0");
        valuesList.add("0.25");
        valuesList.add("0.5");
        valuesList.add("0.75");
        for(int i = 1; i <= 100; i++){
            valuesList.add(Integer.toString(i));
        }

        displayValues = valuesList.toArray(new String[valuesList.size()]);
        mDurationPicker.setDisplayedValues(displayValues);
        mDurationPicker.setMinValue(1);
        mDurationPicker.setMaxValue(104);
        mDurationPicker.setValue(1);
    }

    /**
     * Sets the effects controls to clickable
     * or un-clickable
     */
    private void enableEffectsControl(boolean enable){
        if(!enable){
            mSavePresetButton.animate().translationY(- (mEffectsControlLinearLayout.getHeight() + mPreviewPresetButton.getHeight())).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    mEffectsControlLinearLayout.setVisibility(View.INVISIBLE);
                    mPreviewPresetButton.setVisibility(View.INVISIBLE);
                    mPreviewPresetButton.setAlpha(0.3f);
                    mPreviewPresetButton.setEnabled(false);
                    mEffectsTimeLineView.setStartCircleViewFocus(false);
                    mEffectsTimeLineView.setStopCircleViewFocus(false);
                }
            });

        } else{
            mSavePresetButton.animate().translationY(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mEffectsControlLinearLayout.setVisibility(View.VISIBLE);
                    mPreviewPresetButton.setVisibility(View.VISIBLE);
                    mEffectsTimeLineView.setStopCircleViewFocus(true);
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
                    TextView textView = view.findViewById(R.id.spinner_section_header_text_view);
                    textView.setTypeface(mTextTypeFace);
                    textView.setText(getString(R.string.spinner_group_section_header));
                    view.setOnClickListener(null);
                }
                return view;
            } else if (i > 0 && i < mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mGroupedItemList.get(i - 1).getName());
                textView.setTypeface(mTextTypeFace);
                return view;
            } else if (i == mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                view.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                TextView textView = view.findViewById(R.id.spinner_item_text_view);
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
                    TextView textView = view.findViewById(R.id.spinner_section_header_text_view);
                    textView.setTypeface(mTextTypeFace);
                    textView.setText(getString(R.string.spinner_fixture_section_header));
                    view.setOnClickListener(null);
                }
                return view;
            } else {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = view.findViewById(R.id.spinner_item_text_view);
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
            if(i == 0){
                return null;
            } else if (i > 0 && i < mGroupedItemList.size()){
                return mGroupedItemList.get(i - 1);
            } else if (i == mGroupedItemList.size()){
                return mGroupedItemList.get(i - 1);
            } else if (i == (mGroupedItemList.size() + 1)){
                return null;
            } else {
                return mSingleItemList.get(i - (mGroupedItemList.size() + 2));
            }
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
            if (view == null){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_hint, viewGroup, false);
            }

            TextView textView = view.findViewById(R.id.spinner_hint);
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

    private class HoverThread extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            long thisTime = System.currentTimeMillis();
            long lastTime;
            long time = 0;
            isHovering = true;

            while(time < 100 && isHovering){
                lastTime = System.currentTimeMillis();
                time = lastTime - thisTime;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isHovering){
                Log.d("Action", "HOVER");
                updateDeviceColorInstant(mColorPicker.getColor());
                isHovering = false;
            }
        }
    }
}