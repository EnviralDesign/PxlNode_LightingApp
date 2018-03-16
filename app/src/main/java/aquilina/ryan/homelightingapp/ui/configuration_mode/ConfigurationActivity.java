/*
 * Created by Ryan Aquilina on 2/19/18 2:55 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2018.  All rights reserved
 *
 * Last modified 2/19/18 2:55 PM
 */

package aquilina.ryan.homelightingapp.ui.configuration_mode;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aquilina.ryan.homelightingapp.Application;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.OnlineDevices;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Common;
import aquilina.ryan.homelightingapp.utils.Constants;
import fr.ganfra.materialspinner.MaterialSpinner;

public class ConfigurationActivity extends MainActivity {

    public ArrayList<Device> mOnlineDevicesList = new ArrayList<>();
    private OnlineDevices mOnlineDevices;

    private MaterialSpinner mSpinner;
    private MaterialEditText mDeviceName;
    private MaterialEditText mPixelsPerStrip;
    private MaterialEditText mChunkSize;
    private MaterialEditText mMAPerPixel;
    private MaterialEditText mUDPPort;
    private MaterialEditText mAMPSLimit;
    private MaterialEditText mWarmUpColorRed;
    private MaterialEditText mWarmUpColorGreen;
    private MaterialEditText mWarmUpColorBlue;
    private ScrollView mMainLayout;
    private LinearLayout mHintTextView;

    private Common common = new Common();


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

