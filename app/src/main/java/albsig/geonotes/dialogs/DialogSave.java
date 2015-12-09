package albsig.geonotes.dialogs;


import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import albsig.geonotes.R;
import albsig.geonotes.database.DatabaseContract;
import albsig.geonotes.database.DatabaseHelper;

public class DialogSave extends Dialog implements View.OnClickListener {

    private Location currentLocation;

    private DatabaseHelper dbhelper;
    private SQLiteDatabase dbase;

    private EditText dialogTitle;
    private EditText dialogNote;


    public DialogSave(Context context, Location currentLocation) {
        super(context);
        this.currentLocation = currentLocation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCanceledOnTouchOutside(false);
        this.setContentView(R.layout.dialog_save);
        //call setLayout after setContentView. -1 = MATCH_PARENT, -2 = WRAP_CONTENT
        this.getWindow().setLayout(-1, -2);

        final Button dialogCancelButton = (Button) findViewById(R.id.dialogCancel);
        final Button dialogSaveButton = (Button) findViewById(R.id.dialogSave);

        this.dialogTitle = (EditText) findViewById(R.id.dialogTitle);
        this.dialogNote = (EditText) findViewById(R.id.dialogNote);

        dialogCancelButton.setOnClickListener(this);
        dialogSaveButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.dialogCancel:
                Toast.makeText(this.getContext(), "sasss", Toast.LENGTH_SHORT).show();
                dismiss();
            case R.id.dialogSave:
                saveCurrentPosition(this.dialogTitle.getText().toString(), this.dialogNote.getText().toString(), this.currentLocation);
                dismiss();
        }
    }

    private void saveCurrentPosition(String title, String note, Location location) {
        try {
            this.dbhelper = new DatabaseHelper(getContext());
            this.dbase = dbhelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseContract.FeedEntry.COLUMN_NAME_TITLE, title);
            values.put(DatabaseContract.FeedEntry.COLUMN_NAME_NOTE, note);
            values.put(DatabaseContract.FeedEntry.COLUMN_NAME_LOCATION, "location test");

            //insert can return long, which is the primary key.
            dbase.insert(DatabaseContract.FeedEntry.TABLE_NAME, "null", values);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong ...", Toast.LENGTH_LONG).show();
        }
    }
}
