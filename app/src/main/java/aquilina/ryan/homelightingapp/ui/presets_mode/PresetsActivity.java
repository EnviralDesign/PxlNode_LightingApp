package aquilina.ryan.homelightingapp.ui.presets_mode;

import com.google.gson.Gson;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.AllPresets;
import aquilina.ryan.homelightingapp.model.Preset;
import aquilina.ryan.homelightingapp.ui.main_activity.MainActivity;
import aquilina.ryan.homelightingapp.utils.Constants;

/**
 * Created by SterlingRyan on 9/5/2017.
 */

public class PresetsActivity extends MainActivity {
    private RecyclerView mPresetsRecyclerView;
    private ArrayList<Preset> mPresets;
    private PresetAdapter mAdapter;

    private SharedPreferences mPrefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        mPresets = new ArrayList<>();
        mPresetsRecyclerView = (RecyclerView) findViewById(R.id.presets_recycler_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mPresetsRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new PresetAdapter();
        mPresetsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadPresets();
    }

    /**
     * Load presets from SharedPreferences
     */
    private void loadPresets(){
        mPrefs = getSharedPreferences(Constants.PRESETS_SHARED_PREFERENCES, MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(Constants.GROUP_OF_PRESETS, null);

        AllPresets allPresets = (AllPresets) gson.fromJson(json, AllPresets.class);
        if(allPresets != null){
            for (int i = 0; i < allPresets.getAllPresets().size(); i++){
                mPresets.add(allPresets.getAllPresets().get(i));
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView textView;
        Switch aSwitch;
        CardView cardView;
        ViewHolderClickListener mListener;

        public ViewHolder(View itemView, ViewHolderClickListener mListener) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.preset_name);
            this.aSwitch = (Switch) itemView.findViewById(R.id.preset_switch);
            this.cardView = (CardView) itemView.findViewById(R.id.item_card_view);
            this.mListener = mListener;
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onCardViewClick(view);
        }

        public interface ViewHolderClickListener{
            void onCardViewClick(View view);
        }
    }

    private class PresetAdapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset, parent, false);
            return new ViewHolder(itemView, new ViewHolder.ViewHolderClickListener() {
                @Override
                public void onCardViewClick(View view) {
                    Switch sw = (Switch) view.findViewById(R.id.preset_switch);
                    if(sw.isChecked()){
                        sw.setChecked(false);
                    }
                    else{
                        sw.setChecked(true);
                    }
                    //TODO switch off switches and for each device in the preset activate the effect
                }
            });
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Preset preset = mPresets.get(position);

            holder.textView.setText(preset.getPresetName());
            holder.textView.setTypeface(mTextTypeFace);
        }

        @Override
        public int getItemCount() {
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
}
