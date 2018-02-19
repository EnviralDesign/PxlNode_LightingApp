/*
 * Created by Ryan Aquilina on 10/18/17 4:44 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/3/17 11:01 AM
 */

package aquilina.ryan.homelightingapp.ui.group_managment;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Common;

public class GroupManagementActivity extends MainActivity {

    private ArrayList<DevicesGroup> mGroups;
    private ArrayList<Integer> mToDeleteGroups;
    private HashMap<String, Device> mDevicesSparseArray;
    private Menu mMenu;
    private LinearLayout mHintTextView;
    private RecyclerView mGroupsRecyclerView;
    private GroupsAdapter mAdapter;
    private Common common;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        common = new Common();

        // Set views
        mGroupsRecyclerView = findViewById(R.id.groups_recycler_list);
        mHintTextView = findViewById(R.id.text_view_hint);
        mTitleTextView.setText(R.string.group_title);

        // Set view's data
        mGroups = new ArrayList<>();
        mToDeleteGroups = new ArrayList<>();
        mAdapter = new GroupsAdapter();
        mDevicesSparseArray = new HashMap<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDevicesSparseArray = common.loadDevices(this);

        mGroups = common.loadGroups(this);
        if(mGroups != null){
            if(mGroups.isEmpty()){
                mHintTextView.setVisibility(View.VISIBLE);
            } else {
                mHintTextView.setVisibility(View.GONE);
            }
        } else {
            mHintTextView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();

        mNavigationView.setCheckedItem(R.id.nav_group_node_groups);
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
                mGroups = common.deleteGroups(mToDeleteGroups, this);
                mAdapter.notifyDataSetChanged();
                mAdapter.setDeleteMode(false);
                if(mGroups != null){
                    if(mGroups.isEmpty()){
                        mHintTextView.setVisibility(View.VISIBLE);
                    } else {
                        mHintTextView.setVisibility(View.GONE);
                    }
                } else {
                    mHintTextView.setVisibility(View.VISIBLE);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                            for(int i = 0; i < mToDeleteGroups.size(); i++){
                                if(mToDeleteGroups.get(i) == view.getTag()){
                                    mToDeleteGroups.remove(i);
                                }
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mToDeleteGroups.add((Integer) view.getTag());
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mToDeleteGroups.add((Integer) view.getTag());
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DevicesGroup group = mGroups.get(position);

            holder.cardView.setTag(group.getId());
            holder.textView.setText(group.getName());
            holder.textView.setTypeface(mTextTypeFace);
            holder.subTextView.setText(getGroupItemsSubString(group));
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

        /**
         * Create and return the ip addresses in the group.
         */
        private String getGroupItemsSubString(DevicesGroup group){
            String subText;
            StringBuilder builder = new StringBuilder();

            for(int i = 0; i < group.getDeviceIPArrayList().size(); i++){
                String deviceIP = group.getDeviceIPArrayList().get(i);
                if(deviceIP != null){
                    String deviceName = mDevicesSparseArray.get(deviceIP).getName();
                    if(deviceName != null){
                        builder.append(deviceName);
                        if(i != group.getDeviceIPArrayList().size() - 1){
                            builder.append(", ");
                        }
                    }
                }
            }

            subText = builder.toString();
            if(subText.length() > 40){
                subText = subText.substring(0, 40);
                subText += ".....";
            }

            return subText;
        }
    }
}
