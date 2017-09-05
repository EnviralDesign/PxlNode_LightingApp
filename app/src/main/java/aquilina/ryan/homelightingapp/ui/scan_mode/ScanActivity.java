package aquilina.ryan.homelightingapp.ui.scan_mode;

import com.google.gson.Gson;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import aquilina.ryan.homelightingapp.ui.MainActivity;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;

/**
 * Created by SterlingRyan on 9/4/2017.
 */

public class ScanActivity extends MainActivity {

    private ArrayList<Device> mCheckedDevicesList;
    private ArrayList<Device> mScannedDevicesList;
    private RecyclerView mDevicesListView;
    private RecyclerView.Adapter mDeviceAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressBar mProgressBar;
    private FloatingActionButton mAddToGroupButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Set up views
        invalidateOptionsMenu();
        mDevicesListView = (RecyclerView) findViewById(R.id.devices_list_view);
        mLayoutManager = new LinearLayoutManager(this);
        mDevicesListView.setLayoutManager(mLayoutManager);
        mAddToGroupButton = (FloatingActionButton) findViewById(R.id.add_to_group_fab);
        mAddToGroupButton.setVisibility(View.INVISIBLE);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.setMax(10);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);

        // set up data
        mCheckedDevicesList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter();
        mScannedDevicesList = new ArrayList<>();

        // set up view functionality
        mDevicesListView.setAdapter(mDeviceAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ScanForDevices().execute();
            }
        });
        mAddToGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveGroupDialog();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        refreshDevices();
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
            case R.id.refresh_button:
                refreshDevices();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  Show save group dialog
     *  fragment.
     */
    private void showSaveGroupDialog(){
        DialogFragment dialogFragment = AddGroupDialog.newInstance();
        dialogFragment.show(getFragmentManager(), "AddGroupDialog");
    }

    /**
     * Refresh the list of devices
     */
    private void refreshDevices(){
        new ScanForDevices().execute();
    }

    /**
     * Saves the group of devices
     * locally
     */
    protected boolean saveGroupLocally(String groupName){
        DevicesGroup group = new DevicesGroup(groupName, mCheckedDevicesList);
        mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(group);
        prefsEditor.putString(groupName, json);
        removeCheckedItems();
        showToast(R.string.toast_group_saved);
        return prefsEditor.commit();

//        To Retrieve
//        Gson gson = new Gson();
//        String json = mPrefs.getString("MyObject", "");
//        MyObject obj = gson.fromJson(json, MyObject.class);
    }

    /**
     * Removes items in listview that are checked
     * and removes items from ArrayList
     */
    private void removeCheckedItems(){
        CheckBox cb;
        for(int i = 0; i < mDeviceAdapter.getItemCount(); i++){
            cb = (CheckBox) (mDevicesListView.findViewHolderForAdapterPosition(i).itemView).findViewById(R.id.item_checkbox);
            cb.setChecked(false);
        }
        mCheckedDevicesList.clear();
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

    private void showToast(int stringID){
        Toast.makeText(this, getString(stringID),Toast.LENGTH_SHORT).show();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
        TextView deviceNameTextView;
        TextView deviceIPAddressTextView;
        CheckBox checkBox;
        CardView cardView;

        public ViewHolder(View v) {
            super(v);
            deviceNameTextView = (TextView) v.findViewById(R.id.device_name_text_view);
            deviceIPAddressTextView = (TextView) v.findViewById(R.id.device_ip_address_text_view);
            checkBox = (CheckBox) v.findViewById(R.id.item_checkbox);
            cardView = (CardView)v.findViewById(R.id.item_card_view);
        }
    }

    private class DeviceAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_single_device, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Device device = mScannedDevicesList.get(position);

            //assign data to views
            holder.checkBox.setTag(R.id.deviceName, device.getName());
            holder.checkBox.setTag(R.id.deviceIpAddress, device.getIpAddress());
            holder.deviceNameTextView.setText(device.getName());
            holder.deviceIPAddressTextView.setText(device.getIpAddress());

            //assign view functionality
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = holder.checkBox;
                    Device device = new Device((String) cb.getTag(R.id.deviceName),(String) cb.getTag(R.id.deviceIpAddress));
                    if(!cb.isChecked()){
                        cb.setChecked(true);
                        mCheckedDevicesList.add(device);
                        changeVisibilityOfAddGroupButton();
                    }
                    else{
                        cb.setChecked(false);
                        for(int i = 0; i < mCheckedDevicesList.size(); i++){
                            if(mCheckedDevicesList.get(i).getIpAddress().equals(device.getIpAddress())){
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
        public int getItemCount() {
            return mScannedDevicesList.size();
        }
    }

    private class ScanForDevices extends AsyncTask<Void , Integer, Void>{

        private ArrayList<Device> DevicesList;

        @Override
        protected Void doInBackground(Void... voids) {
            //TODO refresh devices
            DevicesList = new ArrayList<>();
            for(int i = 0; i < 20; i++){
                publishProgress(i);
                DevicesList.add(new Device("192.8.8.8", "Light Name"));
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgressBar.setVisibility(View.INVISIBLE);
            mScannedDevicesList.clear();
            mScannedDevicesList = DevicesList;
            mDeviceAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}