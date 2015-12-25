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


    /**
     * adds TextViews from Tracks and Waypoints into a Scrollview
     * to display it on Screen.
     */
    private void displayScrollView() {

        final LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(10, 5, 10, 5);

        for (TrackDto track : this.allEntrysTrack) {

            TextView trackTextView = configureTrack(track);
            trackTextView.setLayoutParams(params);
            ll.addView(trackTextView);
        }

        for (WaypointDto waypoint : this.allEntrysWaypoint) {

            TextView waypointTextView = configureWaypoint(waypoint);
            waypointTextView.setLayoutParams(params);
            ll.addView(waypointTextView);
        }
        this.sv.addView(ll);
    }


    /**
     * configures a Waypoint and its listeners into a TextView.
     *
     * @param waypoint
     * @return the configured TextView which shows a Waypoint.
     */
    private TextView configureWaypoint(WaypointDto waypoint) {
        final TextView waypointTextView = new TextView(this);

        final long primaryKey = waypoint.getPrimaryKey();
        final String title = waypoint.getTitle();
        final String note = waypoint.getNote();
        final Double latitude = waypoint.getLatitude();
        final Double longitude = waypoint.getLongitude();

        waypointTextView.setText(Html.fromHtml("<b>" + title + "</b><br/><br/>" + "<i>" + note + "</i>"));
        waypointTextView.setBackgroundResource(R.drawable.db_textview_waypoint_shape);

        waypointTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle args = new Bundle();
                DialogFragment dialogEdit = new DialogEditFragment();

                args.putString("dialogTitle", "Edit Waypoint");
                args.putLong("primaryKey", primaryKey);
                args.putString("title", title);
                args.putString("note", note);
                dialogEdit.setArguments(args);
                dialogEdit.show(getSupportFragmentManager(), "dialogEditWaypoint");

                return true;
            }
        });

        waypointTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("DBActivity.LATITUDE", latitude);
                returnIntent.putExtra("DBActivity.LONGITUDE", longitude);
                returnIntent.putExtra("DBActivity.TITLE", title);
                setResult(DBActivity.RESULT_OK, returnIntent);
                finish();
            }
        });

        return waypointTextView;
    }

    /**
     * configures a Track and its listeners into a TextView.
     *
     * @param track
     * @return the configured TextView which shows a Track.
     */
    private TextView configureTrack(TrackDto track) {

        final TextView trackTextView = new TextView(this);
        final long primaryKey = track.getPrimaryKey();
        final String title = track.getTitle();
        final String note = track.getNote();
        final String time = track.getTime();

        trackTextView.setText(Html.fromHtml("<b>" + title + "<br/>" + time + "</b><br/><br/>" + "<i>" + note + "</i>"));
        trackTextView.setBackgroundResource(R.drawable.db_textview_track_shape);

        trackTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle args = new Bundle();
                DialogFragment dialogEdit = new DialogEditFragment();

                args.putString("dialogTitle", "Edit Track");
                args.putLong("primaryKey", primaryKey);
                args.putString("title", title);
                args.putString("note", note);

                dialogEdit.setArguments(args);
                dialogEdit.show(getSupportFragmentManager(), "dialogEditTrack");

                return true;
            }
        });

        trackTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return trackTextView;
    }


    @Override
    public void onDialogEditSaveClick(long primaryKey, String title, String note, String tag) {

        if (tag.equals("dialogEditWaypoint")) {
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

        if (tag.equals("dialogEditTrack")) {
            this.database.changeEntryTrack(primaryKey, title, note);
            for (TrackDto pro : this.allEntrysTrack) {
                if (pro.getPrimaryKey() == primaryKey) {
                    pro.setTitle(title);
                    pro.setNote(note);
                }
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
    public void onDialogEditDeleteClick(long primaryKey, String tag) {

        if (tag.equals("dialogEditWaypoint")) {
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

        if (tag.equals("dialogEditTrack")) {
            database.deleteEntryTrack(primaryKey);
            TrackDto delme = null;
            for (TrackDto pro : this.allEntrysTrack) {
                if (pro.getPrimaryKey() == primaryKey) {
                    delme = pro;
                }
            }
            allEntrysTrack.remove(delme);
        }
        this.sv.removeAllViews();
        displayScrollView();
    }
}



