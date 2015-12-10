package albsig.geonotes.activity;


import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import albsig.geonotes.R;
import albsig.geonotes.database.DatabaseHelper;
import albsig.geonotes.database.DatabaseProduct;
import albsig.geonotes.dialogs.DialogEditFragment;

public class DBActivity extends AppCompatActivity implements DialogEditFragment.DialogEditListener {


    private DatabaseHelper database;
    private ScrollView sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        this.database = new DatabaseHelper(this);
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
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(10, 5, 10, 5);


        ArrayList<DatabaseProduct> allEntrys = this.database.getAllEntrys();

        for (DatabaseProduct currentEntry : allEntrys) {

            final TextView textView = new TextView(this);
            final String title = currentEntry.getTitle();
            final String note = currentEntry.getNote();

            textView.setText(Html.fromHtml("<b><u>" + title + "</u></b><br/><br/>" + "<i>" + note + "</i>"));
            textView.setBackgroundResource(R.drawable.db_textview_shape);


            textView.setOnLongClickListener(new View.OnLongClickListener() {
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

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.startAnimation(AnimationUtils.loadAnimation(DBActivity.this, android.R.anim.fade_out));
                }
            });


            textView.setLayoutParams(params);
            ll.addView(textView);
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
