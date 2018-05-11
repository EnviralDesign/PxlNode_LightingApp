package frost.com.homelighting.ui.groups;

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
import frost.com.homelighting.db.entity.GroupEntity;
import frost.com.homelighting.util.Constants;
import frost.com.homelighting.viewmodel.GroupViewModel;

public class GroupFragment extends Fragment implements Constants{

    private List<GroupEntity> mGroups;
    private List<Integer> mSelectedGroups;
    private SparseArray<String> mGroupSubString;
    private Menu mMenu;
    private LinearLayout mHintTextView;
    private RecyclerView mGroupsRecyclerView;
    private MainActivity mainActivity;
    private GroupsAdapter mAdapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    GroupViewModel groupViewModel;

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mainActivity = (MainActivity) getActivity();

        mGroupSubString = new SparseArray<>();

        ((HomeLightingApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mAdapter = new GroupsAdapter();

        groupViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(GroupViewModel.class);

        groupViewModel.getGroups().observe(this, new Observer<List<GroupEntity>>() {
            @Override
            public void onChanged(@Nullable List<GroupEntity> groupEntities) {
                setGroupData(groupEntities);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity.setTitle(getString(R.string.group_title));
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
        View view = inflater.inflate(R.layout.fragment_group_managment, container, false);

        mGroupsRecyclerView = view.findViewById(R.id.groups_recycler_list);
        mHintTextView = view.findViewById(R.id.linear_layout_hint);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mainActivity);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);
        mSelectedGroups = new ArrayList<>();
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mAdapter.setDeleteMode(false);
                return true;
            case R.id.delete_button:
                new DeleteGroupsTask().execute(new ArrayList<>(mSelectedGroups));
                mAdapter.setDeleteMode(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit_mode, menu);
        mMenu = menu;
        enableDeleteMenuItem(false);
    }

    private void setGroupData(List<GroupEntity> groupEntities) {
        this.mGroups = groupEntities;

        new LoadGroupStringDetailsTask().execute(mGroupSubString);

        if(mGroups.isEmpty()){
            mHintTextView.setVisibility(View.VISIBLE);
        } else {
            mHintTextView.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
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
                            for(int i = 0; i < mSelectedGroups.size(); i++){
                                if(mSelectedGroups.get(i) == view.getTag()){
                                    mSelectedGroups.remove(i);
                                }
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mSelectedGroups.add((int) view.getTag());
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mSelectedGroups.add((Integer) view.getTag());
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            GroupEntity group = mGroups.get(position);

            holder.cardView.setTag(group.getId());
            holder.textView.setText(group.getName());
            holder.subTextView.setText(mGroupSubString.get(group.getId()));
            if(isDeleteMode){
                holder.checkBox.setVisibility(View.VISIBLE);
            } else{
                holder.checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if(mGroups == null){
                return 0;
            }
            return mGroups.size();
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

    private class DeleteGroupsTask extends AsyncTask<List<Integer>, Void, Void>{
        @Override
        protected Void doInBackground(List<Integer>... lists) {
            for(Integer groupId: lists[0]){
                groupViewModel.deleteGroup(groupId);
            }

            // Delete any presets that has this group only
            List<Integer> presetIds = groupViewModel.loadAllPresetsIds();
            for(int presetId: presetIds){
                List<Integer> group = groupViewModel.loadAllGroupIdsForPreset(presetId);
                if(group.isEmpty()){
                    groupViewModel.deletePreset(presetId);
                }
            }

            // Delete any macros that has this preset only
            List<Integer> macroIds = groupViewModel.loadAllMacroIds();
            for(int macroId: macroIds){
                List<Integer> presets = groupViewModel.loadAllPresetIdsForMacro(macroId);
                if(presets.isEmpty()){
                    groupViewModel.deleteMacroEntity(macroId);
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

    private class LoadGroupStringDetailsTask extends AsyncTask<SparseArray<String>, Void, Void>{

        @Override
        protected Void doInBackground(SparseArray<String>... sparseArrays) {
            for(GroupEntity groupEntity: mGroups){
                List<String> names = groupViewModel.loadGroupDeviceNames(groupEntity.getId());
                sparseArrays[0].put(groupEntity.getId(), getItemSubString(names));
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
