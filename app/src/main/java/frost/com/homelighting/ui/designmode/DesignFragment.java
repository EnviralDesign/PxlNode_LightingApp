package frost.com.homelighting.ui.designmode;

import com.google.gson.Gson;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GuardedObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import javax.inject.Inject;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;
import fr.ganfra.materialspinner.MaterialSpinner;
import frost.com.homelighting.HomeLightingApplication;
import frost.com.homelighting.MainActivity;
import frost.com.homelighting.R;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.PresetAndGroupDetailsEntity;
import frost.com.homelighting.db.entity.PresetDetailsEntity;
import frost.com.homelighting.db.entity.PresetEntity;
import frost.com.homelighting.viewmodel.DesignViewModel;

import static frost.com.homelighting.util.Constants.DESIGN_CENTER_COLOR;
import static frost.com.homelighting.util.Constants.DESIGN_CONFIGURATION;
import static frost.com.homelighting.util.Constants.DESIGN_CURRENT_COMMAND;
import static frost.com.homelighting.util.Constants.DESIGN_CURRENT_EFFECT;
import static frost.com.homelighting.util.Constants.DESIGN_CURRENT_SPINNER_POSITION;
import static frost.com.homelighting.util.Constants.DESIGN_DURATION;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_BLINK;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_HUE;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_HUE_HSB;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_HUE_HSL;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_HUE_TWO;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_NONE;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_PULSE;
import static frost.com.homelighting.util.Constants.DESIGN_EFFECT_SPRITE;
import static frost.com.homelighting.util.Constants.DESIGN_REPETITION;
import static frost.com.homelighting.util.Constants.DESIGN_SELECTED_DEVICES;
import static frost.com.homelighting.util.Constants.DESIGN_SHARED_PREFERENCES;
import static frost.com.homelighting.util.Constants.DESIGN_SPRITE_SELECTION;
import static frost.com.homelighting.util.Constants.DESIGN_START_CIRCLE_STATE;
import static frost.com.homelighting.util.Constants.DESIGN_START_COLOR;
import static frost.com.homelighting.util.Constants.DESIGN_STOP_COLOR;

public class DesignFragment extends Fragment{

    private static final String DEFAULT_EFFECT = DESIGN_EFFECT_NONE;
    private static final String DEFAULT_COMMAND = "hue rgb255,255,255 t1 f1";

    private List<DeviceEntity> mSingleItemList;
    private List<GroupEntity> mGroupedItemList;
    private List<String> mSelectedDevices;
    private List<String> mDeviceSprites;

    private MaterialSpinner mDeviceSpinner, mSpritesSpinner;
    private View mSpritesSpinnerContainer;
    private ColorPicker mColorPicker;
    private Button mBlinkButton, mHueButton, mHueTwoButton, mPulseButton,
            mHueHsbButton, mHueHslButton, mSpriteButton, mSavePresetButton, mPreviewPresetButton;
    private NumberPickerView mDurationPicker,  mRepetitionPicker;
    private EffectsTimelineView mEffectsTimeLineView;
    private LinearLayout mEffectsControlLinearLayout;
    private RelativeLayout mHoloPickerControlsRelativeLayout;
    private TextView mHintTextView;
    private SaturationBar mSaturationBar;
    private ValueBar mValueBar;
    private View mainView;

    private ExecutorService executorService;
    private HoverThread hoverThread;
    private MainActivity mMainActivity;

    private int repetition, duration, startColor, stopColor;
    private int spriteId = 1;// example purpose only
    private int spriteX = 0;
    private int spriteY = 0;
    private int currentSpinnerPosition = 0;
    private String currentEffect = DEFAULT_EFFECT;
    private String currentCommand = DEFAULT_COMMAND;
    private String currentSprite = null;

    private long lastTime = 0;
    private Boolean isPreviousEffectAvailable = false;
    private Boolean isFirstChange = true;
    private Boolean isHovering = false;
    private Boolean areEffectsEnabled = false;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    DesignViewModel designViewModel;

