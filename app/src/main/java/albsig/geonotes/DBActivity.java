package albsig.geonotes;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static albsig.geonotes.DatabaseContract.*;

public class DBActivity extends AppCompatActivity {

    //variables for swiping
    private float x1, x2;
    private DisplayMetrics metrics;
    private int MIN_DISTANCE;

    //Databse variables
    private DatabaseHelper dbhelper;
    private SQLiteDatabase dbase;

    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        this.dbhelper = new DatabaseHelper(this);
        this.dbase = dbhelper.getReadableDatabase();

        this.sv = (ScrollView) findViewById(R.id.scrollView);
        readFromDatabase();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_db, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }


    private void readFromDatabase() {

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        final String[] projections = {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_NOTE,
                FeedEntry.COLUMN_NAME_LOCATION
        };

        Cursor c = dbase.query(
                FeedEntry.TABLE_NAME, projections, null, null, null, null, null);

        while (c.moveToNext()) {

            final TextView bra = new TextView(this);
            final String title = c.getString(1);
            final String note = c.getString(2);

            bra.setText(Html.fromHtml("<b><u>" + title + "</u></b><br/><br/>" + "<i>" + note + "</i>"));
            bra.setBackgroundResource(R.drawable.db_textview_shape);

            bra.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DBActivity.this);
                    dialogBuilder.setView(R.layout.dialog_edit);
                    final AlertDialog dialog = dialogBuilder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();


                    final Button dialogSaveButton = (Button) dialog.findViewById(R.id.dialogEditSave);
                    final Button dialogDeleteButton = (Button) dialog.findViewById(R.id.dialogEditDelete);
                    final Button dialogCancelButton = (Button) dialog.findViewById(R.id.dialogEditCancel);
                    final Button dialogShowButton = (Button) dialog.findViewById(R.id.dialogEditShow);


                    final EditText dialogTitle = (EditText) dialog.findViewById(R.id.dialogEditTitle);
                    final EditText dialogNote = (EditText) dialog.findViewById(R.id.dialogEditNote);

                    dialogTitle.setHint(title);
                    dialogNote.setHint(note);

                    dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });

                    dialogSaveButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            dialog.cancel();
                            DBActivity.this.dbhelper = new DatabaseHelper(DBActivity.this);
                            DBActivity.this.dbase = dbhelper.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put(FeedEntry.COLUMN_NAME_TITLE, title);
                            values.put(FeedEntry.COLUMN_NAME_NOTE, note);
                            values.put(FeedEntry.COLUMN_NAME_LOCATION, "location test");

                            //TODO Update in Database
                        }
                    });

                    dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //TODO Delete Button functionality
                        }
                    });

                    dialogShowButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            //TODO Show on Map Button functionality
                        }
                    });
                    return false;
                }
            });

            bra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bra.startAnimation(AnimationUtils.loadAnimation(DBActivity.this, android.R.anim.fade_out));
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            params.setMargins(10, 10, 10, 10);
            bra.setLayoutParams(params);
            ll.addView(bra);
        }
        this.sv.addView(ll);
    }

}
