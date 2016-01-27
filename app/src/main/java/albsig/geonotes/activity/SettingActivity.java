package albsig.geonotes.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import albsig.geonotes.R;

public class SettingActivity extends AppCompatActivity {

    private EditText updateIntervalValue;
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        this.settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.editor = settings.edit();

        this.updateIntervalValue = (EditText) findViewById(R.id.settingsUpdateIntervalValue);

        settingsLoad();
    }


    private void settingsLoad() {
        long upd = this.settings.getLong("updateInterval", 3) / 1000;

        this.updateIntervalValue.setText(String.valueOf(upd));
    }


    public void settingsSave(View v) {

        if (this.updateIntervalValue == null || this.updateIntervalValue.toString().isEmpty()) {
            Toast.makeText(this, "Please fill out settings", Toast.LENGTH_SHORT).show();

        } else {

            long intervalSeconds = Long.parseLong(String.valueOf(this.updateIntervalValue.getText()));
            editor.putLong("updateInterval", intervalSeconds * 1000);
            editor.commit();
            Toast.makeText(this, "Settings have been saved", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void settingsCancel(View v) {
        finish();
    }


}
