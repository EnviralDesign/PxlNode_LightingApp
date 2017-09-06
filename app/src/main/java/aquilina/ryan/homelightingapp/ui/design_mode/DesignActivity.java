package aquilina.ryan.homelightingapp.ui.design_mode;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.model.Device;
import aquilina.ryan.homelightingapp.model.DevicesGroup;
import aquilina.ryan.homelightingapp.ui.MainActivity;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by SterlingRyan on 9/5/2017.
 */

public class DesignActivity extends MainActivity {

    private ArrayList<Device> mSingleItemList;
    private ArrayList<DevicesGroup> mGroupedItemList;

    private MaterialSpinner mSpinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        mSingleItemList = new ArrayList<>();
        mGroupedItemList = new ArrayList<>();

        for(int i = 0; i < 6; i++){
            mSingleItemList.add(new Device("HEY", "Single Default"));
        }

        for(int i = 0; i < 6; i++){
            mGroupedItemList.add(new DevicesGroup("Group Default", null));
        }

        // Set up views
        mSpinner = (MaterialSpinner) findViewById(R.id.item_spinner);
        mSpinner.setAdapter(new CustomSpinnerAdapter());
    }

    private class CustomSpinnerAdapter implements SpinnerAdapter{
        @Override
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            if(i == 0){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_section_header, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_section_header_text_view);
                textView.setText(getString(R.string.spinner_group_section_header));
                return view;
            } else if (i > 0 && i < mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else if (i == mGroupedItemList.size()){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                view.setBackgroundColor(ContextCompat.getColor(getBaseContext(),R.color.colorPrimary));
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else if (i == (mGroupedItemList.size() + 1)){
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_section_header, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_section_header_text_view);
                textView.setText(getString(R.string.spinner_fixture_section_header));
                return view;
            } else {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_item, viewGroup, false);
                TextView textView = (TextView) view.findViewById(R.id.spinner_item_text_view);
                textView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getName());
                return view;
            }
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

        }

        @Override
        public int getCount() {
            return mSingleItemList.size() + mGroupedItemList.size() + 2;
        }

        @Override
        public Object getItem(int i) {
            if(i > mGroupedItemList.size()){
                return mSingleItemList.get(i - mGroupedItemList.size());
            }
            return mGroupedItemList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spinner_hint, viewGroup, false);
            TextView textView = (TextView) view.findViewById(R.id.spinner_hint);

            if(i == 0 || i == (mGroupedItemList.size() + 1)){;
                textView.setText(getString(R.string.spinner_hint));
                return view;
            } else if (i > 0 && i <= mGroupedItemList.size()){
                textView.setText(mGroupedItemList.get(i - 1).getName());
                return view;
            } else {
                textView.setText(mSingleItemList.get(i - (mGroupedItemList.size() + 2)).getName());
                return view;
            }
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
