package albsig.geonotes.activity;


import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import albsig.geonotes.R;
import albsig.geonotes.database.DatabaseHelper;
import albsig.geonotes.database.TrackDto;
import albsig.geonotes.database.WaypointDto;
import albsig.geonotes.dialogs.DialogEditFragment;

public class DBActivity extends AppCompatActivity implements DialogEditFragment.DialogEditListener {


    private DatabaseHelper database;
    private ScrollView sv;
    private ArrayList<TrackDto> allEntrysTrack;
    private ArrayList<WaypointDto> allEntrysWaypoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        this.database = new DatabaseHelper(this);
        this.allEntrysWaypoint = database.getAllEntrysWaypoint();
        this.allEntrysTrack = database.getAllEntrysTrack();

        this.sv = (ScrollView) findViewById(R.id.scrollView);

        displayScrollView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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


    private void displayScrollView() {

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(10, 5, 10, 5);

        for (WaypointDto currentEntry : this.allEntrysWaypoint) {

            final TextView textView = new TextView(this);

            final long primaryKey = currentEntry.getPrimaryKey();
            final String title = currentEntry.getTitle();
            final String note = currentEntry.getNote();

            final Double latitude = currentEntry.getLatitude();
            final Double longitude = currentEntry.getLongitude();

            textView.setText(Html.fromHtml("<b><u>" + title + "</u></b><br/><br/>" + "<i>" + note + "</i>"));
            textView.setBackgroundResource(R.drawable.db_textview_shape);


            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Bundle args = new Bundle();
                    DialogFragment dialogEdit = new DialogEditFragment();

                    args.putLong("primaryKey", primaryKey);
                    args.putString("title", title);
                    args.putString("note", note);
                    dialogEdit.setArguments(args);
                    dialogEdit.show(getSupportFragmentManager(), "dialogEdit");

                    return true;
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("DBActivity.LATITUDE", latitude);
                    returnIntent.putExtra("DBActivity.LONGITUDE",longitude);
                    returnIntent.putExtra("DBActivity.TITLE",title);
                    setResult(DBActivity.RESULT_OK,returnIntent);
                    finish();
                }
            });

            textView.setLayoutParams(params);
            ll.addView(textView);
        }
        this.sv.addView(ll);
    }

    @Override
    public void onDialogEditSaveClick(long primaryKey, String title, String note) {
        this.database.changeEntryWaypoint(primaryKey, title, note);
        for (WaypointDto pro : this.allEntrysWaypoint) {
            if (pro.getPrimaryKey() == primaryKey) {
                pro.setTitle(title);
                pro.setNote(note);
            }
        }
        this.sv.removeAllViews();
        displayScrollView();
    }


    /**
     * Deletes an entry from the database and from the arraylist where it is stashed.
     * removes all views from scrollview, as it can only hold one view.
     * creates a new view with the new entrys.
     *
     * @param primaryKey
     */
    @Override
    public void onDialogEditDeleteClick(long primaryKey) {
        database.deleteEntryWaypoint(primaryKey);
        WaypointDto delme = null;
        for (WaypointDto pro : this.allEntrysWaypoint) {
            if (pro.getPrimaryKey() == primaryKey) {
                delme = pro;
            }
        }
        allEntrysWaypoint.remove(delme);
        this.sv.removeAllViews();
        displayScrollView();
    }
}
