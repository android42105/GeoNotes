package albsig.geonotes.dialogs;

import android.app.Activity;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import albsig.geonotes.R;

public class DialogSaveFragment extends DialogFragment {

    //listener to deliver actions
    private NoticeDialogListener dialoglistener;
    private EditText title;
    private EditText note;

    //callback interface for interaction.
    public interface NoticeDialogListener {
        void onDialogSaveSaveClick(String title, String note);
    }

    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_save, null);

        builder.setView(view);

        // getting the Views components.
        final Button dialogCancelButton = (Button) view.findViewById(R.id.dialogCancel);
        final Button dialogSaveButton = (Button) view.findViewById(R.id.dialogSave);
        this.title = (EditText) view.findViewById(R.id.dialogTitle);
        this.note = (EditText) view.findViewById(R.id.dialogNote);

        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (title == null || title.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill out Title", Toast.LENGTH_SHORT).show();
                } else {
                    dismiss();
                    dialoglistener.onDialogSaveSaveClick(title.getText().toString(), note.getText().toString());
                }
            }
        });

        return builder.create();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialoglistener = (NoticeDialogListener) activity;
        } catch (Exception e) {
            // The activity doesn't implement the interface, throw exception
            Log.d("Debug", activity.toString() + " must implement NoticeDialogListener");
        }
    }
}


