/*
 * Created by Ryan Aquilina on 10/16/17 4:53 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 10/16/17 4:52 PM
 */

package frost.com.homelighting.ui.scan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import frost.com.homelighting.MainActivity;
import frost.com.homelighting.R;

public class AddGroupDialog extends DialogFragment {

    private String mGroupName;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */

    public static AddGroupDialog newInstance(){
        return new AddGroupDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View rootView = inflater.inflate(R.layout.dialog_save, nullParent);
        TextView title = rootView.findViewById(R.id.alertTitle);
        title.setText(getString(R.string.dialog_save_group));
        final EditText editText = rootView.findViewById(R.id.dialog_edit_text);
        editText.setHint(getString(R.string.dialog_edit_text_hint));
        Button saveButton = rootView.findViewById(R.id.button2);
        saveButton.setText(getString(R.string.dialog_save_button));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidText(editText.getText().toString())){
                    mGroupName = editText.getText().toString();
                    saveGroup();
                    dismiss();
                }
            }
        });

        Button cancelButton = rootView.findViewById(R.id.button3);
        cancelButton.setText(getString(R.string.dialog_cancel_button));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        builder.setView(rootView);
        return builder.create();
    }

    /**
     * Checks if text is valid
     */
    private boolean isValidText(String text){
        text = text.trim();

        if(text == null || text.equals("")){
            return false;
        }

        return true;
    }

    /**
     * Saves the group locally
     */
    private void saveGroup(){
        ((ScanFragment) ((MainActivity)getActivity()).getSupportFragmentManager().findFragmentById(R.id.fragment_content)).saveGroup(mGroupName);
    }
}
