package aquilina.ryan.homelightingapp.ui.design_mode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.DialogTitle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import aquilina.ryan.homelightingapp.R;
import aquilina.ryan.homelightingapp.ui.scan_mode.AddGroupDialog;
import aquilina.ryan.homelightingapp.ui.scan_mode.ScanActivity;

/**
 * Created by SterlingRyan on 9/11/2017.
 */

public class AddPresetDialog extends DialogFragment{

    private String mPresetName;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */

    public static AddPresetDialog newInstance(){
        AddPresetDialog dialog = new AddPresetDialog();
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_save, null);
        TextView title = (TextView) rootView.findViewById(R.id.alertTitle);
        title.setText(getString(R.string.dialog_save_preset_title));
        final EditText editText = rootView.findViewById(R.id.dialog_edit_text);
        editText.setHint(getString(R.string.dialog_preset_edit_text_hint));
        Button saveButton = (Button) rootView.findViewById(R.id.button2);
        saveButton.setText(getString(R.string.dialog_save_button));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidText(editText.getText().toString())){
                    mPresetName = editText.getText().toString();
                    saveGroup();
                    dismiss();
                }
            }
        });

        Button cancelButton = (Button) rootView.findViewById(R.id.button3);
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
        if(text.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * Saves the preset locally
     */
    private void saveGroup(){
        //TODO create saving functionality
    }
}
