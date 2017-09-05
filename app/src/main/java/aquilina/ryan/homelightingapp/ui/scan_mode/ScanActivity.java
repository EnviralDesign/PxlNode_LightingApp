package aquilina.ryan.homelightingapp.ui.scan_mode;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.ui.MainActivity;
import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;

/**
 * Created by SterlingRyan on 9/4/2017.
 */

public class ScanActivity extends MainActivity{

    private ArrayList<Device> mCheckedDevicesList;
    private ArrayList<Device> mScannedDevicesList;
    private ListView mDevicesListView;
    private DeviceAdapter mDeviceAdapter;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Set up views
        mDevicesListView = (ListView) findViewById(R.id.devices_list_view);
        FloatingActionButton refreshFAB = (FloatingActionButton) findViewById(R.id.refresh_button);
        FloatingActionButton addGroupFAB = (FloatingActionButton) findViewById(R.id.add_group);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mProgressBar.setMax(10);

        // set up data
        mCheckedDevicesList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter();
        mScannedDevicesList = new ArrayList<>();

        // set up view functionality
        mDevicesListView.setAdapter(mDeviceAdapter);
        addGroupFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveGroupDialog();
            }
        });
        refreshFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshDevices();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        refreshDevices();
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
        mScannedDevicesList.clear();
        new ScanForDevices().execute();
    }


    protected boolean saveGroupLocally(String groupName){
        DevicesGroup group = new DevicesGroup(groupName, mCheckedDevicesList);
        //TODO save group

        return true;
    }

    private static class ViewHolder{
        TextView deviceNameTextView;
        TextView deviceIPAddressTextView;
        CheckBox checkBox;
    }

    private class DeviceAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mScannedDevicesList.size();
        }

        @Override
        public Object getItem(int i) {
            return mScannedDevicesList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if(view == null){
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
                view = inflater.inflate(R.layout.item_single_device, viewGroup, false);

                // Set up View Holder
                viewHolder = new ViewHolder();
                viewHolder.deviceNameTextView = (TextView) view.findViewById(R.id.device_name_text_view);
                viewHolder.deviceIPAddressTextView = (TextView) view.findViewById(R.id.device_ip_address_text_view);
                viewHolder.checkBox = (CheckBox) view.findViewById(R.id.checkbox);

                // Store the view holder with the view
                view.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) view.getTag();
            }

            // Populate views with data
            viewHolder.checkBox.setTag(R.id.deviceName, viewHolder.deviceNameTextView.getText().toString());
            viewHolder.checkBox.setTag(R.id.deviceIpAddress, viewHolder.deviceIPAddressTextView.getText().toString());
            viewHolder.deviceNameTextView.setText(mScannedDevicesList.get(i).getName());
            viewHolder.deviceIPAddressTextView.setText(mScannedDevicesList.get(i).getIpAddress());

            // assign view functionality
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    Device device = new Device((String) cb.getTag(R.id.deviceName),(String) cb.getTag(R.id.deviceIpAddress));
                    if(cb.isChecked()){
                        mCheckedDevicesList.add(device);
                    }
                    else{
                        for(int i = 0; i < mCheckedDevicesList.size(); i++){
                            if(mCheckedDevicesList.get(i).getIpAddress().equals(device.getIpAddress())){
                                mCheckedDevicesList.remove(i);
                                return;
                            }
                        }
                    }
                }
            });
            return view;
        }

        @Override
        public CharSequence[] getAutofillOptions() {
            return new CharSequence[0];
        }
    }

    private class ScanForDevices extends AsyncTask<Void , Integer, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            //TODO refresh devices
            for(int i = 0; i <= 10; i++){
                publishProgress(i);
                mScannedDevicesList.add(new Device("192.8.8.8", "Light Name"));
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
            mDeviceAdapter.notifyDataSetChanged();
            mDevicesListView.requestLayout();
        }
    }
}
