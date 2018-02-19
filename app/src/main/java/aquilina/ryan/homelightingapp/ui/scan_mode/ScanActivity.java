/*
 * Created by Ryan Aquilina on 10/9/17 3:30 PM
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/9/17 3:14 PM
 */

package aquilina.ryan.homelightingapp.ui.scan_mode;

import com.google.gson.Gson;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import aquilina.ryan.homelightingapp.Application;
import aquilina.ryan.homelightingapp.model.AllGroups;
import aquilina.ryan.homelightingapp.model.OnlineDevices;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.utils.Common;
import aquilina.ryan.homelightingapp.utils.Constants;

public class ScanActivity extends MainActivity {

    private Common common;

    private ArrayList<Device> mScannedDevicesList;
    private ArrayList<String> mCheckedDevicesList;
    private HashMap<String, Device> mDevicesMap;

    private RecyclerView mDevicesListView;
    private DeviceAdapter mDeviceAdapter;

    private FloatingActionButton mAddToGroupButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mNoDevicesTextView;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Set up views
        invalidateOptionsMenu();
        super.setSelectedNavMenuItem(R.id.nav_scan);
        mDevicesListView = findViewById(R.id.devices_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mDevicesListView.setLayoutManager(mLayoutManager);
        mAddToGroupButton =  findViewById(R.id.add_to_group_fab);
        mAddToGroupButton.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mNoDevicesTextView = findViewById(R.id.no_devices_found);
        mTitleTextView.setText(R.string.scan_mode_title);

        // set up data
        mDevicesMap = new HashMap<>();
        mCheckedDevicesList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter();
        mScannedDevicesList = new ArrayList<>();
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        common = new Common();

        // set up view functionality
        mDevicesListView.setAdapter(mDeviceAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWifiStateAndConnect();
            }
        });
        mAddToGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveGroupDialog();
            }
        });
        mNoDevicesTextView.setVisibility(View.GONE);
    }


    @Override
    protected void onStart() {
        super.onStart();

        mDevicesMap = common.loadDevices(this);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_SINGLE_DEVICES, null);
        OnlineDevices singleGroup = gson.fromJson(json, OnlineDevices.class);

        if(singleGroup != null){
            if(singleGroup.getDevicesList().isEmpty()) {
                getWifiStateAndConnect();
            } else {
                mScannedDevicesList = singleGroup.getDevicesList();
                ((Application)getApplicationContext()).setScannedDevices(singleGroup);
            }
        } else {
            getWifiStateAndConnect();
        }

        mNavigationView.setCheckedItem(R.id.nav_scan);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.refresh:
                getWifiStateAndConnect();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check wifi connection and get devices
     */
    private void getWifiStateAndConnect(){
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wm.isWifiEnabled()){
            new ScanForDevices().execute();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            mScannedDevicesList.clear();
            mDeviceAdapter.notifyDataSetChanged();
            common.showToast(getApplicationContext(), "No connections are available");
            mNoDevicesTextView.setText(getString(R.string.text_view_no_connection));
            mNoDevicesTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     *  Show save group dialog fragment.
     */
    private void showSaveGroupDialog(){
        DialogFragment dialogFragment = AddGroupDialog.newInstance();
        dialogFragment.show(getFragmentManager(), "AddGroupDialog");
    }

    /**
     * Refresh the list of devices
     */
    private void refreshDevices(){
        // Get wifi IP
        String subIP = "";
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        int noOfDots = 0;
        for(int i = 0; noOfDots < 3; i++){
            subIP += ip.substring(i, i + 1);
            if(ip.charAt(i) == '.'){
                noOfDots += 1;
            }
        }

        // Execute a queue of tasks for each possible device
        ExecutorService executorService = Executors.newFixedThreadPool(255);
        for( int i = 0; i <= 255; i++){
            Runnable worker = new WorkerThread(subIP, i);
            executorService.execute(worker);
        }
        executorService.shutdown();

        while(!executorService.isTerminated()){
            // Do nothing
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                saveSingleFixturesLocally();
            }
        });
    }

    /**
     * Saves the group of devices locally
     */
    protected void saveGroupLocally(String groupName){
        AllGroups allGroups;
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_DEVICES_GROUPS, null);
        if(json == null){
            allGroups = new AllGroups();
        } else {
            allGroups = gson.fromJson(json, AllGroups.class);
        }

        DevicesGroup group = new DevicesGroup(allGroups.getGroups().size() + 1, groupName, mCheckedDevicesList);
        allGroups.addGroup(group);
        json = gson.toJson(allGroups);
        prefsEditor.putString(Constants.GROUP_OF_DEVICES_GROUPS, json);
        removeCheckedItems();
        common.showToast(getApplicationContext(), getString(R.string.toast_group_saved));
        prefsEditor.commit();
    }

    /**
     * Removes items in listview that are checked
     * and removes items from ArrayList
     */
    private void removeCheckedItems(){
        mCheckedDevicesList.clear();
        mDeviceAdapter = new DeviceAdapter();
        mDevicesListView.setAdapter(mDeviceAdapter);
        changeVisibilityOfAddGroupButton();
    }

    /**
     * Changes the visibility of the add
     * group floating action button according to
     * list size
     */
    private void changeVisibilityOfAddGroupButton(){
        if(mCheckedDevicesList.isEmpty()){
            mAddToGroupButton.setVisibility(View.INVISIBLE);
        }
        else {
            mAddToGroupButton.setVisibility(View.VISIBLE);
        }
    }

    @Nullable
    private Device getDeviceConnectedToWifi(String subIP, int i){
        int TIMEOUT_VALUE = 2000;

        HttpURLConnection urlConnection;
        URL url;
        String strLine;
        try{
            url = new URL("http://" + subIP + Integer.toString(i) + "/mcu_info");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_VALUE);
            urlConnection.setReadTimeout(TIMEOUT_VALUE);
            urlConnection.setUseCaches(false);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            strLine = convertStreamToString(inputStream);
            if(!strLine.isEmpty()){
                if(strLine.substring(0,5).equals("name:")){
                    int nameLength = 0;
                    for(int j = 0;j <= strLine.length(); j++){
                        if(strLine.charAt(j) != ','){
                            nameLength += 1;
                        }
                        else{
                            break;
                        }
                    }
                    return new Device((strLine.substring(5, nameLength)), subIP + Integer.toString(i));
                }
            }
            Log.w("Good Device Ip", subIP + Integer.toString(i));
        } catch (Exception e){
            Log.w("Bad Device Ip", subIP + Integer.toString(i));
        }
        return null;
    }

    /**
     * Convert the InputStream to a String.
     */
    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
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

    /**
     * Saves the scanned devices locally
     */
    private boolean saveSingleFixturesLocally(){
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        OnlineDevices singleGroup = new OnlineDevices(mScannedDevicesList);

        Gson gson = new Gson();
        String json = gson.toJson(singleGroup);
        prefsEditor.putString(Constants.GROUP_OF_SINGLE_DEVICES, json);

        // Save the list in memory
        ((Application)getApplicationContext()).setScannedDevices(singleGroup);
        return prefsEditor.commit();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView deviceNameTextView;
        TextView deviceIPAddressTextView;
        CheckBox checkBox;
        CardView cardView;
        ViewHolderClick mListener;

        private ViewHolder(View v, ViewHolderClick listener) {
            super(v);
            mListener = listener;
            deviceNameTextView =  v.findViewById(R.id.device_name_text_view);
            deviceIPAddressTextView =  v.findViewById(R.id.device_ip_address_text_view);
            checkBox = v.findViewById(R.id.item_checkbox);
            cardView = v.findViewById(R.id.item_card_view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardViewClick(view);
        }

        private interface ViewHolderClick{
            void onCardViewClick(View view);
        }
    }

    private class DeviceAdapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_device, parent, false);
            return new ViewHolder(itemView, new ViewHolder.ViewHolderClick() {
                @Override
                public void onCardViewClick(View view) {
                    CheckBox cb = view.findViewById(R.id.item_checkbox);
                    String ip = (String) cb.getTag();
                    if(!cb.isChecked()){
                        cb.setChecked(true);
                        mCheckedDevicesList.add(ip);
                        changeVisibilityOfAddGroupButton();
                    }
                    else{
                        cb.setChecked(false);
                        for(int i = 0; i < mCheckedDevicesList.size(); i++){
                            if(mCheckedDevicesList.get(i).equals(ip)){
                                mCheckedDevicesList.remove(i);
                                changeVisibilityOfAddGroupButton();
                                return;
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            Device device = mScannedDevicesList.get(position);

            //assign data to views
            holder.checkBox.setTag(device.getIpAddress());
            holder.deviceNameTextView.setText(device.getName());
            holder.deviceNameTextView.setTypeface(mTextTypeFace);
            holder.deviceIPAddressTextView.setText(device.getIpAddress());
            holder.deviceIPAddressTextView.setTypeface(mSubTextTypeFace);
        }

        @Override
        public int getItemCount() {
            return mScannedDevicesList.size();
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

    private class ScanForDevices extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            refreshDevices();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mSwipeRefreshLayout.setRefreshing(true);
            mScannedDevicesList.clear();
            mDevicesMap = common.loadDevices(getApplicationContext());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDeviceAdapter.notifyDataSetChanged();
            common.saveDeviceHashMap(mDevicesMap, getApplicationContext());
            if(mScannedDevicesList.isEmpty()){
                mNoDevicesTextView.setText(getString(R.string.text_view_no_devices_found));
                mNoDevicesTextView.setVisibility(View.VISIBLE);
            }
            else{
                mNoDevicesTextView.setVisibility(View.GONE);
            }
        }
    }

    private class WorkerThread implements Runnable{
        private String subIp;
        private int i;

        private WorkerThread(String subIp, int i) {
            this.subIp = subIp;
            this.i = i;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(-19);
            final Device device = getDeviceConnectedToWifi(subIp, i);
            if(device != null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDevicesMap.put(device.getIpAddress(), device);
                        mScannedDevicesList.add(device);
                        mDeviceAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }
}
