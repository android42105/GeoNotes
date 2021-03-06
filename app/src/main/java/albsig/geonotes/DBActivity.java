package albsig.geonotes;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }


    private void swipeLeft2Right() {
        onBackPressed();
    }

    private void readFromDatabase() {

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        String[] projections = {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_NOTE,
                FeedEntry.COLUMN_NAME_LONGITUDE, FeedEntry.COLUMN_NAME_LATITUDE, FeedEntry.COLUMN_NAME_FILTER
        };

        Cursor c = dbase.query(
                FeedEntry.TABLE_NAME, projections, null, null, null, null, null);
        boolean goon = true;
        while (c.moveToNext()) {
            final TextView bra = new TextView(this);

            final int id = c.getInt(0);
            final String title = c.getString(1);
            final String note = c.getString(2);
            final String longitude = c.getString(3);
            final String latitude = c.getString(4);
            final int filter = c.getInt(5);

            bra.setText("Title\n" + title + "\n\nNote\n" + note + "\n\nLongitude\n" + longitude + "\n\nLatitude\n" + latitude + "\n\nFilter\n" + filter);
            bra.setBackgroundResource(R.drawable.db_textview_shape);

            bra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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

                            String longitude = "999.99";
                            String latitude = "888.88";
                            int filter = 5;

                            ContentValues values = new ContentValues();
                            values.put(FeedEntry.COLUMN_NAME_TITLE, title);
                            values.put(FeedEntry.COLUMN_NAME_NOTE, note);
                            values.put(FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
                            values.put(FeedEntry.COLUMN_NAME_LATITUDE, latitude);
                            values.put(FeedEntry.COLUMN_NAME_FILTER, filter);


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
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new AbsListView.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            params.setMargins(10, 10, 10, 10);
            bra.setLayoutParams(params);
            ll.addView(bra);
        }
        this.sv.addView(ll);
    }

}
