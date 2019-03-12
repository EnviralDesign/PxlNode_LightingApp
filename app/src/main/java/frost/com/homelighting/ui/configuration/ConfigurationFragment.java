package frost.com.homelighting.ui.configuration;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import fr.ganfra.materialspinner.MaterialSpinner;
import frost.com.homelighting.BuildConfig;
import frost.com.homelighting.HomeLightingApplication;
import frost.com.homelighting.MainActivity;
import frost.com.homelighting.R;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.util.Constants;
import frost.com.homelighting.viewmodel.ConfigurationViewModel;

import static android.content.Context.WIFI_SERVICE;

public class ConfigurationFragment extends Fragment implements Constants{

    private String mWarmUpColorRed,mWarmUpColorGreen,mWarmUpColorBlue;

    private MaterialSpinner mSpinner;
    private MaterialEditText mDeviceName, mPixelsPerStrip, mChunkSize, mMAPerPixel, mUDPPort,
            mAMPSLimit;
    private TextView mHintTextView;

    private ScrollView mMainLayout;
    private LinearLayout mHintLinearLayout;
    private ProgressBar mProgressBar;
    private ProgressDialog mProgressDialogue;
    private ColorBoxView mColorBox;
    private ColorPicker mColorPicker;

    private List<DeviceEntity> mOnlineDevicesList;

    private MainActivity mainActivity;

    private int debugDeviceCounter = 0;

    private TextWatcher mDeviceNameWatcher = new TextWatcher() {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9_\\-]*");
        Matcher patternMatcher;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Do nothing
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            patternMatcher = pattern.matcher(text);
            mDeviceName.setError(null);

