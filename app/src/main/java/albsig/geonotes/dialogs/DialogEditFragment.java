package albsig.geonotes.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import albsig.geonotes.R;


public class DialogEditFragment extends DialogFragment {

    private DialogEditListener dialoglistener;
    private long primaryKey;
    private String title;
    private String note;
    private String kind;

    public interface DialogEditListener {
        void onDialogEditSaveClick(long primaryKey, String title, String note, String kind);
        void onDialogEditDeleteClick(long primaryKey, String kind);
    }


    @Override
    public Dialog onCreateDialog(Bundle saveInstanceState) {

        this.primaryKey = getArguments().getLong("primaryKey");
        this.title = getArguments().getString("title");
        this.note = getArguments().getString("note");
        this.kind = getArguments().getString("kind");


        //creating new builder, setting its view.
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_edit, null);

        builder.setView(view);

        // getting the Views components.
        final Button dialogCancelButton = (Button) view.findViewById(R.id.dialogEditCancel);
        final Button dialogSaveButton = (Button) view.findViewById(R.id.dialogEditSave);
        final Button dialogDeleteButton = (Button) view.findViewById(R.id.dialogEditDelete);

        final EditText dialogTitle = (EditText) view.findViewById(R.id.dialogEditTitle);
        final EditText dialogNote = (EditText) view.findViewById(R.id.dialogEditNote);

        dialogTitle.setText(this.title);
        dialogNote.setText(this.note);
        final String kind = this.kind;

        // following lines create button press logic and notifys listener if its called
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        dialogSaveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (dialogTitle == null || dialogTitle.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill out Title", Toast.LENGTH_SHORT).show();
                } else {
                    dismiss();
                    dialoglistener.onDialogEditSaveClick(primaryKey, dialogTitle.getText().toString(), dialogNote.getText().toString(), kind);
                }
            }
        });

        dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
                dialoglistener.onDialogEditDeleteClick(primaryKey, kind);
            }
        });


        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialoglistener = (DialogEditListener) activity;
        } catch (Exception e) {
            // The activity doesn't implement the interface, throw exception
            Log.d("Debug", activity.toString() + " must implement NoticeDialogListener");
        }
    }

}
