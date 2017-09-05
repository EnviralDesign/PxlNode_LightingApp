package aquilina.ryan.homelightingapp.ui.scan_mode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import aquilina.ryan.homelightingapp.R;

/**
 * Created by SterlingRyan on 9/4/2017.
 */

public class AddGroupDialog extends DialogFragment{

    private String mGroupName;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */

    public static AddGroupDialog newInstance(){
        AddGroupDialog dialog = new AddGroupDialog();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_save_group));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_edit_text, null);
        final EditText editText = (EditText) rootView.findViewById(R.id.dialog_edit_text);
        editText.setHint(getString(R.string.dialog_edit_text_hint));

        builder.setView(rootView)
                .setPositiveButton(getString(R.string.dialog_save_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isValidText(editText.getText().toString())){
                            mGroupName = editText.getText().toString();
                            saveGroup();
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

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
     * Saves the group locally
     */
    private void saveGroup(){
        ((ScanActivity) getActivity()).saveGroupLocally(mGroupName);
    }
}