            if(!patternMatcher.matches()){
                mDeviceName.setError(getString(R.string.configuration_error_special_or_spaces_characters));
            }
            if(text.length() > 64){
                mDeviceName.setError(getString(R.string.configuration_error_more_than_64_characters));
            }
            if(text.isEmpty()){
                mDeviceName.setError(getString(R.string.configuration_error_empty));
            }
        }
    };

    private TextWatcher mPixelPerStripWatcher = new TextWatcher() {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher patternMatcher;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mPixelsPerStrip.setError(null);
            patternMatcher = pattern.matcher(editable.toString());
            if(!editable.toString().equals("")){
                Integer pixelsPerStrip = Integer.valueOf(editable.toString());
                if(!patternMatcher.matches()){
                    mPixelsPerStrip.setError(getString(R.string.configuration_error_special_characters));
                } else if (pixelsPerStrip > 1500 || pixelsPerStrip < 0){
                    mPixelsPerStrip.setError(getString(R.string.configuration_error_invalid_input));
                }
            }
        }
    };

    private TextWatcher mChunkSizeWatcher = new TextWatcher() {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher patternMatcher;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mChunkSize.setError(null);
            patternMatcher = pattern.matcher(editable.toString());

            if(!editable.toString().equals("")) {
                Integer chunkSize = Integer.valueOf(editable.toString());
                if (!patternMatcher.matches()) {
                    mChunkSize.setError(getString(R.string.configuration_error_special_characters));
                } else if (chunkSize > 200 || chunkSize < 0) {
                    mChunkSize.setError(getString(R.string.configuration_error_invalid_input));
                }
            }
        }
    };

    private TextWatcher mMAPerPixelWatcher = new TextWatcher() {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher patternMatcher;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mMAPerPixel.setError(null);
            patternMatcher = pattern.matcher(editable.toString());

            if(!editable.toString().equals("")) {
                Integer mMaPerPixelInteger = Integer.valueOf(editable.toString());
                if (!patternMatcher.matches()) {
                    mMAPerPixel.setError(getString(R.string.configuration_error_special_characters));
                } else if (mMaPerPixelInteger > 99999 || mMaPerPixelInteger < 0) {
                    mMAPerPixel.setError(getString(R.string.configuration_error_invalid_input));
                }
            }
        }
    };

    private TextWatcher mUDPPortWatcher = new TextWatcher() {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher patternMatcher;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            mUDPPort.setError(null);
            patternMatcher = pattern.matcher(editable.toString());

            if (!editable.toString().equals("")) {
                Integer udpStreamingPort = Integer.valueOf(editable.toString());
                if (!patternMatcher.matches()) {
                    mUDPPort.setError(getString(R.string.configuration_error_special_characters));
                } else if (udpStreamingPort > 65536 || udpStreamingPort < 0) {
                    mUDPPort.setError(getString(R.string.configuration_error_invalid_input));
                }
            }
        }
    };

    private TextWatcher mAmpsLimitWatcher = new TextWatcher() {
        Pattern pattern = Pattern.compile("[0-9]*\\.[0-9]*|[0-9]*");
        Matcher patternMatcher;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            patternMatcher = pattern.matcher(editable.toString());
            mAMPSLimit.setError(null);

            if(!editable.toString().isEmpty()){
                if(!patternMatcher.matches()){
                    mAMPSLimit.setError(getString(R.string.configuration_error_invalid_input));
                }
            }
        }
    };

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ConfigurationViewModel configurationViewModel;

    public static ConfigurationFragment newInstance() {
        return new ConfigurationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        ((HomeLightingApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        configurationViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ConfigurationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuration, container, false);

        // Set up views.
        mSpinner = view.findViewById(R.id.item_spinner);
        mDeviceName = view.findViewById(R.id.device_name_edit_text);
        mPixelsPerStrip = view.findViewById(R.id.pixels_per_strip_edit_text);
        mChunkSize = view.findViewById(R.id.chunk_size_edit_text);
        mMAPerPixel = view.findViewById(R.id.ma_per_strip);
        mUDPPort = view.findViewById(R.id.udp_port_edit_text);
        mAMPSLimit = view.findViewById(R.id.amps_limit_edit_text);
        mMainLayout = view.findViewById(R.id.main_layout);
        mHintLinearLayout = view.findViewById(R.id.linear_layout_hint);
        mProgressBar = view.findViewById(R.id.progressBar);
        mColorBox = view.findViewById(R.id.colorBox);
        mHintTextView = view.findViewById(R.id.text_view_hint);
        Button mUpdateDeviceButton = view.findViewById(R.id.update_device_button);
        mProgressDialogue = new ProgressDialog(mainActivity, R.style.MyProgressDialogue);
        mProgressDialogue.setMessage(getString(R.string.progress_dialogue_message));

        // Set views functionality.
        mDeviceName.addTextChangedListener(mDeviceNameWatcher);
        mPixelsPerStrip.addTextChangedListener(mPixelPerStripWatcher);
        mChunkSize.addTextChangedListener(mChunkSizeWatcher);
        mMAPerPixel.addTextChangedListener(mMAPerPixelWatcher);
        mUDPPort.addTextChangedListener(mUDPPortWatcher);
        mAMPSLimit.addTextChangedListener(mAmpsLimitWatcher);

        mColorBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mColorPicker.show();

                mColorPicker.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(int color) {
                        mWarmUpColorRed = String.valueOf(Color.red(color));
                        mWarmUpColorGreen = String.valueOf(Color.green(color));
                        mWarmUpColorBlue = String.valueOf(Color.blue(color));
                        mColorBox.setBoxColor(color);
                        mColorPicker.dismiss();
                    }
                });
            }
        });
        mUpdateDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateUserInput();
            }
        });

        // Load data.
        mColorPicker = new ColorPicker(mainActivity,0, 0, 0);
        mOnlineDevicesList = mainActivity.mOnlineDevices;
        mSpinner.setAdapter(new CustomSpinnerAdapter());
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                DeviceEntity selectedDevice = (DeviceEntity) adapterView.getAdapter().getItem(i);
                new GetDeviceInfoTask().execute(selectedDevice.getIpAddress());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.invalidateOptionsMenu();
        mainActivity.setTitle(R.string.configuration_mode_title);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkIfOnlineDevicesIsNotEmpty();
        checkWifiConnection();
    }

    /**
     * Clears the texts inside the edit texts.
     */
    private void clearAllTexts(){
        mDeviceName.setText("");
        mPixelsPerStrip.setText("");
        mChunkSize.setText("");
        mMAPerPixel.setText("");
        mUDPPort.setText("");
        mAMPSLimit.setText("");
    }

    /**
     * Validate user input and send json.
     */
    private void validateUserInput() {
        Boolean areInputsValid = true;

        // Check if any of the edit texts are empty.
        if(mDeviceName.getText().toString().isEmpty()){
            mDeviceName.setError(getString(R.string.configuration_error_empty));
        }
        if(mPixelsPerStrip.getText().toString().isEmpty()){
            mPixelsPerStrip.setError(getString(R.string.configuration_error_empty));
        }
        if(mChunkSize.getText().toString().isEmpty()){
            mChunkSize.setError(getString(R.string.configuration_error_empty));
        }
        if(mUDPPort.getText().toString().isEmpty()){
            mUDPPort.setError(getString(R.string.configuration_error_empty));
        }
        if(mMAPerPixel.getText().toString().isEmpty()){
            mMAPerPixel.setError(getString(R.string.configuration_error_empty));
        }
        if(mAMPSLimit.getText().toString().isEmpty()){
            mAMPSLimit.setError(getString(R.string.configuration_error_empty));
        }

        // Check if there are any errors reported.
        if(mDeviceName.getError() != null){
            areInputsValid = false;
        }
        if(mPixelsPerStrip.getError() != null){
            areInputsValid = false;
        }
        if(mChunkSize.getError() != null){
            areInputsValid = false;
        }
        if(mUDPPort.getError() != null){
            areInputsValid = false;
        }
        if(mMAPerPixel.getError() != null){
            areInputsValid = false;
        }
        if(mAMPSLimit.getError() != null){
            areInputsValid = false;
        }

        if(areInputsValid){
            try{
                sendUpdateDeviceCommand(createJSONMessage());
            } catch (JSONException e){
                e.printStackTrace();
                mainActivity.showToast("Error producing JSON Object");
                turnOffUI();
            }
        } else {
            mainActivity.showToast("Invalid input");
        }
    }

    /**
     * Creates JSON Object with device configuration.
     * @return JSON Message with device configuration.
     */
    private JSONObject createJSONMessage() throws JSONException{
        JSONObject jsonMessage = new JSONObject();
        jsonMessage.put(CONFIGURATION_DEVICE_NAME, mDeviceName.getText().toString());
        jsonMessage.put(CONFIGURATION_PIXELS_PER_STRIP, mPixelsPerStrip.getText().toString());
        jsonMessage.put(CONFIGURATION_CHUNK_SIZE, mChunkSize.getText().toString());
        jsonMessage.put(CONFIGURATION_UDP_STREAMING_PORT, mUDPPort.getText().toString());
        jsonMessage.put(CONFIGURATION_MA_PER_PIXEL, mMAPerPixel.getText().toString());
        jsonMessage.put(CONFIGURATION_AMPS_LIMIT, mAMPSLimit.getText().toString());
        jsonMessage.put(CONFIGURATION_WARMUP_COLOR, new JSONArray(
                new String[]{
                        mWarmUpColorRed,
                        mWarmUpColorGreen,
                        mWarmUpColorBlue
                }
        ));
        return jsonMessage;
    }

    /**
     * Check if the list of online devices is empty
     * if so then update the UI.
     */
    private void checkIfOnlineDevicesIsNotEmpty(){
        if(mainActivity.mOnlineDevices.isEmpty()){
            turnOffUI();
        } else {
            turnOnUI();
        }
    }

    private void turnOffUI(){
        mMainLayout.setVisibility(View.GONE);
        mHintLinearLayout.setVisibility(View.VISIBLE);
    }

    private void turnOnUI(){
        mHintLinearLayout.setVisibility(View.GONE);
        mMainLayout.setVisibility(View.VISIBLE);
    }
    /**
     * Check wifi connection if not
     * update UI.
     */
    private void checkWifiConnection(){
        WifiManager wm = (WifiManager) mainActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
        if(!wm.isWifiEnabled()){
            mMainLayout.setVisibility(View.GONE);
            mHintTextView.setText(R.string.configuration_no_network_hint);
            mHintLinearLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Update the variables of the device.
     */
    private void updateDeviceVariables(){
        DeviceEntity device = (DeviceEntity) mSpinner.getAdapter().getItem(mSpinner.getSelectedItemPosition() - 1);
        device.setName(mDeviceName.getText().toString());
        device.setPixelsPerStrip(mPixelsPerStrip.getText().toString().equals("") ? -1 : Short.decode(mPixelsPerStrip.getText().toString()));
        device.setChunkSize(mChunkSize.getText().toString().equals("") ? -1 : Short.decode(mChunkSize.getText().toString()));
        device.setUDPStreamingPort(mUDPPort.getText().toString().equals("") ? -1 : Integer.valueOf(mUDPPort.getText().toString()));
        device.setMaPerPixel(mMAPerPixel.getText().toString().equals("") ? -1 : Short.decode(mMAPerPixel.getText().toString()));
        device.setAmpsLimit(mAMPSLimit.getText().toString().equals("") ? -1f :Float.parseFloat(mAMPSLimit.getText().toString()));
        device.setWarmUpColor(mWarmUpColorRed
                + " "
                + mWarmUpColorGreen
                + " "
                + mWarmUpColorBlue);
        refreshSpinnerAdapter();
    }

    /**
     * Refreshes the spinner adapter.
     */
    private void refreshSpinnerAdapter(){
        // Let Java garbage collector collect the last adapter.
        mSpinner.setAdapter(new CustomSpinnerAdapter());
        checkIfOnlineDevicesIsNotEmpty();
    }

    private void refreshColorPicker(int red, int green, int blue){
        mColorPicker = new ColorPicker(mainActivity, red, green, blue);
        mColorBox.setBoxColor(Color.rgb(red, green, blue));
    }

    /**
     * Send update json to device
     */
    public void sendUpdateDeviceCommand(JSONObject json){
        DeviceEntity deviceEntity = (DeviceEntity) mSpinner.getAdapter().getItem(mSpinner.getSelectedItemPosition() - 1);

        UpdateDeviceTask updateDeviceTask = new UpdateDeviceTask(deviceEntity, json);
        updateDeviceTask.execute();
    }


    /**
     * Custom adapter that loads online devices only.
     */
    private class CustomSpinnerAdapter implements SpinnerAdapter {


        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
            TextView textView = view.findViewById(R.id.spinner_item_text_view);
            textView.setText(mOnlineDevicesList.get(i).getName());
            TextView subTextView = view.findViewById(R.id.spinner_item_subtext_view);
            subTextView.setText(mOnlineDevicesList.get(i).getIpAddress());
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
            return mOnlineDevicesList.size();
        }

        @Override
        public Object getItem(int i) {
            return mOnlineDevicesList.get(i + 1);
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
            if(view == null){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_hint, viewGroup, false);
            }

            TextView textView = view.findViewById(R.id.spinner_hint);
            TextView subTextView = view.findViewById(R.id.spinner_hint_subtext);
            if(i < 0){
                textView.setText(getString(R.string.configuration_mode_spinner_hint));
                return view;
            } else {
                textView.setText(mOnlineDevicesList.get(i).getName());
                subTextView.setText(mOnlineDevicesList.get(i).getIpAddress());
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
            return mOnlineDevicesList.isEmpty();
        }
    }

    private class GetDeviceInfoTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMainLayout.setAlpha(0.5f);
            disableEnableControls(false, mMainLayout);
            mProgressDialogue.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpURLConnection urlConnection;
            URL url;
            String strLine;
            try{
                url = new URL("http://" + strings[0] + "/mcu_json");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                strLine = convertStreamToString(inputStream);
                Log.w("GetDeviceInfoTask", "Get device info success for device :" + strings[0]);
                return new JSONObject(strLine);
            } catch (Exception e){
                Log.w("GetDeviceInfoTask", "Get device info failure for device :" + strings[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try{
                loadDevicesIntoViews(jsonObject);
                mMainLayout.setAlpha(1f);
                disableEnableControls(true, mMainLayout);
            } catch (JSONException e){
                e.printStackTrace();
                mainActivity.showToast("Error connecting to device, rescan for online devices");
                mProgressBar.setVisibility(View.INVISIBLE);
                turnOffUI();
            }
            mProgressDialogue.dismiss();
        }

        private void disableEnableControls(boolean enable, ViewGroup vg){
            for (int i = 0; i < vg.getChildCount(); i++){
                View child = vg.getChildAt(i);
                child.setEnabled(enable);
                if (child instanceof ViewGroup){
                    disableEnableControls(enable, (ViewGroup)child);
                }
            }
        }

        private void loadDevicesIntoViews(JSONObject jsonObject) throws JSONException{
            clearAllTexts();

//            if(BuildConfig.DEBUG){
//                mDeviceName.setText("Device " +  debugDeviceCounter);
//                mPixelsPerStrip.setText("24");
//                mChunkSize.setText("30");
//                mMAPerPixel.setText("8");
//                mUDPPort.setText("2345");
//                mAMPSLimit.setText("34");
//                refreshColorPicker(123, 123, 123);
//                mWarmUpColorRed = "123";
//                mWarmUpColorGreen = "123";
//                mWarmUpColorBlue = "123";
//
//                debugDeviceCounter += 1;
//                return;
//            }

            if (jsonObject != null){
                mDeviceName.setText(jsonObject.getString(CONFIGURATION_DEVICE_NAME));
                mPixelsPerStrip.setText(jsonObject.getString(CONFIGURATION_PIXELS_PER_STRIP));
                mChunkSize.setText(jsonObject.getString(CONFIGURATION_CHUNK_SIZE));
                mMAPerPixel.setText(jsonObject.getString(CONFIGURATION_MA_PER_PIXEL));
                mUDPPort.setText(jsonObject.getString(CONFIGURATION_UDP_STREAMING_PORT));
                mAMPSLimit.setText(jsonObject.getString(CONFIGURATION_AMPS_LIMIT));
                String warmUpColor = jsonObject.getString(CONFIGURATION_WARMUP_COLOR);
                String warmUpRed = null;
                String warmUpGreen = null;
                String warmUpBlue = null;
                int lastPos = 0;
                for (int j = 0; j < warmUpColor.length(); j++){
                    if(warmUpColor.charAt(j) == ','){
                        if(warmUpRed == null){
                            warmUpRed = warmUpColor.substring(lastPos + 1, j);
                        } else if(warmUpGreen == null){
                            warmUpGreen = warmUpColor.substring(lastPos, j);
                        }
                        lastPos = j + 1;
                    }
                    if(warmUpBlue == null && j == warmUpColor.length() - 1){
                        warmUpBlue = warmUpColor.substring(lastPos, j);
                    }
                }
                refreshColorPicker(Integer.parseInt(warmUpRed), Integer.parseInt(warmUpGreen), Integer.parseInt(warmUpBlue));
                mWarmUpColorRed = warmUpRed;
                mWarmUpColorGreen = warmUpGreen;
                mWarmUpColorBlue = warmUpBlue;
            } else {
                throw new JSONException("null");
            }
        }

        /**
         * Convert the InputStream to a String.
         */
        private String convertStreamToString(InputStream is) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    private class UpdateDeviceTask extends AsyncTask<Void, Void, Void>{
        private DeviceEntity deviceEntity;
        private JSONObject configurationJSONObject;
        private String ipAddress;

        public UpdateDeviceTask(DeviceEntity deviceEntity, JSONObject configurationJSONObject) {
            this.deviceEntity = deviceEntity;
            this.configurationJSONObject = configurationJSONObject;
            this.ipAddress = deviceEntity.getIpAddress();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialogue.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            int TIMEOUT_VALUE = 10000;

            HttpURLConnection urlConnection;
            URL url;
            DataOutputStream dataOutputStream;
            try{
                url = new URL("http://" + ipAddress + "/mcu_json");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(configurationJSONObject.toString().getBytes().length));
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setConnectTimeout(TIMEOUT_VALUE);
                urlConnection.setReadTimeout(TIMEOUT_VALUE);
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                dataOutputStream.writeBytes(configurationJSONObject.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
                urlConnection.getInputStream();
                urlConnection.disconnect();
                Log.d("UpdateDevicePostCommand", "Success to http://" + ipAddress + "/mcu_config");

            } catch (Exception e){
                Log.d("UpdateDevicePostCommand", "Fail to http://" + ipAddress + "/mcu_config");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Update device in db
            updateDeviceVariables();
            configurationViewModel.updateDevice(deviceEntity);
            mProgressDialogue.dismiss();
        }
    }
}
