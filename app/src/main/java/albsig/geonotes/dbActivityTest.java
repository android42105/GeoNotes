package albsig.geonotes;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static albsig.geonotes.DatabaseContract.*;

public class dbActivityTest extends AppCompatActivity {

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

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        String[] projections = {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_NOTE,
                FeedEntry.COLUMN_NAME_LOCATION
        };

        Cursor c = dbase.query(
                FeedEntry.TABLE_NAME, projections, null, null, null, null, null);

        boolean goon = true;
        while (goon) {
            goon = c.moveToNext();
            TextView bra = new TextView(this);
            bra.setText(c.getPosition() + "das ist aus der datenbank");
            ll.addView(bra);
        }
        this.sv.addView(ll);
        c.moveToLast();
        Log.d("DB ACTIVITY", c.getPosition() + " ");
        int n = c.getPosition();


    }

}