    public static DesignFragment newInstance(Bundle bundle) {
        DesignFragment designFragment = new DesignFragment();
        designFragment.setArguments(bundle);
        return designFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMainActivity = ((MainActivity) getActivity());

        ((HomeLightingApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        designViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(DesignViewModel.class);

        designViewModel.getOnlineDevices().observe(this, new Observer<List<DeviceEntity>>() {
            @Override
            public void onChanged(@Nullable List<DeviceEntity> deviceEntities) {
                setOnlineDevices(deviceEntities);
            }
        });

        designViewModel.getGroups().observe(this, new Observer<List<GroupEntity>>() {
            @Override
            public void onChanged(@Nullable List<GroupEntity> groupEntities) {
                setGroups(groupEntities);
            }
        });

        designViewModel.getSelectedDevices().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                setSelectedDevices(strings);
            }
        });

        designViewModel.getSprites().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                if(strings.isEmpty()){
                    mSpriteButton.setVisibility(View.GONE);
                } else {
                    setSprites(strings);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivity.invalidateOptionsMenu();
        mMainActivity.setTitle(R.string.design_mode_title);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_design, container, false);

        mColorPicker = mainView.findViewById(R.id.picker);
        mSaturationBar = mainView.findViewById(R.id.saturationBar);
        mValueBar = mainView.findViewById(R.id.valueBar);
        mColorPicker.setShowOldCenterColor(false);
        mDeviceSpinner = mainView.findViewById(R.id.item_spinner);
        mSpritesSpinner = mainView.findViewById(R.id.sprites_spinner);
        mSpritesSpinnerContainer = mainView.findViewById(R.id.sprites_spinner_container);
        mBlinkButton = mainView.findViewById(R.id.blink_button);
        mHueButton = mainView.findViewById(R.id.hue_button);
        mHueTwoButton = mainView.findViewById(R.id.hue2_button);
        mPulseButton =  mainView.findViewById(R.id.pulse_button);
        mHueHsbButton = mainView.findViewById(R.id.huehsb_button);
        mHueHslButton = mainView.findViewById(R.id.huehsl_button);
        mSpriteButton = mainView.findViewById(R.id.sprite_button);
        mDurationPicker = mainView.findViewById(R.id.duration_picker);
        mRepetitionPicker = mainView.findViewById(R.id.repetitions_picker);
        mEffectsTimeLineView = mainView.findViewById(R.id.effects_timeline);
        mSavePresetButton = mainView.findViewById(R.id.save_preset_button);
        mPreviewPresetButton = mainView.findViewById(R.id.preview_preset_button);
        mEffectsControlLinearLayout = mainView.findViewById(R.id.effects_controls_linear_layout);
        mHoloPickerControlsRelativeLayout = mainView.findViewById(R.id.holo_picker_controls);
        mHintTextView = mainView.findViewById(R.id.linear_layout_hint);

        mSingleItemList = new ArrayList<>();
        mGroupedItemList = new ArrayList<>();
        mSelectedDevices = new ArrayList<>();
        mDeviceSprites = new ArrayList<>();
        hoverThread = new HoverThread();
        executorService = Executors.newCachedThreadPool();

        mBlinkButton.setTag(DESIGN_EFFECT_BLINK);
        mPulseButton.setTag(DESIGN_EFFECT_PULSE);
        mHueButton.setTag(DESIGN_EFFECT_HUE);
        mHueTwoButton.setTag(DESIGN_EFFECT_HUE_TWO);
        mHueHslButton.setTag(DESIGN_EFFECT_HUE_HSL);
        mHueHsbButton.setTag(DESIGN_EFFECT_HUE_HSB);
        mSpriteButton.setTag(DESIGN_EFFECT_SPRITE);

