package frost.com.homelighting.ui.lighting;

import android.app.DialogFragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import frost.com.homelighting.HomeLightingApplication;
import frost.com.homelighting.MainActivity;
import frost.com.homelighting.R;
import frost.com.homelighting.db.entity.DeviceEntity;
import frost.com.homelighting.db.entity.MacroDetailsEntity;
import frost.com.homelighting.db.entity.MacroEntity;
import frost.com.homelighting.db.entity.PresetEntity;
import frost.com.homelighting.util.Constants;
import frost.com.homelighting.viewmodel.LightingViewModel;

public class LightingFragment extends Fragment implements Constants {

    private TextView mHintTextView;
    private RecyclerView mRecyclerView;
    private Menu mMenu;

    private List<PresetEntity> mPresets;
    private List<MacroEntity> mMacros;
    private List<Integer> mSelectedPresets, mSelectedMacros;
    private boolean isAddMacroAvailable;
    private SparseArray<String> mMacrosSubString, mPresetsSubString;

    private MainActivity mainActivity;
    private PresetAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    LightingViewModel lightingViewModel;

    public static LightingFragment newInstance() {
        return new LightingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mainActivity = (MainActivity) getActivity();

        mMacrosSubString = new SparseArray<>();
        mPresetsSubString = new SparseArray<>();

        ((HomeLightingApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        lightingViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(LightingViewModel.class);

        lightingViewModel.getMacros().observe(this, new Observer<List<MacroEntity>>() {
            @Override
            public void onChanged(@Nullable List<MacroEntity> macroEntities) {
                setMacros(macroEntities);
            }
        });

        lightingViewModel.getPresets().observe(this, new Observer<List<PresetEntity>>() {
            @Override
            public void onChanged(@Nullable List<PresetEntity> presetEntities) {
                setPresets(presetEntities);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.invalidateOptionsMenu();
        mainActivity.setTitle(R.string.lighting_mode_title);
        mainActivity.setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                if(mAdapter.isSelectionMode()){
                    mAdapter.setSelectionMode(false);
                    return true;
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_lighting, container, false);

        // Set views.
        mRecyclerView = view.findViewById(R.id.presets_recycler_list);
        mHintTextView = view.findViewById(R.id.linear_layout_hint);

        // Set view's data/design
        mPresets = new ArrayList<>();
        mSelectedPresets = new ArrayList<>();
        mMacros = new ArrayList<>();
        mSelectedMacros = new ArrayList<>();
        mAdapter = new PresetAdapter();
        isAddMacroAvailable = false;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_refresh_lights, menu);
        mMenu = menu;
        enableSelectionMenuItem(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mAdapter.setSelectionMode(false);
                return true;
            case R.id.add_macro_button:
                if(isAddMacroAvailable){
                    createNewMacro();
                }
                return true;
            case R.id.delete_button:
                deleteCheckedItems();
                return true;
            case R.id.refresh:
                switchOffLights();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPresets(List<PresetEntity> mPresets) {
        this.mPresets = mPresets;

        new LoadPresetStringDetailsTask().execute(mPresetsSubString);

        // Load presets and macros
        if(!mPresets.isEmpty() || !mMacros.isEmpty()){
            mHintTextView.setVisibility(View.GONE);
        } else {
            mHintTextView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void setMacros(List<MacroEntity> mMacros) {
        this.mMacros = mMacros;

        new LoadMacroStringDetailsTask().execute(mMacrosSubString);

        // Load presets and macros
        if(!mPresets.isEmpty() || !mMacros.isEmpty()){
            mHintTextView.setVisibility(View.GONE);
        } else {
            mHintTextView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Switch off a list of online devices.
     */
    private void switchOffLights(){
        List<DeviceEntity> onlineDevices = mainActivity.mOnlineDevices;

        if(onlineDevices != null){
            ExecutorService executorService = Executors.newFixedThreadPool(onlineDevices.size());
            for(DeviceEntity device : onlineDevices){
                Runnable worker = new PostRequest("blank", device.getIpAddress());
                executorService.execute(worker);
            }
            executorService.shutdown();
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
     * Deletes all items in mSelected Items.
     */
    private void deleteCheckedItems(){

        // Delete macros from memory
        if(!mSelectedMacros.isEmpty()){
            new DeleteMacroTask().execute(new ArrayList<>(mSelectedMacros));
        }

        //Delete Presets from memory
        if(!mSelectedPresets.isEmpty()){
            new DeletePresetTask().execute(new ArrayList<>(mSelectedPresets));
        }

        mAdapter.setSelectionMode(false);
        mAdapter.notifyDataSetChanged();
        if(mMacros.isEmpty() && mPresets.isEmpty()){
            mHintTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Enable or disable selection menu in app bar.
     */
    private void enableSelectionMenuItem(boolean enable){
        if(enable){
            mMenu.findItem(R.id.delete_button).setVisible(true);
            mMenu.findItem(R.id.add_macro_button).setVisible(true);
        }
        else{
            mMenu.findItem(R.id.delete_button).setVisible(false);
            mMenu.findItem(R.id.add_macro_button).setVisible(false);
        }
    }

    /**
     * Refresh the recycler view to un-check all checked items.
     */
    private void removeCheckedItems(){
        mSelectedPresets.clear();
        mSelectedMacros.clear();
        mAdapter = new PresetAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Create a new Macro
     */
    private void createNewMacro(){
        DialogFragment dialogFragment = AddMacroDialog.newInstance();
        dialogFragment.show(mainActivity.getFragmentManager(), "AddMacroDialog");
    }

    /**
     * Save presets as macro.
     */
    public void saveMacro(String macroName){
        MacroEntity macroEntity = new MacroEntity(macroName);
        ArrayList<Integer> presetIds = new ArrayList<>(mSelectedPresets);
        new SaveMacroTask(presetIds).execute(macroEntity);
        mAdapter.setSelectionMode(false);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Create and return the sub string.
     */
    private String getItemSubString(List<String> subStrings){
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < subStrings.size(); i++){
            stringBuffer.append(subStrings.get(i));
            if(i != subStrings.size() - 1){
                stringBuffer.append(", ");
            }
        }

        if(stringBuffer.length() > 35){
            stringBuffer = new StringBuffer(stringBuffer.substring(0, 35));
            stringBuffer.append(".....");
        }

        return stringBuffer.toString();
    }

    private static class TitleViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;

        private TitleViewHolder(View itemView) {
            super(itemView);
            this.titleTextView = itemView.findViewById(R.id.header_title);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener{
        TextView nameTextView;
        TextView groupTextView;
        ImageView aSwitch;
        LinearLayout cardView;
        CheckBox checkBox;
        ViewHolderClickListener mListener;

        private ItemViewHolder(View itemView, ViewHolderClickListener mListener) {
            super(itemView);
            this.nameTextView = itemView.findViewById(R.id.preset_name);
            this.groupTextView = itemView.findViewById(R.id.associated_group);
            this.aSwitch = itemView.findViewById(R.id.preset_switch);
            this.cardView = itemView.findViewById(R.id.item_card_view);
            this.checkBox = itemView.findViewById(R.id.item_checkbox);
            this.mListener = mListener;
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
            aSwitch.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardViewClick(view);
        }

        @Override
        public boolean onLongClick(View view) {
            return mListener.onCardViewLongClick(view);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mListener.onCardGestureListener(view, motionEvent);
        }

        private interface ViewHolderClickListener{
            void onCardViewClick(View view);
            boolean onCardViewLongClick(View view);
            boolean onCardGestureListener(View view, MotionEvent motionEvent);
        }
    }

    private class PresetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int MACRO_TITLE = 0;
        private static final int PRESETS_TITLE = 1;
        private static final int MACRO_ITEM = 2;
        private static final int PRESET_ITEM = 3;

        private boolean isSelectionMode = false;

        private ItemViewHolder.ViewHolderClickListener mListener = new ItemViewHolder.ViewHolderClickListener(){

            @Override
            public void onCardViewClick(View view) {
                itemOnClick(view);
            }

            @Override
            public boolean onCardGestureListener(View view, MotionEvent motionEvent) {
                return itemOnGestureListener(view, motionEvent);
            }

            @Override
            public boolean onCardViewLongClick(View view) {
                if(!isSelectionMode){
                    setSelectionMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    String groupType = (String) (view.findViewById(R.id.preset_switch)).getTag(R.id.groupType);
                    if(groupType.equals(MACRO)){
                        mSelectedMacros.add((Integer)(view.findViewById(R.id.preset_switch)).getTag(R.id.ID));
                    } else{
                        mSelectedPresets.add((Integer)(view.findViewById(R.id.preset_switch)).getTag(R.id.ID));
                    }

                    // Check if creating a macro is available
                    checkNewMacroAvailability();
                }
                return true;
            }
        };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == MACRO_TITLE || viewType == PRESETS_TITLE){
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lighting_mode_header, parent, false);
                return new TitleViewHolder(itemView);
            } else {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lighting, parent, false);
                return new ItemViewHolder(itemView, mListener);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            // Create a list of two separate types, with
            // titles separating each type.
            switch (holder.getItemViewType()){
                case MACRO_TITLE:{
                    TitleViewHolder macroTitleViewHolder = (TitleViewHolder) holder;
                    macroTitleViewHolder.titleTextView.setText(getString(R.string.recycler_view_macros_title));
                    break;
                }
                case MACRO_ITEM:{
                    ItemViewHolder macroItemViewHolder = (ItemViewHolder) holder;
                    MacroEntity macro = mMacros.get(position - 1);

                    // Set the tags to identify the macro.
                    macroItemViewHolder.cardView.setTag(R.id.ID, macro.getId());
                    macroItemViewHolder.cardView.setTag(R.id.groupType, MACRO);
                    macroItemViewHolder.aSwitch.setTag(R.id.ID, macro.getId());
                    macroItemViewHolder.aSwitch.setTag(R.id.groupType, MACRO);
                    macroItemViewHolder.nameTextView.setText(macro.getName());
                    String substring = mMacrosSubString.get(macro.getId());
                    macroItemViewHolder.groupTextView.setText(substring);
                    if(isSelectionMode){
                        macroItemViewHolder.checkBox.setVisibility(View.VISIBLE);
                        macroItemViewHolder.aSwitch.setVisibility(View.INVISIBLE);
                    } else{
                        macroItemViewHolder.checkBox.setVisibility(View.GONE);
                        macroItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    }
                    macroItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    break;
                }
                case PRESETS_TITLE:{
                    TitleViewHolder presetTitleViewHolder = (TitleViewHolder) holder;
                    presetTitleViewHolder.titleTextView.setText(getString(R.string.recycler_view_presets_title));
                    break;
                }
                case PRESET_ITEM:{
                    ItemViewHolder presetItemViewHolder = (ItemViewHolder) holder;
                    PresetEntity preset;
                    if(mMacros.isEmpty()){
                        preset = mPresets.get(position - 1);
                    } else {
                        preset = mPresets.get(position - (mMacros.size() + 2));
                    }

                    // Set the tags to identify the preset.
                    presetItemViewHolder.cardView.setTag(R.id.ID, preset.getId());
                    presetItemViewHolder.cardView.setTag(R.id.groupType, PRESET);
                    presetItemViewHolder.aSwitch.setTag(R.id.ID, preset.getId());
                    presetItemViewHolder.aSwitch.setTag(R.id.groupType, PRESET);
                    presetItemViewHolder.nameTextView.setText(preset.getName());
                    String substring = mPresetsSubString.get(preset.getId());
                    presetItemViewHolder.groupTextView.setText(substring);
                    if(isSelectionMode){
                        presetItemViewHolder.checkBox.setVisibility(View.VISIBLE);
                        presetItemViewHolder.aSwitch.setVisibility(View.INVISIBLE);
                    } else{
                        presetItemViewHolder.checkBox.setVisibility(View.GONE);
                        presetItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    }
                    presetItemViewHolder.aSwitch.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (mMacros.isEmpty() && mPresets.isEmpty()) {
                return 0;
            }
            else if (mMacros.isEmpty()) {
                return mPresets.size() + 1;
            }
            else if (mPresets.isEmpty()){
                return mMacros.size() + 1;
            }
            else{
                return mMacros.size() + mPresets.size() + 2;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mMacros.isEmpty() && mPresets.isEmpty()) {
                return -1;
            }
            else if (mMacros.isEmpty()) {
                if(position == 0 ){
                    return PRESETS_TITLE;
                }
                else {
                    return PRESET_ITEM;
                }
            }
            else if (mPresets.isEmpty()){
                if(position == 0 ){
                    return MACRO_TITLE;
                }
                else {
                    return MACRO_ITEM;
                }
            }
            else{
                if(position == 0){
                    return MACRO_TITLE;
                }
                else if (position <= mMacros.size()){
                    return MACRO_ITEM;
                }
                else if (position == mMacros.size() + 1){
                    return PRESETS_TITLE;
                }
                else {
                    return PRESET_ITEM;
                }
            }
        }

        private boolean isSelectionMode() {
            return isSelectionMode;
        }

        private void setSelectionMode(boolean selectionMode) {
            isSelectionMode = selectionMode;
            mAdapter.notifyDataSetChanged();
            if(selectionMode){
                enableSelectionMenuItem(true);
            }
            else{
                enableSelectionMenuItem(false);
                removeCheckedItems();
            }
        }

        /**
         * Switch on a preset or a macro
         * @param id is the id number of the Macro/Preset
         * @param groupType is the type of group either Macro or Preset
         */
        private void switchOnDevices(int id, String groupType){
            if(groupType.equals(PRESET)){
                PresetEntity presetClicked = null;
                for(PresetEntity preset : mPresets){
                    if (preset.getId() == id){
                        presetClicked = preset;
                        break;
                    }
                }
                sendPresetCommandToDevices(presetClicked);
            }
            else {
                MacroEntity clickedMacro = null;
                for(MacroEntity macro: mMacros){
                    if(id == macro.getId()){
                        clickedMacro = macro;
                    }
                }

                if(clickedMacro != null){
                    sendMacroCommands(clickedMacro);
                }
            }
        }

        /**
         * Switch on the presets inside the macro.
         * @param macro macro chosen to be switched on.
         */
        private void sendMacroCommands(MacroEntity macro){
            new SendMacroCommandsTask().execute(macro.getId());
        }

        /**
         * Send the preset command to the devices
         * @param presetClicked the preset that has been activated
         */
        private void sendPresetCommandToDevices(PresetEntity presetClicked){
            if(presetClicked != null){
                new SendPresetCommandsTask(presetClicked.getCommand()).execute(presetClicked.getId());
            }
        }

        /**
         * Turn on preset.
         * @param view the turn on preset switch
         * @param motionEvent type of motion event
         * @return event captured or not
         */
        private boolean itemOnGestureListener(View view, MotionEvent motionEvent){
            ImageView imageView = view.findViewById(R.id.preset_switch);
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    imageView.setImageDrawable(mainActivity.getDrawable(R.drawable.ic_play_on));
                    switchOnDevices((int) view.getTag(R.id.ID), (String) view.getTag(R.id.groupType));
                    return true;
                case MotionEvent.ACTION_UP:
                    imageView.setImageDrawable(mainActivity.getDrawable(R.drawable.ic_play_off));
                    return true;
                case MotionEvent.ACTION_MOVE:
                    imageView.setImageDrawable(mainActivity.getDrawable(R.drawable.ic_play_off));
                    return true;
            }
            return false;
        }

        /**
         * Add/remove a preset or macro from the list of selected items.
         * @param view the child view inside the recyclerview.
         */
        private void itemOnClick(View view){
            if(isSelectionMode){
                CheckBox cb = view.findViewById(R.id.item_checkbox);
                if(cb.isChecked()){
                    cb.setChecked(false);
                    if((view.getTag(R.id.groupType)).equals(PRESET)){
                        for(int i = 0; i < mSelectedPresets.size(); i++){
                            if(mSelectedPresets.get(i) == ((int) view.getTag(R.id.ID))){
                                mSelectedPresets.remove(i);
                            }
                        }
                    } else {
                        for(int i = 0; i < mSelectedMacros.size(); i++){
                            if(mSelectedMacros.get(i) == ((int) view.getTag(R.id.ID))){
                                mSelectedMacros.remove(i);
                            }
                        }
                    }
                }
                else{
                    cb.setChecked(true);
                    String type = (String)view.getTag(R.id.groupType);
                    if(type.equals(MACRO)){
                        mSelectedMacros.add((int) view.getTag(R.id.ID));
                    }
                    else{
                        mSelectedPresets.add((int) view.getTag(R.id.ID));
                    }
                }
            }
            checkNewMacroAvailability();
        }

        private void checkNewMacroAvailability(){
            if(mSelectedMacros.isEmpty() && !mSelectedPresets.isEmpty()){
                enableAddMacroMenuButton(true);
            } else {
                enableAddMacroMenuButton(false);
            }
        }

        private void enableAddMacroMenuButton(boolean enable){
            if(enable){
                mMenu.findItem(R.id.add_macro_button).setIcon(R.drawable.ic_add_macro_on);
                isAddMacroAvailable = true;
            } else{
                mMenu.findItem(R.id.add_macro_button).setIcon(R.drawable.ic_add_macro_off);
                isAddMacroAvailable = false;
            }
        }


    }

    private class DeleteMacroTask extends AsyncTask<List<Integer>, Void, Void>{

        @Override
        protected Void doInBackground(List<Integer>... macroIds) {
            for(Integer macroId : macroIds[0]){
                lightingViewModel.deleteMacroEntity(macroId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSelectedMacros.clear();
        }
    }

    private class DeletePresetTask extends AsyncTask<List<Integer>, Void, Void>{

        @Override
        protected Void doInBackground(List<Integer>... lists) {
            for(Integer presetId : lists[0]){
                lightingViewModel.deletePresetEntity(presetId);
            }
            List<Integer> macroIds = lightingViewModel.loadAllMacroIds();
            for(int macroId: macroIds){
                List<Integer> presets = lightingViewModel.loadAllPresetIdsForMacro(macroId);
                if(presets.isEmpty()){
                    lightingViewModel.deleteMacroEntity(macroId);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSelectedPresets.clear();
        }
    }

    private class SaveMacroTask extends AsyncTask<MacroEntity, Void, Void>{

        private final ArrayList<Integer> presetIds;

        public SaveMacroTask(ArrayList<Integer> deviceIps) {
            this.presetIds = deviceIps;
        }

        @Override
        protected Void doInBackground(MacroEntity... macroEntities) {
            Long macroId = lightingViewModel.insertMacroEntity(macroEntities[0]);
            List<MacroDetailsEntity> macroDetailsEntities = new ArrayList<>();
            for(int presetId : presetIds){
                macroDetailsEntities.add(new MacroDetailsEntity(macroId.intValue(), presetId));
            }
            lightingViewModel.insertAllMacroDetailsEntity(macroDetailsEntities);
            return null;
        }
    }

    private class SendMacroCommandsTask extends AsyncTask<Integer, Void, List<DeviceIPAndCommand>>{

        @Override
        protected List<DeviceIPAndCommand> doInBackground(Integer... integers) {
            return lightingViewModel.loadMacroDeviceIdAndCommand(integers[0]);
        }

        @Override
        protected void onPostExecute(List<DeviceIPAndCommand> deviceIPAndCommands) {
            super.onPostExecute(deviceIPAndCommands);
            if(deviceIPAndCommands.size() > 0){
                ExecutorService executorService = Executors.newFixedThreadPool(deviceIPAndCommands.size());
                for(DeviceIPAndCommand deviceAndCommand :deviceIPAndCommands){
                    Runnable worker = new PostRequest(deviceAndCommand.getCommand(), deviceAndCommand.getIp_address());
                    executorService.execute(worker);
                }
                executorService.shutdown();
            }
        }
    }

    private class SendPresetCommandsTask extends AsyncTask<Integer, Void, List<String>>{
        private String command;

        public SendPresetCommandsTask(String command) {
            this.command = command;
        }

        @Override
        protected List<String> doInBackground(Integer... integers) {
            return lightingViewModel.loadPresetDeviceId(integers[0]);
        }

        @Override
        protected void onPostExecute(List<String> devicesIpAddresses) {
            super.onPostExecute(devicesIpAddresses);

            if(!devicesIpAddresses.isEmpty()){
                ExecutorService executorService = Executors.newFixedThreadPool(devicesIpAddresses.size());
                for(String ipAddress: devicesIpAddresses){
                    Runnable worker = new PostRequest(command, ipAddress);
                    executorService.execute(worker);
                }
                executorService.shutdown();
            }
        }
    }

    private class LoadMacroStringDetailsTask extends AsyncTask<SparseArray<String>, Void, Void>{

        @Override
        protected Void doInBackground(SparseArray<String>... sparseArrays) {
            for(MacroEntity macroEntity: mMacros){
                List<String> names = lightingViewModel.loadMacroPresetNames(macroEntity.getId());
                sparseArrays[0].put(macroEntity.getId(), getItemSubString(names));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class LoadPresetStringDetailsTask extends AsyncTask<SparseArray<String>, Void, Void>{

        @Override
        protected Void doInBackground(SparseArray<String>... sparseArrays) {
            for(PresetEntity presetEntity: mPresets){
                String groupName = lightingViewModel.loadPresetGroupName(presetEntity.getId());
                if(groupName == null){
                    List<String> names = lightingViewModel.loadPresetDeviceNames(presetEntity.getId());
                    sparseArrays[0].put(presetEntity.getId(), getItemSubString(names));
                } else{
                    sparseArrays[0].put(presetEntity.getId(), groupName);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }
}
