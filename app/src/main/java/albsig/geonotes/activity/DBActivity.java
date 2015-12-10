package albsig.geonotes.activity;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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

import albsig.geonotes.R;
import albsig.geonotes.database.DatabaseHelper;
import albsig.geonotes.dialogs.DialogEditFragment;

import static albsig.geonotes.database.DatabaseContract.*;

public class DBActivity extends AppCompatActivity implements DialogEditFragment.DialogEditListener {

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
        final String[] projections = {FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_NOTE,
                FeedEntry.COLUMN_NAME_LOCATION};

        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new AbsListView.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        params.setMargins(10, 10, 10, 10);

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
                    Bundle args = new Bundle();
                    DialogFragment dialogEdit = new DialogEditFragment();

                    args.putString("title", title);
                    args.putString("note", note);
                    dialogEdit.setArguments(args);
                    dialogEdit.show(getSupportFragmentManager(), "dialogEdit");

                    return true;
                }
            });

            bra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bra.startAnimation(AnimationUtils.loadAnimation(DBActivity.this, android.R.anim.fade_out));
                }
            });


            bra.setLayoutParams(params);
            ll.addView(bra);
        }

        this.sv.addView(ll);
    }

    @Override
    public void onDialogEditSaveClick(String title, String note) {

    }

    @Override
    public void onDialogEditDeleteClick() {

    }
}
