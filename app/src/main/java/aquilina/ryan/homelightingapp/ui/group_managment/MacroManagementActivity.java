/*
 * Created by Ryan Aquilina on 11/2/17 11:39 AM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 11/2/17 11:39 AM
 */

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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllMacros;
import aquilina.ryan.homelightingapp.model.Macro;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;

public class MacroManagementActivity extends MainActivity{

    private Menu mMenu;
    private LinearLayout mHintTextView;
    private RecyclerView mGroupsRecyclerView;
    private ArrayList<Macro> mMacros;
    private ArrayList<Integer> mSelectedMacros;
    private GroupsAdapter mAdapter;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macros);

        // Set views
        mGroupsRecyclerView = findViewById(R.id.groups_recycler_list);
        mHintTextView = findViewById(R.id.linear_layout_hint);
        mTitleTextView.setText(R.string.macros_title);

        // Set view's data
        mMacros = new ArrayList<>();
        mSelectedMacros = new ArrayList<>();
        mAdapter = new GroupsAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mGroupsRecyclerView.setLayoutManager(layoutManager);
        mGroupsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMacros = loadMacros();
        if(mMacros.isEmpty()){
            mHintTextView.setVisibility(View.VISIBLE);
        } else {
            mHintTextView.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();

        mNavigationView.setCheckedItem(R.id.nav_group_macros);
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
                deleteCheckedGroups();
                mAdapter.setDeleteMode(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Delete checked groups.
     */
    private void deleteCheckedGroups(){
        ArrayList<Macro> macros;
        macros = loadMacros();

        if(macros != null){
            for(Integer i: mSelectedMacros){
                for(Macro macro: macros){
                    if(macro.getId() == i){
                        macros.remove(macro);
                        break;
                    }
                }
            }
        }

        mMacros = macros;
        if(mMacros != null){
            if(mMacros.isEmpty()){
                mHintTextView.setVisibility(View.VISIBLE);
            } else {
                mHintTextView.setVisibility(View.GONE);
            }
        } else {
            mHintTextView.setVisibility(View.VISIBLE);
        }

        saveMacros(macros);
    }

    /**
     * Once the checked macros are deleted, save the changes.
     */
    private void saveMacros(ArrayList<Macro> macros){
        mPrefs = getSharedPreferences(DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        AllMacros allMacros = new AllMacros();
        Gson gson = new Gson();

        allMacros.setMacros(macros);

        String json = gson.toJson(allMacros);
        prefsEditor.putString(GROUP_OF_MACROS, json);
        prefsEditor.apply();
        mMacros = macros;
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
     * Load macros from SharedPreferences.
     */
    private ArrayList<Macro> loadMacros(){
        mPrefs = getSharedPreferences(DEVICES_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(GROUP_OF_MACROS, null);

        if(json == null){
            return new ArrayList<>();
        } else {
            return gson.fromJson(json, AllMacros.class).getMacros();
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
                            for(int i = 0; i < mSelectedMacros.size(); i++){
                                if(mSelectedMacros.get(i) == view.getTag()){
                                    mSelectedMacros.remove(i);
                                }
                            }
                        }
                        else{
                            cb.setChecked(true);
                            mSelectedMacros.add((Integer) view.getTag());
                        }
                    }
                }

                @Override
                public void onCardViewLongClick(View view) {
                    setDeleteMode(true);
                    ((CheckBox)view.findViewById(R.id.item_checkbox)).setChecked(true);
                    mSelectedMacros.add((Integer) view.getTag());
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Macro macro = mMacros.get(position);

            holder.cardView.setTag(macro.getId());
            holder.textView.setText(macro.getName());
            holder.textView.setTypeface(mTextTypeFace);
            holder.subTextView.setText(getGroupItemsSubString(macro));
            if(isDeleteMode){
                holder.checkBox.setVisibility(View.VISIBLE);
            } else{
                holder.checkBox.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if(mMacros == null){
                return 0;
            }
            return mMacros.size();
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
         * Create and return the preset names in a Macro.
         */
        private String getGroupItemsSubString(Macro macro){
            String subText;
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < macro.getPresetList().size(); i++){
                String presetName = macro.getPresetList().get(i).getPresetName();
                if(presetName != null){
                    builder.append(presetName);
                    if(i != macro.getPresetList().size() - 1){
                        builder.append(", ");
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
