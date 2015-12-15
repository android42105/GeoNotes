package albsig.geonotes.activity;


import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import albsig.geonotes.R;
import albsig.geonotes.database.DatabaseHelper;
import albsig.geonotes.dialogs.DialogSaveFragment;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, DialogSaveFragment.DialogSaveListener {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final float UPATE_DISTANCE_IN_METERS = 5;

    //variable to check if activity is currently tracking.
    private boolean isTracking = false;

    private GoogleMap map;

    private LocationManager locationManager;
    private Location currentLocation;

    //Database var
    private DatabaseHelper databse;

    //Elements in UserInterface
    private Button buttonTrack;
    private ProgressBar progressBar;

    //variables for swiping
    private float x1, x2;
    private DisplayMetrics metrics;
    private int MIN_DISTANCE;

    //Location from DBActivity
    private double latitude = 48.209280;
    private double longitude = 9.032319;
    private String title = "WIN Fakultät";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setting up LocationManager
        this.locationManager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);

        // getting mapFragment.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get the GoogleMap Object.
        this.map = mapFragment.getMap();

        databse = new DatabaseHelper(this);

        //init the UI elements
        this.buttonTrack = (Button) findViewById(R.id.buttonTrack);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.GONE);

        //display info
        this.metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        this.MIN_DISTANCE = metrics.widthPixels / 3;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = new LatLng(latitude, longitude);
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(position,15);
        googleMap.addMarker(new MarkerOptions().position(position).title(title));
        googleMap.animateCamera(camera);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        this.map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        this.map.setMyLocationEnabled(true);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    /**
     * Starts locating current position with best available provider.
     */
    @SuppressWarnings("ResourceType")
    public void startTracking(View v) {
        if (!isTracking) {
            Toast.makeText(this, "locating Position, please wait...", Toast.LENGTH_LONG).show();
            String provider = locationManager.getBestProvider(new Criteria(), true);
            this.locationManager.requestLocationUpdates(provider, UPDATE_INTERVAL_IN_MILLISECONDS, UPATE_DISTANCE_IN_METERS, this);
            this.buttonTrack.setText(R.string.track_stop);
            this.isTracking = true;
            this.progressBar.setVisibility(View.VISIBLE);
        } else if (isTracking) {
            Toast.makeText(this, "locating stopped", Toast.LENGTH_LONG).show();
            this.buttonTrack.setText(R.string.track_start);
            this.isTracking = false;
            this.progressBar.setVisibility(View.GONE);
            stopLocationUpdates();
        }
    }


    private void stopLocationUpdates() {
        //noinspection ResourceType
        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x1 - x2;
                if (deltaX > MIN_DISTANCE) {
                    swipeRight2Left();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void swipeRight2Left() {

        Intent intent = new Intent(this, DBActivity.class);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void openSaveDialog(View v) {
        DialogFragment dialog = new DialogSaveFragment();
        dialog.show(getSupportFragmentManager(), "dialogSave");
    }

    @Override
    public void onDialogSaveSaveClick(String title, String note) {
        databse.saveCurrentPosition(title, note, this.currentLocation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == DBActivity.RESULT_OK) {
                latitude = data.getDoubleExtra("DBActivity.LATITUDE", 48.209280);
                longitude = data.getDoubleExtra("DBActivity.LONGITUDE", 9.032319);
                title = data.getStringExtra("DBActivity.TITLE");

                LatLng position = new LatLng(latitude, longitude);
                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(position,15);
                map.addMarker(new MarkerOptions().position(position).title(title));
                map.animateCamera(camera);
            }
        }
    }
}

