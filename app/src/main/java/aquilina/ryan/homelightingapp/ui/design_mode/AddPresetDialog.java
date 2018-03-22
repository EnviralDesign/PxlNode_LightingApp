/*
 * Created by Ryan Aquilina on 10/18/17 4:21 PM
 * Contact details in https://www.upwork.com/freelancers/~01ed20295946e923f0
 * Copyright (c) 2017.  All rights reserved
 *
 * Last modified 9/28/17 3:26 PM
 */

package aquilina.ryan.homelightingapp.ui.design_mode;

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

import aquilina.ryan.homelightingapp.R;

public class AddPresetDialog extends DialogFragment{

    private String mPresetName;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddPresetDialog newInstance(){
        return new AddPresetDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final ViewGroup nullParent = null;
        View rootView = inflater.inflate(R.layout.dialog_save, nullParent, false);
        TextView title = rootView.findViewById(R.id.alertTitle);
        title.setText(getString(R.string.dialog_save_preset_title));
        final EditText editText = rootView.findViewById(R.id.dialog_edit_text);
        editText.setHint(getString(R.string.dialog_preset_edit_text_hint));
        Button saveButton = rootView.findViewById(R.id.button2);
        saveButton.setText(getString(R.string.dialog_save_button));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidText(editText.getText().toString())){
                    mPresetName = editText.getText().toString();
                    savePreset();
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
        if(text.equals("")){
            return false;
        }

        return true;
    }

    /**
     * Saves the preset locally
     */
    private void savePreset(){
        ((DesignActivity) getActivity()).savePresetLocally(mPresetName);
    }
}