    private TextWatcher mWarmUpColorBlueWatcher = new TextWatcher() {
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
            mWarmUpColorBlue.setError(null);
            patternMatcher = pattern.matcher(editable.toString());

            if (!editable.toString().equals("")) {
                Integer warmUpColor = Integer.valueOf(editable.toString());
                if (!patternMatcher.matches()) {
                    mWarmUpColorBlue.setError(getString(R.string.configuration_error_special_characters));
                } else if (warmUpColor > 255 || warmUpColor < 0) {
                    mWarmUpColorBlue.setError("");
                }
            }
        }
    };

    private TextWatcher mWarmUpColorGreenWatcher = new TextWatcher() {
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
            mWarmUpColorGreen.setError(null);
            patternMatcher = pattern.matcher(editable.toString());

            if (!editable.toString().equals("")) {
                Integer warmUpColor = Integer.valueOf(editable.toString());
                if (!patternMatcher.matches()) {
                    mWarmUpColorGreen.setError(getString(R.string.configuration_error_special_characters));
                } else if (warmUpColor > 255 || warmUpColor < 0) {
                    mWarmUpColorGreen.setError("");
                }
            }
        }
    };

    private TextWatcher mWarmUpColorRedWatcher = new TextWatcher() {
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
            mWarmUpColorRed.setError(null);
            patternMatcher = pattern.matcher(editable.toString());

            if (!editable.toString().equals("")) {
                Integer warmUpColor = Integer.valueOf(editable.toString());
                if (!patternMatcher.matches()) {
                    mWarmUpColorRed.setError(getString(R.string.configuration_error_special_characters));
                } else if (warmUpColor > 255 || warmUpColor < 0) {
                    mWarmUpColorRed.setError("");
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        getWindow().setBackgroundDrawable(null);
        super.setSelectedNavMenuItem(R.id.nav_configuration);

        mTitleTextView.setText(R.string.configuration_mode_title);
        mSpinner = findViewById(R.id.item_spinner);
        mDeviceName = findViewById(R.id.device_name_edit_text);
        mPixelsPerStrip = findViewById(R.id.pixels_per_strip_edit_text);
        mChunkSize = findViewById(R.id.chunk_size_edit_text);
        mMAPerPixel = findViewById(R.id.ma_per_strip);
        mUDPPort = findViewById(R.id.udp_port_edit_text);
        mAMPSLimit = findViewById(R.id.amps_limit_edit_text);
        mWarmUpColorRed = findViewById(R.id.warm_up_red_edit_text);
        mWarmUpColorGreen = findViewById(R.id.warm_up_green_edit_text);
        mWarmUpColorBlue = findViewById(R.id.warm_up_blue_edit_text);
        mMainLayout = findViewById(R.id.main_layout);
        mHintTextView = findViewById(R.id.text_view_hint);
        Button mUpdateDeviceButton = findViewById(R.id.update_device_button);

        mDeviceName.addTextChangedListener(mDeviceNameWatcher);
        mPixelsPerStrip.addTextChangedListener(mPixelPerStripWatcher);
        mChunkSize.addTextChangedListener(mChunkSizeWatcher);
        mMAPerPixel.addTextChangedListener(mMAPerPixelWatcher);
        mUDPPort.addTextChangedListener(mUDPPortWatcher);
        mAMPSLimit.addTextChangedListener(mAmpsLimitWatcher);
        mWarmUpColorBlue.addTextChangedListener(mWarmUpColorBlueWatcher);
        mWarmUpColorGreen.addTextChangedListener(mWarmUpColorGreenWatcher);
        mWarmUpColorRed.addTextChangedListener(mWarmUpColorRedWatcher);

        mUpdateDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    validateUserInput();
                } catch (JSONException e){
                    common.showToast(getApplicationContext(), "Invalid input");
                }

            }
        });

        mOnlineDevices = ((Application)getApplicationContext()).getScannedDevices();
        if(mOnlineDevices != null) {
            mOnlineDevicesList = mOnlineDevices.getDevicesList();
        }
        mSpinner.setAdapter(new CustomSpinnerAdapter());
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Device selectedDevice = (Device) adapterView.getAdapter().getItem(i);

                clearAllTexts();

                mDeviceName.setText(selectedDevice.getName());
                if(selectedDevice.getPixelsPerStrip() != -1){
                    mPixelsPerStrip.setText(String.format(Locale.ENGLISH,"%d",selectedDevice.getPixelsPerStrip()));
                }
                if(selectedDevice.getChunkSize() != -1){
                    mChunkSize.setText(String.format(Locale.ENGLISH,"%d",selectedDevice.getChunkSize()));
                }
                if(selectedDevice.getMaPerPixel() != -1){
                    mMAPerPixel.setText(String.format(Locale.ENGLISH,"%d",selectedDevice.getMaPerPixel()));
                }
                if(selectedDevice.getUdpStreamingPort() != -1){
                    mUDPPort.setText(String.format(Locale.ENGLISH,"%d",selectedDevice.getUdpStreamingPort()));
                }
                if(selectedDevice.getAmpsLimit() != -1f){
                    mAMPSLimit.setText(String.format(Locale.ENGLISH,"%f",selectedDevice.getAmpsLimit()));
                }
                if(!selectedDevice.getWarmUpColor().equals("-1")){
                    String warmUpColor = selectedDevice.getWarmUpColor();
                    String warmUpRed = null;
                    String warmUpGreen = null;
                    String warmUpBlue = null;
                    int lastPos = 0;
                    for (int j = 0; j < warmUpColor.length(); j++){
                        if(warmUpColor.charAt(j) == ' '){
                            if(warmUpRed == null){
                                warmUpRed = warmUpColor.substring(lastPos, j);
                            } else if(warmUpGreen == null){
                                warmUpGreen = warmUpColor.substring(lastPos, j);
                            }
                            lastPos = j + 1;
                        }
                        if(warmUpBlue == null && j == warmUpColor.length() - 1){
                            warmUpBlue = warmUpColor.substring(lastPos, j + 1);
                        }
                    }

                    mWarmUpColorRed.setText(warmUpRed);
                    mWarmUpColorGreen.setText(warmUpGreen);
                    mWarmUpColorBlue.setText(warmUpBlue);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIfOnlineDevicesIsNotEmpty();
    }

    /**
     * Clears the texts inside the edit texts
     */
    private void clearAllTexts(){
        mDeviceName.setText("");
        mPixelsPerStrip.setText("");
        mChunkSize.setText("");
        mMAPerPixel.setText("");
        mUDPPort.setText("");
        mAMPSLimit.setText("");
        mWarmUpColorRed.setText("");
        mWarmUpColorGreen.setText("");
        mWarmUpColorBlue.setText("");
    }

    /**
     * Validate user input and send json
     */
    private void validateUserInput() throws JSONException{
        Boolean areInputsValid = true;

        // Check if any of the edit texts are empty.
        if(mDeviceName.getText().toString().isEmpty()){
            mDeviceName.setError(getString(R.string.configuration_error_empty));
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
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put(Constants.CONFIGURATION_DEVICE_NAME, mDeviceName.getText().toString());
            jsonMessage.put(Constants.CONFIGURATION_PIXELS_PER_STRIP, mPixelsPerStrip.getText().toString());
            jsonMessage.put(Constants.CONFIGURATION_CHUNK_SIZE, mChunkSize.getText().toString());
            jsonMessage.put(Constants.CONFIGURATION_UDP_STREAMING_PORT, mUDPPort.getText().toString());
            jsonMessage.put(Constants.CONFIGURATION_MA_PER_PIXEL, mMAPerPixel.getText().toString());
            jsonMessage.put(Constants.CONFIGURATION_AMPS_LIMIT, mAMPSLimit.getText().toString());
            jsonMessage.put(Constants.CONFIGURATION_WARMUP_COLOR, new JSONArray(
                    new String[]{
                        mWarmUpColorRed.getText().toString(),
                        mWarmUpColorGreen.getText().toString(),
                        mWarmUpColorBlue.getText().toString()
                    }
            ));
            updateDeviceVariables();
            sendUpdateDeviceCommand(jsonMessage);
        }
    }

    /**
     * Check if the list of online devices is empty
     * if so then update the UI.
     */
    private void checkIfOnlineDevicesIsNotEmpty(){
        if(mOnlineDevicesList.isEmpty()){
            mMainLayout.setVisibility(View.GONE);
            mHintTextView.setVisibility(View.VISIBLE);
        } else {
            mHintTextView.setVisibility(View.GONE);
            mMainLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Update the variables of the device
     */
    private void updateDeviceVariables(){
        Device device = (Device) mSpinner.getAdapter().getItem(mSpinner.getSelectedItemPosition() - 1);
        device.setName(mDeviceName.getText().toString());
        device.setPixelsPerStrip(mPixelsPerStrip.getText().toString().equals("") ? -1 : Short.decode(mPixelsPerStrip.getText().toString()));
        device.setChunkSize(mChunkSize.getText().toString().equals("") ? -1 : Short.decode(mChunkSize.getText().toString()));
        device.setUdpStreamingPort(mUDPPort.getText().toString().equals("") ? -1 : Integer.valueOf(mUDPPort.getText().toString()));
        device.setMaPerPixel(mMAPerPixel.getText().toString().equals("") ? -1 : Short.decode(mMAPerPixel.getText().toString()));
        device.setAmpsLimit(mAMPSLimit.getText().toString().equals("") ? -1f :Float.parseFloat(mAMPSLimit.getText().toString()));

        Boolean isValidColor = true;

        if(mWarmUpColorRed.getText().toString().equals("")){
            isValidColor = false;
        } else if(mWarmUpColorGreen.getText().toString().equals("")){
            isValidColor = false;
        }else if(mWarmUpColorBlue.getText().toString().equals("")){
            isValidColor = false;
        }
        if(isValidColor){
            device.setWarmUpColor(mWarmUpColorRed.getText().toString()
                            + " "
                            + mWarmUpColorGreen.getText().toString()
                            + " "
                            + mWarmUpColorBlue.getText().toString());
        }

        mOnlineDevices.setDeviceByIP(device.getIpAddress(), device);
        ((Application)getApplication()).setScannedDevices(mOnlineDevices);
        common.saveDevice(device, this);
        refreshSpinnerAdapter();
    }

    /**
     * Refreshes the spinner adapter.
     */
    private void refreshSpinnerAdapter(){
        // Let Java garbage collect the last adapter.
        mSpinner.setAdapter(new CustomSpinnerAdapter());
        checkIfOnlineDevicesIsNotEmpty();
    }

    /**
     * Send update json to device
     */
    public void sendUpdateDeviceCommand(JSONObject json){
       Runnable updateDeviceRequest = new UpdateDeviceRequest(json,
               ((Device) mSpinner.getAdapter().getItem(
                       mSpinner.getSelectedItemPosition() - 1))
                       .getIpAddress());

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(updateDeviceRequest);
    }

    private static class UpdateDeviceRequest implements Runnable{
        JSONObject json;
        String ipAddress;

        public UpdateDeviceRequest(JSONObject json, String ipAddresses) {
            this.json = json;
            this.ipAddress = ipAddresses;
        }

        @Override
        public void run() {
            HttpURLConnection urlConnection;
            URL url;
            DataOutputStream dataOutputStream;
            try{
                url = new URL("http://" + ipAddress + "/mcu_config");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Length", Integer.toString(json.toString().getBytes().length));
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                urlConnection.setUseCaches(false);
                urlConnection.setDoInput(true);
                dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                dataOutputStream.writeBytes(json.toString());
                dataOutputStream.flush();
                dataOutputStream.close();
                urlConnection.getInputStream();
                urlConnection.disconnect();
                Log.d("UpdateDevicePostCommand", "Success to http://" + ipAddress + "/mcu_config");
            } catch (Exception e){
                Log.d("UpdateDevicePostCommand", "Fail to http://" + ipAddress + "/mcu_config");
            }
        }
    }


    /**
     * Custom adapter that loads online devices only.
     */
    private class CustomSpinnerAdapter implements SpinnerAdapter{

        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
            TextView textView = view.findViewById(R.id.spinner_item_text_view);
            textView.setText(mOnlineDevicesList.get(i).getName());
            textView.setTypeface(mTextTypeFace);
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
            textView.setTypeface(mTextTypeFace);
            if(i < 0){
                textView.setText(getString(R.string.configuration_mode_spinner_hint));
                return view;
            } else {
                textView.setText(mOnlineDevicesList.get(i).getName());
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
}