        // Set up view's functionality & design
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
        mEffectsTimeLineView.setTypeface(Typeface.createFromAsset(mMainActivity.getAssets(), "fonts/montserrat_light.ttf"));
        mSpritesSpinner.setAdapter(new SpritesSpinnerAdapter());
        mDeviceSpinner.setAdapter(new CustomSpinnerAdapter());
        mDeviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentSpinnerPosition = i + 1;
                mSelectedDevices.clear();
                if(i == -1){
                    hideAllLightingView();
                } else if (i >= 0 && i < mGroupedItemList.size()){
                    designViewModel.getDevicesIpAddressInGroup(mGroupedItemList.get(i).getId());
                    designViewModel.setSpritesFromGroups(mGroupedItemList.get(i).getId());
                    if(!isPreviousEffectAvailable) {
                        showAllLightingViews();
                        isFirstChange = false;
                    }
                } else if (i == (mGroupedItemList.size())){
                    hideAllLightingView();
                } else {
                    String ipAddress = mSingleItemList.get(i - (mGroupedItemList.size() + 1)).getIpAddress();
                    designViewModel.setSpritesFromDevice(ipAddress);
                    mSelectedDevices.add(ipAddress);
                    if(!isPreviousEffectAvailable){
                        showAllLightingViews();
                        isFirstChange = false;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        View.OnClickListener mEffectButtonOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.isSelected()){
                    if(view.getTag() == DESIGN_EFFECT_SPRITE){
                        setSpriteButtonUnselected();
                    }
                    view.setSelected(false);
                    enableEffectsControl(false);
                    mSavePresetButton.setAlpha(0.3f);
                    mSavePresetButton.setEnabled(false);
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
                    if(view.getTag() == DESIGN_EFFECT_SPRITE){
                        setSpriteButtonSelected();
                    } else {
                        view.setSelected(true);
                        setSpriteButtonUnselected();
                    }
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
        mBlinkButton.setOnClickListener(mEffectButtonOnClickListener);
        mHueButton.setOnClickListener(mEffectButtonOnClickListener);
        mHueTwoButton.setOnClickListener(mEffectButtonOnClickListener);
        mPulseButton.setOnClickListener(mEffectButtonOnClickListener);
        mHueHslButton.setOnClickListener(mEffectButtonOnClickListener);
        mHueHsbButton.setOnClickListener(mEffectButtonOnClickListener);
        mSpriteButton.setOnClickListener(mEffectButtonOnClickListener);

        mSavePresetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSavePresetDialog();
            }
        });
        mPreviewPresetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formulateCommand();
            }
        });
        mPreviewPresetButton.setEnabled(false);

        Bundle bundle = getArguments();
        if(bundle != null){
            isPreviousEffectAvailable = true;
            setPreviousEffectVariables(bundle);
        } else {
            setPreviousEffectVariables(savedInstanceState);
        }

        return mainView;
    }

    @Override
    public void onPause() {
        if(isPreviousEffectAvailable){
            saveEffectsVariables();
        }
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(DESIGN_START_COLOR, startColor);
        outState.putInt(DESIGN_STOP_COLOR, stopColor);
        outState.putInt(DESIGN_CENTER_COLOR, mColorPicker.getColor());
        outState.putInt(DESIGN_REPETITION, repetition);
        outState.putInt(DESIGN_DURATION, duration);
        outState.putString(DESIGN_CURRENT_EFFECT, currentEffect);
        outState.putString(DESIGN_CURRENT_COMMAND, currentCommand);
        outState.putInt(DESIGN_CURRENT_SPINNER_POSITION, currentSpinnerPosition);
        outState.putBoolean(DESIGN_START_CIRCLE_STATE, mEffectsTimeLineView.ismIsStartCircleInDefault());
        outState.putInt(DESIGN_SPRITE_SELECTION, mSpritesSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState);
    }

    private void setOnlineDevices(List<DeviceEntity> onlineDevices){
        mSingleItemList = onlineDevices;
    }

    private void setGroups(List<GroupEntity> groups){
        mGroupedItemList = groups;
    }

    private void setSelectedDevices(List<String> selectedDevices){
        mSelectedDevices = selectedDevices;
    }

    public void setSprites(List<String> mDeviceSprites) {
        this.mDeviceSprites = mDeviceSprites;
    }

    private void setPreviousEffectVariables(Bundle bundle){
        setPickerProperties();
        if(bundle != null){
            currentSpinnerPosition = bundle.getInt(DESIGN_CURRENT_SPINNER_POSITION);
            mDeviceSpinner.setSelection(currentSpinnerPosition);
            mHintTextView.setVisibility(View.GONE);
            mColorPicker.setColor(bundle.getInt(DESIGN_CENTER_COLOR));
            currentCommand = bundle.getString(DESIGN_CURRENT_COMMAND);
            currentEffect = bundle.getString(DESIGN_CURRENT_EFFECT);
            mHoloPickerControlsRelativeLayout.setVisibility(View.VISIBLE);
            mSaturationBar.setVisibility(View.VISIBLE);
            mValueBar.setVisibility(View.VISIBLE);
            if(currentSpinnerPosition == 0 ){
                mSpriteButton.setVisibility(View.GONE);
            } else {
                mSpriteButton.setVisibility(View.VISIBLE);
            }
            mSavePresetButton.setAlpha(1);
            mSavePresetButton.setVisibility(View.VISIBLE);

            if(!mDeviceSprites.isEmpty()){
                mSpritesSpinner.setSelection(bundle.getInt(DESIGN_SPRITE_SELECTION));
            }
            mSelectedDevices = bundle.getStringArrayList(DESIGN_SELECTED_DEVICES);

            if(!currentEffect.equals(DEFAULT_EFFECT)){
                switch (currentEffect){
                    case DESIGN_EFFECT_BLINK:
                        mBlinkButton.setSelected(true);
                        break;
                    case DESIGN_EFFECT_PULSE:
                        mPulseButton.setSelected(true);
                        break;
                    case DESIGN_EFFECT_HUE:
                        mHueButton.setSelected(true);
                        break;
                    case DESIGN_EFFECT_HUE_TWO:
                        mHueTwoButton.setSelected(true);
                        break;
                    case DESIGN_EFFECT_HUE_HSB:
                        mHueHsbButton.setSelected(true);
                        break;
                    case DESIGN_EFFECT_HUE_HSL:
                        mHueHslButton.setSelected(true);
                        break;
                    case DESIGN_EFFECT_SPRITE:
                        setSpriteButtonSelected();
                        break;
                }

                mEffectsControlLinearLayout.setVisibility(View.VISIBLE);
                mPreviewPresetButton.setEnabled(true);
                mPreviewPresetButton.setVisibility(View.VISIBLE);
                mPreviewPresetButton.setAlpha(1);
                duration = bundle.getInt(DESIGN_DURATION);
                repetition = bundle.getInt(DESIGN_REPETITION);
                startColor = bundle.getInt(DESIGN_START_COLOR);
                stopColor = bundle.getInt(DESIGN_STOP_COLOR);
                mDurationPicker.setValue(duration);
                mRepetitionPicker.setValue(repetition);

                mEffectsTimeLineView.changeStopCircleColor(stopColor, true);

                // Check if the start circle was set to default.
                if(bundle.getBoolean(DESIGN_START_CIRCLE_STATE)){
                    mEffectsTimeLineView.setStartCircleToDefault();
                } else {
                    mEffectsTimeLineView.changeStartCircleColor(startColor, true);
                }

                isPreviousEffectAvailable = true;
            }
        } else {
            duration = 1;
            repetition = 1;
            startColor = ContextCompat.getColor(mMainActivity.getBaseContext(), R.color.colorPrimary);
            stopColor = ContextCompat.getColor(mMainActivity.getBaseContext(), R.color.colorPrimary);
        }
    }

    private void setSpriteButtonSelected(){
        mSpriteButton.setSelected(true);
        ResizeWidthAnimation resizeAnimation = new ResizeWidthAnimation(mSpriteButton, mHoloPickerControlsRelativeLayout.getWidth());
        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mSpriteButton.setVisibility(View.VISIBLE);
                mSpritesSpinnerContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSpriteButton.setVisibility(View.GONE);
                mSpritesSpinnerContainer.setVisibility(View.VISIBLE);
                mSpritesSpinner.performClick();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        resizeAnimation.setDuration(400);
        mSpriteButton.startAnimation(resizeAnimation);
    }

    private void setSpriteButtonUnselected(){
        mSpriteButton.setSelected(false);
        mSpritesSpinnerContainer.setVisibility(View.GONE);
        mSpriteButton.setVisibility(View.VISIBLE);
        ResizeWidthAnimation resizeAnimation = new ResizeWidthAnimation(mSpriteButton, dipToPixels(70));
        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSpritesSpinnerContainer.setVisibility(View.GONE);
                mSpriteButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        resizeAnimation.setDuration(400);
        mSpriteButton.startAnimation(resizeAnimation);
    }

    /**
     * Update command variables in a pre-determined rate
     */
    private void updateDeviceColorIncrementally(){
        long thisTime = System.currentTimeMillis();

        if(!isPreviousEffectAvailable){
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
            mEffectsTimeLineView.changeStopCircleColor(color,false);
            stopColor = color;
        }

        if(mEffectsTimeLineView.getStartCircleView().isSelected()) {
            mEffectsTimeLineView.changeStartCircleColor(color, false);
            startColor = color;
        }

        // Enable buttons if effects are enabled.
        if(areEffectsEnabled){
            mPreviewPresetButton.setEnabled(true);
            mPreviewPresetButton.setAlpha(1);
            mSavePresetButton.setEnabled(true);
            mSavePresetButton.setAlpha(1);
        }

        if(!mSelectedDevices.isEmpty()){
            formulateCommand(color);
        }
        lastTime = System.currentTimeMillis();
    }

    /**
     * Formulate command.
     */
    private void formulateCommand(){
        currentCommand = getCommand();
        getSelectedIpAddressesAndSendCommands(currentCommand);
    }

    private void formulateCommand(int color){
        String command = "hue2 rgb" + Integer.toString(Color.red(color)) + "," + Integer.toString(Color.green(color))+ "," + Integer.toString(Color.blue(color)) + " t1 f2";
        currentCommand = command;
        getSelectedIpAddressesAndSendCommands(command);
    }

    /**
     *  Get the saturation or hue percentage.
     */
    private String getPercentageValue(String value){
        Double num = Double.valueOf(value);
        if(num > 1.0){
            return Double.toString(Math.ceil(num));
        } else if(num == 1.0){
            return "100";
        } else {
            if(value.length() == 3){
                return value.charAt(2) + "0";
            } else{
                return value.substring(2, 4);
            }
        }
    }

    /**
     * Get the number of frames to be sent in the command.
     */
    private String getFrames(int duration){
        String[] durationLabelsAndValue = getResources().getStringArray(R.array.duration_values);
        if(mDurationPicker.getValue() - 1 < durationLabelsAndValue.length) {
            String value = durationLabelsAndValue[mDurationPicker.getValue() - 1 ];
            return value.substring(value.lastIndexOf('=') + 2);
        } else {
            return Integer.toString((duration - 4) * 60);
        }
    }

    /**
     *  Get selected Ip Addresses and send each one a command.
     */
    private void getSelectedIpAddressesAndSendCommands(String command){
        try{
            for(String ip: mSelectedDevices){
                Runnable worker = new PostRequest(command, ip);
                executorService.execute(worker);
            }
        } catch (RejectedExecutionException e){
            e.printStackTrace();
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
     * Shows the lighting views.
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

        int selection = mDeviceSpinner.getSelectedItemPosition();

        if(mSpritesSpinnerContainer.getVisibility() != View.VISIBLE){
            if(selection == 0){
                mSpriteButton.setVisibility(View.GONE);
            } else {
                mSpriteButton.setVisibility(View.VISIBLE);
                mSpriteButton.setAlpha(0);
                mSpriteButton.animate().setDuration(500).alpha(1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSpriteButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

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
        mSpriteButton.setVisibility(View.GONE);
        mSpriteButton.animate().setDuration(500).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mSpriteButton.setVisibility(View.GONE);
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
        dialogFragment.show(mMainActivity.getFragmentManager(), "AddPresetDialog");
    }

    /**
     * Saves the preset locally.
     */
    protected void savePreset(String presetName){
        int i = mDeviceSpinner.getSelectedItemPosition();
        GroupEntity groupEntity = null;
        if(i == 0){
            mMainActivity.showToast(R.string.toast_choose_group_or_device);
            return;
        } else if (i > 0 && i <= mGroupedItemList.size()){
            groupEntity = (GroupEntity) mDeviceSpinner.getSelectedItem();
            if(groupEntity == null){
                return;
            }
        } else if (i == (mGroupedItemList.size() + 1)){
            mMainActivity.showToast(R.string.toast_choose_group_or_device);
            return;
        }

        PresetEntity presetEntity = new PresetEntity(presetName, getCommand());
        if(groupEntity == null){
            SavePresetDeviceTask savePresetTask = new SavePresetDeviceTask(mSelectedDevices);
            savePresetTask.execute(presetEntity);
        } else {
            SavePresetGroupTask savePresetGroupTask = new SavePresetGroupTask(mSelectedDevices, groupEntity.getId());
            savePresetGroupTask.execute(presetEntity);
        }
    }

    /**
     * Create the appropriate command
     * @return command to be sent in post request
     */
    private String getCommand(){
        Boolean isHSL = false;
        Boolean isHSB = false;
        Boolean isSprite = false;
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
        } else if (mSpriteButton.isSelected()){
            isSprite = true;
            button = "sprite";
        }

        // Get the appropriate start and stop color commands
        String startColorString;
        String stopColorString;
        if(isHSL){
            float[] hsl = new float[3];
            ColorUtils.colorToHSL(startColor, hsl);
            startColorString = "hsl" + Integer.toString((int) hsl[0]) + "," + getPercentageValue(Float.toString(hsl[1])) + "," + getPercentageValue(Float.toString(hsl[2]));
            ColorUtils.colorToHSL(stopColor, hsl);
            stopColorString = "hsl" + Integer.toString((int) hsl[0])+ "," + getPercentageValue(Float.toString(hsl[1])) + "," + getPercentageValue(Float.toString(hsl[2]));
        } else if (isHSB){
            float[] hsv = new float[3];
            Color.RGBToHSV(Color.red(startColor), Color.green(startColor), Color.blue(startColor), hsv);
            startColorString = "hsb" + Integer.toString((int) hsv[0]) + "," + getPercentageValue(Float.toString(hsv[1])) + "," + getPercentageValue(Float.toString(hsv[2]));
            Color.RGBToHSV(Color.red(stopColor), Color.green(stopColor), Color.blue(stopColor), hsv);
            stopColorString = "hsb" + Integer.toString((int) hsv[0]) + "," + getPercentageValue(Float.toString(hsv[1])) + "," + getPercentageValue(Float.toString(hsv[2]));
        } else {
            startColorString = "rgb" + Integer.toString(Color.red(startColor)) + "," + Integer.toString(Color.green(startColor))+ "," + Integer.toString(Color.blue(startColor));
            stopColorString = "rgb" + Integer.toString(Color.red(stopColor)) + "," + Integer.toString(Color.green(stopColor))+ "," + Integer.toString(Color.blue(stopColor));
        }

        // Check if the start color is in default, else send a command without start color.
        String command;
        if(mEffectsTimeLineView.getStartCircleView().isColorChanged()){
            command = button + " " + startColorString + " " + stopColorString + " t" + repetition + " f" + getFrames(duration);
        } else {
            command = button + " " + stopColorString + " t" + repetition + " f" + getFrames(duration);
        }

        if(isSprite){
            command += " s" + Integer.toString(spriteId) + " x" + spriteX + " y" + spriteY;
        }

        return command;
    }

    /**
     * Stores the configuration variables for later use.
     */
    private void saveEffectsVariables(){
        SharedPreferences Prefs = mMainActivity.getSharedPreferences(DESIGN_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = Prefs.edit();

        ArrayList<String> selectedDevices = new ArrayList<>(mSelectedDevices);

        DesignConfiguration designConfiguration = new DesignConfiguration(startColor, stopColor, mColorPicker.getColor(),repetition, duration, currentEffect, currentCommand, currentSpinnerPosition, selectedDevices, mEffectsTimeLineView.ismIsStartCircleInDefault(), mSpritesSpinner.getSelectedItemPosition());

        Gson gson = new Gson();
        String json = gson.toJson(designConfiguration);
        prefsEditor.putString(DESIGN_CONFIGURATION, json);
        prefsEditor.apply();
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
        valuesList = getDurationLabels();
        for(int i = 1; i <= 100; i++){
            valuesList.add(Integer.toString(i));
        }

        displayValues = valuesList.toArray(new String[valuesList.size()]);
        mDurationPicker.setDisplayedValues(displayValues);
        mDurationPicker.setMinValue(1);
        mDurationPicker.setMaxValue(100 + + getDurationLabelsUnderOneLength());
        mDurationPicker.setValue(1);
    }

    /**
     * Get the labels from R.array.duration_values
     */
    private ArrayList<String> getDurationLabels(){
        String[] durationLabelsAndValues = getResources().getStringArray(R.array.duration_values);
        ArrayList<String> labels = new ArrayList<>();
        for (String string: durationLabelsAndValues) {
            labels.add(string.substring(0, string.indexOf(" =")));
        }

        return labels;
    }

    private int getDurationLabelsUnderOneLength(){
        String[] durationLabelsAndValues = getResources().getStringArray(R.array.duration_values);
        return durationLabelsAndValues.length;
    }

    /**
     * Sets the effects controls to clickable
     * or un-clickable.
     */
    private void enableEffectsControl(boolean enable){
        if(!enable){
            areEffectsEnabled = false;
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
            areEffectsEnabled = true;
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

    private int dipToPixels(float dipValue) {
        DisplayMetrics metrics = mMainActivity.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    /**
     * Customize the Spinner Adapter to allow for
     * two separate lists to be loaded under two
     * section headers.
     */
    private class CustomSpinnerAdapter implements SpinnerAdapter {
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
                    textView.setText(getString(R.string.spinner_group_section_header));
                    view.setOnClickListener(null);
                }
                return view;
            } else if (i > 0 && i < mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_group, viewGroup, false);
                TextView textView = view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else if (i == mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_group, viewGroup, false);
                view.setBackgroundColor(ContextCompat.getColor(mMainActivity.getBaseContext(),R.color.colorPrimary));
                TextView textView = view.findViewById(R.id.spinner_item_text_view);
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
                    textView.setText(getString(R.string.spinner_fixture_section_header));
                    view.setOnClickListener(null);
                }
                return view;
            } else {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getName());
                TextView subTextView = view.findViewById(R.id.spinner_item_subtext_view);
                subTextView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getIpAddress());
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
            TextView subTextView = view.findViewById(R.id.spinner_hint_subtext);
            subTextView.setVisibility(View.GONE);
            if(i == 0 || i == (mGroupedItemList.size() + 1)){
                textView.setText(getString(R.string.design_mode_spinner_hint));
                return view;
            } else if (i > 0 && i <= mGroupedItemList.size()){
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else {
                textView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getName());
                subTextView.setVisibility(View.VISIBLE);
                subTextView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getIpAddress());
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
            return mGroupedItemList.isEmpty() && mSingleItemList.isEmpty();
        }

    }

    private class SpritesSpinnerAdapter implements SpinnerAdapter{
        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_group, viewGroup, false);
            view.setBackgroundColor(ContextCompat.getColor(mMainActivity.getBaseContext(),R.color.colorPrimary));
            TextView textView = view.findViewById(R.id.spinner_item_text_view);
            textView.setText(mDeviceSprites.get(i));
            if(i == 0){
                textView.setTextColor(mMainActivity.getColor(R.color.colorUnusedText));
            }
            return view;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            if(mDeviceSprites.isEmpty()){
                return 1;
            }
            return mDeviceSprites.size();
        }

        @Override
        public Object getItem(int i) {
            return mDeviceSprites.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_hint_sprite, viewGroup, false);
            }

            TextView textView = view.findViewById(R.id.spinner_hint);
            TextView subTextView = view.findViewById(R.id.spinner_hint_subtext);
            subTextView.setVisibility(View.GONE);

            if(i == 0){
                textView.setText(getString(R.string.sprites));
                textView.setTextColor(mMainActivity.getColor(R.color.colorUnusedText));
            } else {
                textView.setText(mDeviceSprites.get(i));
            }

            return view;
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
            return mDeviceSprites.isEmpty();
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

    private class SavePresetDeviceTask extends AsyncTask<PresetEntity, Void, Void>{

        private final List<String> deviceIps;

        public SavePresetDeviceTask(List<String> deviceIps) {
            this.deviceIps = deviceIps;
        }

        @Override
        protected Void doInBackground(PresetEntity... presetEntities) {
            Long presetId = designViewModel.savePreset(presetEntities[0]);
            List<PresetDetailsEntity> presetDetailsEntities = new ArrayList<>();
            for(String deviceIp : deviceIps){
                presetDetailsEntities.add(new PresetDetailsEntity(presetId.intValue(), deviceIp));
            }
            designViewModel.savePresetDetails(presetDetailsEntities);
            return null;
        }
    }

    private class SavePresetGroupTask extends AsyncTask<PresetEntity, Void, Void>{

        private final List<String> deviceIps;
        private final int groupId;

        public SavePresetGroupTask(List<String> deviceIps, int groupId) {
            this.deviceIps = deviceIps;
            this.groupId = groupId;
        }

        @Override
        protected Void doInBackground(PresetEntity... presetEntities) {
            Long presetId = designViewModel.savePreset(presetEntities[0]);
            List<PresetDetailsEntity> presetDetailsEntities = new ArrayList<>();
            for(String deviceIp : deviceIps){
                presetDetailsEntities.add(new PresetDetailsEntity(presetId.intValue(), deviceIp));
            }
            designViewModel.insertPresetAndGroupDetails(new PresetAndGroupDetailsEntity(presetId.intValue(), groupId));
            designViewModel.savePresetDetails(presetDetailsEntities);
            return null;
        }
    }
}
