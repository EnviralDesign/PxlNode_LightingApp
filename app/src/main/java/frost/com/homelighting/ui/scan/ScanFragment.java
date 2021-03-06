package frost.com.homelighting.ui.scan;

import android.app.DialogFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import frost.com.homelighting.BuildConfig;
import frost.com.homelighting.HomeLightingApplication;
import frost.com.homelighting.MainActivity;
import frost.com.homelighting.R;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.GroupDetailsEntity;
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.db.entity.OnlineDeviceEntity;
import frost.com.homelighting.viewmodel.ScanViewModel;

import static android.content.Context.WIFI_SERVICE;

public class ScanFragment extends Fragment {
    private static final String TAG = "SCAN_FRAGMENT";

    private List<DeviceEntity> mScannedDevicesList;
    private List<String> mCheckedDevicesList;

    private RecyclerView mDevicesRecyclerView;
    private DeviceAdapter mDeviceAdapter;

    private FloatingActionButton mAddToGroupButton;
    private ProgressBar mProgressBar;
    private TextView mNoDevicesTextView;

    private MainActivity mMainActivity;

    private int debugDeviceCounter = 0;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ScanViewModel scanViewModel;


    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mMainActivity = ((MainActivity) getActivity());


        ((HomeLightingApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        scanViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ScanViewModel.class);

        scanViewModel.getOnlineDevices().observe(this, new Observer<List<DeviceEntity>>() {
            @Override
            public void onChanged(@Nullable List<DeviceEntity> deviceEntities) {
                mScannedDevicesList = deviceEntities;
                mDeviceAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMainActivity.invalidateOptionsMenu();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mMainActivity);
        mDevicesRecyclerView.setLayoutManager(mLayoutManager);
        mMainActivity.setTitle(R.string.scan_mode_title);
        mDevicesRecyclerView.setAdapter(mDeviceAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        mDevicesRecyclerView = view.findViewById(R.id.devices_recycler_view);
        mAddToGroupButton =  view.findViewById(R.id.add_to_group_fab);
        mAddToGroupButton.setVisibility(View.INVISIBLE);
        mProgressBar = view.findViewById(R.id.progressBar);
        mNoDevicesTextView = view.findViewById(R.id.no_devices_found);

        mCheckedDevicesList = new ArrayList<>();
        mScannedDevicesList = new ArrayList<>();
        mDeviceAdapter = new DeviceAdapter();

        mAddToGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSaveGroupDialog();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
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
        WifiManager wm = (WifiManager) mMainActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wm.isWifiEnabled()){
            new ScanForDevices().execute();
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mScannedDevicesList.clear();
            mNoDevicesTextView.setText(getString(R.string.text_view_no_connection));
            mNoDevicesTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     *  Show save group dialog fragment.
     */
    private void showSaveGroupDialog(){
        DialogFragment dialogFragment = AddGroupDialog.newInstance();
        dialogFragment.show(mMainActivity.getFragmentManager(), "AddGroupDialog");
    }

    /**
     *  Check if name is already taken
     */
    public boolean checkIfGroupNameAlreadyExists(String presetName){
        List<String> presetNames = scanViewModel.getGroupNames();
        for (String presetNameTaken : presetNames) {
            if(presetNameTaken.equals(presetName)){
                mMainActivity.showToast(R.string.toast_duplicate_name);
                return false;
            }
        }
        return true;
    }

    /**
     * Refresh the list of devices
     */
//    private void refreshDevices(){
//        // Get wifi IP
//        String subIP = "";
//        WifiManager wm = (WifiManager) mMainActivity.getApplicationContext().getSystemService(WIFI_SERVICE);
//
//        if(wm != null){
//            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//
//            int noOfDots = 0;
//            for(int i = 0; noOfDots < 3; i++){
//                subIP += ip.substring(i, i + 1);
//                if(ip.charAt(i) == '.'){
//                    noOfDots += 1;
//                }
//            }
//
//            // Execute a queue of tasks for each possible device
//            ExecutorService executorService = Executors.newFixedThreadPool(255);
//            for( int i = 0; i <= 255; i++){
//                Runnable worker = new WorkerThread(subIP, i);
//                executorService.execute(worker);
//            }
//            executorService.shutdown();
//
//            while(!executorService.isTerminated()){
//                // Do nothing
//            }
//        }
//    }

    private void refreshDevices(){
        Thread thread = new Thread(new Runnable() {
            String UDP_IP = "192.168.4.1";
            int UDP_PORT = 2390;
            String message = "EnviralDesignPxlNode\\xC8";

            @Override
            public void run() {
                DatagramSocket datagramSocket = null;
                try {
                    datagramSocket = new DatagramSocket();
//                    datagramSocket.setBroadcast(true);
                    InetAddress serverAddress = InetAddress.getByName(UDP_IP);
                    byte[] buffer = message.getBytes();
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, serverAddress, UDP_PORT);
                    datagramSocket.receive(datagramPacket);
                    String stringData = new String (buffer, 0 , datagramPacket.getLength());
                    Log.i("HERE RYAN :", stringData);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    /**
     * Saves the group of devices
     */
    protected void saveGroup(String groupName){
        GroupEntity groupEntity = new GroupEntity(groupName);
        ArrayList<String> devicesIpAddress = new ArrayList<>(mCheckedDevicesList);
        new SaveGroupTask(devicesIpAddress).execute(groupEntity);

        removeCheckedItems();
    }

    /**
     * Removes items in listview that are checked
     * and removes items from checked ArrayList.
     */
    private void removeCheckedItems(){
        mCheckedDevicesList.clear();
        mDeviceAdapter = new DeviceAdapter();
        mDevicesRecyclerView.setAdapter(mDeviceAdapter);
        changeVisibilityOfAddGroupButton();
    }

    /**
     * Changes the visibility of the add
     * group floating action button according to
     * list size.
     */
    private void changeVisibilityOfAddGroupButton(){
        if(mCheckedDevicesList.isEmpty()){
            mAddToGroupButton.setVisibility(View.INVISIBLE);
        }
        else {
            mAddToGroupButton.setVisibility(View.VISIBLE);
        }
    }

    private DeviceEntity getDeviceConnectedToWifi(String subIP, int i){
        int TIMEOUT_VALUE = 5000;

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
                    return new DeviceEntity((strLine.substring(5, nameLength)), subIP + Integer.toString(i));
                }
            }
            Log.w("Good Device Ip", subIP + Integer.toString(i));
        } catch (Exception e){
            Log.w("Bad Device Ip", subIP + Integer.toString(i));
        }
        if(BuildConfig.DEBUG){
            debugDeviceCounter += 1;
            if(debugDeviceCounter <= 2){
                return new DeviceEntity( "Device " +  Integer.toString(i), subIP + Integer.toString(i));
            }
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

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView deviceNameTextView;
        TextView deviceIPAddressTextView;
        CheckBox checkBox;
        LinearLayout cardView;
        ViewHolderClick mListener;

        private ViewHolder(View v, ViewHolderClick listener) {
            super(v);
            mListener = listener;
            deviceNameTextView =  v.findViewById(R.id.device_name_edit_text);
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
        @NonNull
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
            DeviceEntity device = mScannedDevicesList.get(position);

            //assign data to views
            holder.checkBox.setTag(device.getIpAddress());
            holder.deviceNameTextView.setText(device.getName());
            holder.deviceIPAddressTextView.setText(device.getIpAddress());
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

//    public class ScanForDevices extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            scanViewModel.deleteAllOnlineDevices();
//            refreshDevices();
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mProgressBar.setVisibility(View.VISIBLE);
//            mScannedDevicesList.clear();
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            scanViewModel.insertAllDevices(mScannedDevicesList);
//            scanViewModel.saveOnlineDevices(changeToOnlineDevices(mScannedDevicesList));
//            if(mScannedDevicesList.isEmpty()){
//                mNoDevicesTextView.setText(getString(R.string.text_view_no_devices_found));
//                mNoDevicesTextView.setVisibility(View.VISIBLE);
//            }
//            else{
//                mNoDevicesTextView.setVisibility(View.GONE);
//            }
//            mProgressBar.setVisibility(View.INVISIBLE);
//        }
//
//        private List<OnlineDeviceEntity> changeToOnlineDevices(List<DeviceEntity> deviceEntities){
//            List<OnlineDeviceEntity> onlineDevices = new ArrayList<>();
//            for(DeviceEntity deviceEntity: deviceEntities){
//                onlineDevices.add(new OnlineDeviceEntity(deviceEntity.getIpAddress()));
//            }
//            return onlineDevices;
//        }
//    }

    public class PacketSenderAndReceiver implements Runnable {
        String UDP_IP = "255.255.255.255";
        int UDP_PORT = 2390;
        Integer code = 200;
        String message = "EnviralDesignPxlNode";

        @Override
        public void run() {
            DatagramSocket sendSocket = null;
            try {
                sendSocket = new DatagramSocket(2391);
                sendSocket.setBroadcast(true);
                sendSocket.setSoTimeout(3000);
                InetAddress serverAddress = InetAddress.getByName(UDP_IP);

                byte[] bufferOne = message.getBytes();
                byte[] bufferTwo = new byte[1];
                bufferTwo[0] = code.byteValue();
                byte[] completeBuffer = new byte[bufferOne.length + 1];

                System.arraycopy(bufferOne, 0, completeBuffer, 0, bufferOne.length);

                System.arraycopy(bufferTwo, 0, completeBuffer, bufferOne.length, 1);

                DatagramPacket sendingPacket = new DatagramPacket(completeBuffer, completeBuffer.length, serverAddress, UDP_PORT);

                byte buf[] = new byte[1024];

                DatagramPacket receivingPacket = new DatagramPacket(buf, buf.length);

                sendSocket.send(sendingPacket);
                try {
                    while(true){
                        sendSocket.receive(receivingPacket);

                        byte[] array = receivingPacket.getData();
                        int length = 0;
                        for(int j = 0; j <= array.length; j++){
                            if(array[j] == 0){
                                length = j;
                                break;
                            }
                        }
                        array = Arrays.copyOfRange(array,21, length );
                        String deviceName = new String(array);
                        deviceName = deviceName.replaceAll("[^a-zA-Z_-]", "");

                        final DeviceEntity deviceEntity = new DeviceEntity( deviceName, receivingPacket.getAddress().toString().substring(1));
                        mMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScannedDevicesList.add(deviceEntity);
                            }
                        });
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                Log.i("Hello World", message);

            } catch (IOException e){
                e.printStackTrace();
            }
            sendSocket.close();
        }
    }

    public class ScanForDevices extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            scanViewModel.deleteAllOnlineDevices();
            new PacketSenderAndReceiver().run();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mScannedDevicesList.clear();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            scanViewModel.insertAllDevices(mScannedDevicesList);
            scanViewModel.saveOnlineDevices(changeToOnlineDevices(mScannedDevicesList));
            if(mScannedDevicesList.isEmpty()){
                mNoDevicesTextView.setText(getString(R.string.text_view_no_devices_found));
                mNoDevicesTextView.setVisibility(View.VISIBLE);
            }
            else{
                mNoDevicesTextView.setVisibility(View.GONE);
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        private List<OnlineDeviceEntity> changeToOnlineDevices(List<DeviceEntity> deviceEntities){
            List<OnlineDeviceEntity> onlineDevices = new ArrayList<>();
            for(DeviceEntity deviceEntity: deviceEntities){
                onlineDevices.add(new OnlineDeviceEntity(deviceEntity.getIpAddress()));
            }
            return onlineDevices;
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
            final DeviceEntity device = getDeviceConnectedToWifi(subIp, i);
            if(device != null){
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mScannedDevicesList.add(device);
                    }
                });
            }
        }
    }

    private class SaveGroupTask extends AsyncTask<GroupEntity, Void, Void>{

        private final ArrayList<String> deviceIps;

        public SaveGroupTask(ArrayList<String> deviceIps) {
            this.deviceIps = deviceIps;
        }

        @Override
        protected Void doInBackground(GroupEntity... groupEntities) {
            Long groupId = scanViewModel.saveGroup(groupEntities[0]);
            List<GroupDetailsEntity> groupDetailsEntities = new ArrayList<>();
            for(String deviceIp : deviceIps){
                groupDetailsEntities.add(new GroupDetailsEntity(groupId.intValue(), deviceIp));
            }
            scanViewModel.saveAllGroupDetails(groupDetailsEntities);
            return null;
        }
    }

}
