package frost.com.homelighting.ui.presets;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import frost.com.homelighting.HomeLightingApplication;
import frost.com.homelighting.MainActivity;
import frost.com.homelighting.R;
import frost.com.homelighting.db.entity.PresetEntity;
import frost.com.homelighting.viewmodel.PresetViewModel;

public class PresetFragment extends Fragment{

    private Menu mMenu;
    private LinearLayout mHintTextView;
    private RecyclerView mGroupsRecyclerView;
    private List<PresetEntity> mPresets;
    private List<Integer> mSelectedPresets;
    private GroupsAdapter mAdapter;
    private MainActivity mainActivity;
    private SparseArray<String> mPresetsSubString;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    PresetViewModel presetViewModel;

    public static PresetFragment newInstance() {
        return new PresetFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mainActivity = (MainActivity) getActivity();

        mPresetsSubString = new SparseArray<>();

        ((HomeLightingApplication) mainActivity.getApplication())
                .getApplicationComponent()
                .inject(this);

        mAdapter = new GroupsAdapter();

        presetViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(PresetViewModel.class);

        presetViewModel.getPresets().observe(this, new Observer<List<PresetEntity>>() {
            @Override
            public void onChanged(@Nullable List<PresetEntity> presetEntities) {
                setPresets(presetEntities);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setTitle(R.string.presets_title);
        mainActivity.setOnBackClickListener(new MainActivity.OnBackClickListener() {
            @Override
            public boolean onBackClick() {
                if(mAdapter.isDeleteMode()){
                    mAdapter.setDeleteMode(false);
                    return true;
                }
               return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presets_managment, container, false);

        // Set views
        mGroupsRecyclerView = view.findViewById(R.id.groups_recycler_list);
        mHintTextView = view.findViewById(R.id.linear_layout_hint);

        // Set view's data
        mPresets = new ArrayList<>();
        mSelectedPresets = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_mode, menu);
        mMenu = menu;
        enableDeleteMenuItem(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mAdapter.setDeleteMode(false);
                return true;
            case R.id.delete_button:
                deleteCheckedGroups();
                mAdapter.setDeleteMode(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setPresets(List<PresetEntity> mPresets) {
        this.mPresets = mPresets;

        new LoadPresetStringDetailsTask().execute(mPresetsSubString);

        if(mPresets.isEmpty()){
            mHintTextView.setVisibility(View.VISIBLE);
        } else {
            mHintTextView.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     *  Delete checked groups.
     */
    private void deleteCheckedGroups(){
        new DeletePresetsTask().execute(new ArrayList<>(mSelectedPresets));
    }

    /**
     * Enables/Disables the delete menu item.
     */
    private void enableDeleteMenuItem(boolean enable){
        if(enable){
            mMenu.findItem(R.id.delete_button).setVisible(true);
        }
        else{
            mMenu.findItem(R.id.delete_button).setVisible(false);
        }
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

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView textView;
        TextView subTextView;
        CardView cardView;
        CheckBox checkBox;
        ViewHolder.ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolder.ViewHolderClickListener mListener) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.group_name);
            this.subTextView = itemView.findViewById(R.id.associated_devices);
            this.cardView = itemView.findViewById(R.id.item_card_view);
            this.checkBox = itemView.findViewById(R.id.item_checkbox);
            this.mListener = mListener;
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardViewClick(view);
        }

        @Override
        public boolean onLongClick(View view) {
            mListener.onCardViewLongClick(view);
            return true;
        }

        public interface ViewHolderClickListener{
            void onCardViewClick(View view);
            void onCardViewLongClick(View view);
        }
    }

    private class GroupsAdapter extends RecyclerView.Adapter<ViewHolder>{

        private boolean isDeleteMode = false;

        public boolean isDeleteMode() {
            return isDeleteMode;
        }

        public void setDeleteMode(boolean deleteMode) {
            isDeleteMode = deleteMode;
            mAdapter.notifyDataSetChanged();
            if(deleteMode){
                enableDeleteMenuItem(true);
            }
            else{
                enableDeleteMenuItem(false);
                mSelectedPresets.clear();
                mAdapter = new GroupsAdapter();
                mGroupsRecyclerView.setAdapter(mAdapter);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new ViewHolder(itemView, new ViewHolder.ViewHolderClickListener() {
                @Override
                public void onCardViewClick(View view) {
                    if(isDeleteMode){
                        CheckBox cb = view.findViewById(R.id.item_checkbox);
                        if(cb.isChecked()){
                            cb.setChecked(false);
                            for(int i = 0; i < mSelectedPresets.size(); i++){
                                if(mSelectedPresets.get(i) == view.getTag()){
                                    mSelectedPresets.remove(i);
                                }
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mSelectedPresets.add((Integer) view.getTag());
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mSelectedPresets.add((Integer) view.getTag());
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            PresetEntity preset = mPresets.get(position);

            holder.cardView.setTag(preset.getId());
            holder.textView.setText(preset.getName());
            holder.subTextView.setText(mPresetsSubString.get(preset.getId()));
            if(isDeleteMode){
                holder.checkBox.setVisibility(View.VISIBLE);
            } else{
                holder.checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if(mPresets == null){
                return 0;
            }
            return mPresets.size();
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

    private class DeletePresetsTask extends AsyncTask<List<Integer>, Void, Void>{
        @Override
        protected Void doInBackground(List<Integer>... lists) {
            for(int presetId: lists[0]){
                presetViewModel.removePreset(presetId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSelectedPresets.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private class LoadPresetStringDetailsTask extends AsyncTask<SparseArray<String>, Void, Void>{

        @Override
        protected Void doInBackground(SparseArray<String>... sparseArrays) {
            for(PresetEntity presetEntity: mPresets){
                String groupName = presetViewModel.loadPresetGroupName(presetEntity.getId());
                if(groupName == null){
                    List<String> names = presetViewModel.loadPresetDeviceNames(presetEntity.getId());
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
