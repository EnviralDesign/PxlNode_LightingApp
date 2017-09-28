package aquilina.ryan.homelightingapp.ui.group_managment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import aquilina.ryan.homelightingapp.R;

/**
 * Created by SterlingRyan on 9/18/2017.
 */

public class AddMacroDialog extends DialogFragment {

    private String mMacroName;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static AddMacroDialog newInstance(){
        AddMacroDialog dialog = new AddMacroDialog();
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
        title.setText(getString(R.string.dialog_save_macro_title));
        final EditText editText = rootView.findViewById(R.id.dialog_edit_text);
        editText.setHint(getString(R.string.dialog_preset_edit_text_hint));
        Button saveButton = (Button) rootView.findViewById(R.id.button2);
        saveButton.setText(getString(R.string.dialog_save_button));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidText(editText.getText().toString())){
                    mMacroName = editText.getText().toString();
                    saveMacro();
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
    private void saveMacro(){
        ((GroupManagementActivity) getActivity()).saveMacro(mMacroName);
    }
}