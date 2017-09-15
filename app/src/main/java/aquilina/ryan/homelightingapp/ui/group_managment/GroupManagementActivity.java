package aquilina.ryan.homelightingapp.ui.group_managment;

import com.google.gson.Gson;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import java.security.acl.Group;
import java.util.ArrayList;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllGroups;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Constants;

/**
 * Created by SterlingRyan on 9/15/2017.
 */

public class GroupManagementActivity extends MainActivity {
    private RecyclerView mGroupsRecyclerView;
    private ArrayList<DevicesGroup> mGroups;
    private ArrayList<String> mToDeleteGroups;
    private GroupsAdapter mAdapter;
    private Menu mMenu;

    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        mGroups = new ArrayList<>();
        mToDeleteGroups = new ArrayList<>();
        mGroupsRecyclerView = (RecyclerView) findViewById(R.id.presets_recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new GroupsAdapter();
        mGroupsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGroups = loadGroups();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(mAdapter.isDeleteMode()){
            mAdapter.setDeleteMode(false);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_mode, menu);
        mMenu = menu;
        enableDeleteMenuItem(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mAdapter.setDeleteMode(false);
                return true;
            case R.id.delete_button:
                deleteCheckedItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCheckedItems(){
        ArrayList<DevicesGroup> groups;
        groups = loadGroups();

        for(String string: mToDeleteGroups){
            for(DevicesGroup group: groups){
                if(group.getName().equals(string)){
                    groups.remove(group);
                    break;
                }
            }
        }

        saveGroupList(groups);
    }

    private void saveGroupList(ArrayList<DevicesGroup> groups){
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        AllGroups allGroups = new AllGroups(groups);

        String json = gson.toJson(allGroups);
        prefsEditor.putString(Constants.GROUP_OF_DEVICES_GROUPS, json);
        prefsEditor.apply();
        mGroups = groups;
        mAdapter.notifyDataSetChanged();
    }

    private void enableDeleteMenuItem(boolean enable){
        if(enable){
            mMenu.findItem(R.id.delete_button).setVisible(true);
        }
        else{
            mMenu.findItem(R.id.delete_button).setVisible(false);
        }
    }

    /**
     * Load presets from SharedPreferences
     */
    private ArrayList<DevicesGroup> loadGroups(){
        mPrefs = getSharedPreferences(Constants.DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_DEVICES_GROUPS, null);

        AllGroups allGroups = (AllGroups) gson.fromJson(json, AllGroups.class);
        ArrayList<DevicesGroup> groups = new ArrayList<>();

        if(allGroups != null){
            for (int i = 0; i < allGroups.getGroups().size(); i++){
                groups.add(allGroups.getGroups().get(i));
            }
        }
        return groups;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        TextView textView;
        CardView cardView;
        CheckBox checkBox;
        ViewHolder.ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolder.ViewHolderClickListener mListener) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.preset_name);
            this.cardView = (CardView) itemView.findViewById(R.id.item_card_view);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.item_checkbox);
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
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
            return new ViewHolder(itemView, new ViewHolder.ViewHolderClickListener() {
                @Override
                public void onCardViewClick(View view) {
                    if(isDeleteMode){
                        CheckBox cb = (CheckBox) view.findViewById(R.id.item_checkbox);
                        if(cb.isChecked()){
                            cb.setChecked(false);
                            for(int i = 0; i < mToDeleteGroups.size(); i++){
                                if(mToDeleteGroups.get(i).equals((String) view.getTag())){
                                    mToDeleteGroups.remove(i);
                                }
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mToDeleteGroups.add(((TextView)view.findViewById(R.id.preset_name)).getText().toString());
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mToDeleteGroups.add((String) view.getTag());
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DevicesGroup group = mGroups.get(position);

            holder.cardView.setTag(group.getName());
            holder.textView.setText(group.getName());
            holder.textView.setTypeface(mTextTypeFace);
            if(isDeleteMode){
                holder.checkBox.setVisibility(View.VISIBLE);
            } else{
                holder.checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
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
}
