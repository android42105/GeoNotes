package albsig.geonotes.activity;


import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import albsig.geonotes.R;
import albsig.geonotes.database.DatabaseHelper;
import albsig.geonotes.dialogs.DialogSaveFragment;
import albsig.geonotes.util.EzSwipe;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, DialogSaveFragment.DialogSaveListener {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final float UPATE_DISTANCE_IN_METERS = 5;

    //variable to check if activity is currently tracking.
    private boolean isTracking = false;

    private GoogleMap map;

    private LocationManager locationManager;
    private Location currentLocation;

    //Database var
    private DatabaseHelper database;

    //Elements in UserInterface
    private Button buttonTrack;
    private Button buttonRemoveMarkers;

    //Location from DBActivity
    private double latitude = 48.209280;
    private double longitude = 9.032319;
    private String title;

    private String trackInfo;
    private long time;
    private int point;

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

        database = new DatabaseHelper(this);

        //init the UI elements
        this.buttonTrack = (Button) findViewById(R.id.buttonTrack);
        this.buttonRemoveMarkers = (Button) findViewById(R.id.buttonRemoveMarkers);

        title = getString(R.string.standardWaypointTitle);
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
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(position, 15);
        googleMap.addMarker(new MarkerOptions().position(position).title(title));
        googleMap.animateCamera(camera);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        this.map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        this.map.setMyLocationEnabled(true);

        this.trackInfo += currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ";";
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
            time = System.currentTimeMillis();
            this.trackInfo = String.valueOf("");
            String provider = locationManager.getBestProvider(new Criteria(), true);
            this.locationManager.requestLocationUpdates(provider, UPDATE_INTERVAL_IN_MILLISECONDS, UPATE_DISTANCE_IN_METERS, this);
            this.buttonTrack.setText(R.string.track_stop);
            this.isTracking = true;

        } else if (isTracking) {
            this.time = System.currentTimeMillis() - this.time;

            this.buttonTrack.setText(R.string.track_start);
            this.isTracking = false;
            stopLocationUpdates();

            DialogFragment dialogTrack = new DialogSaveFragment();
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.saveTrackTitle));
            dialogTrack.setArguments(args);
            dialogTrack.show(getSupportFragmentManager(), "dialogSaveTrack");
        }
    }


    private void stopLocationUpdates() {
        //noinspection ResourceType
        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (EzSwipe.getAction(event)) {

            case EzSwipe.SWIPE_RIGHT_TO_LEFT:
                swipeRight2Left();
                break;
            default:
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
        DialogFragment dialogLocation = new DialogSaveFragment();
        Bundle args = new Bundle();
        args.putString("title", getString(R.string.saveLocationTitle));
        dialogLocation.setArguments(args);
        dialogLocation.show(getSupportFragmentManager(), "dialogSaveLocation");
    }

    @Override
    public void onDialogSaveSaveClick(String title, String note, String tag) {
        //differentiate between incoming save clicks from DialogsaveFragment.
        if (tag.equals("dialogSaveLocation")) {
            if (this.currentLocation == null) {
                Toast.makeText(getApplicationContext(), getString(R.string.locationError), Toast.LENGTH_LONG).show();
            } else {
                database.saveCurrentPosition(title, note, this.currentLocation);
            }
        } else if (tag.equals("dialogSaveTrack")) {
            if (this.trackInfo == null || this.trackInfo.isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.locationError), Toast.LENGTH_LONG).show();
            } else {
                database.saveTrack(title, note, this.trackInfo, this.time);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == DBActivity.RESULT_OK) { // requestCode 1 is DBActivity

            if (data.getIntExtra("DBActivity.CHECK", -1) == 1) { // click on Waypoint

                final double lat = data.getDoubleExtra("DBActivity.LATITUDE", 48.209280);
                final double lng = data.getDoubleExtra("DBActivity.LONGITUDE", 9.032319);
                final String title = data.getStringExtra("DBActivity.TITLE");
                final LatLng position = new LatLng(lat, lng);

                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(position, 15);
                map.addMarker(new MarkerOptions().position(position).title(title));
                map.animateCamera(camera);
            }

            if (data.getIntExtra("DBActivity.CHECK", -1) == 2) { // click on Track

                final String tracks = data.getStringExtra("DBActivity.TRACKSTRING");
                final String[] track = tracks.split(";");
                final String title = data.getStringExtra("DBActivity.TITLE");
                final ArrayList<LatLng> arrayPoints = new ArrayList();
                final PolylineOptions polylineOptions = new PolylineOptions();

                for (String waypoint : track) {
                    String[] pos = waypoint.split(",");
                    LatLng position = new LatLng(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]));
                    arrayPoints.add(position);
                }
                final LatLng firstPoint = arrayPoints.get(0);
                final LatLng lastPoint = (arrayPoints.get(arrayPoints.size() - 1));

                polylineOptions.addAll(arrayPoints);
                polylineOptions.color(Color.BLUE);
                polylineOptions.width(5);

                LatLngBounds.Builder bounds = new LatLngBounds.Builder();
                bounds.include(firstPoint);
                bounds.include(lastPoint);

                CameraUpdate camera = CameraUpdateFactory.newLatLngBounds(bounds.build(), 5);
                map.addMarker(new MarkerOptions().position(firstPoint).title(title));
                map.addMarker(new MarkerOptions().position(lastPoint).title(title));
                map.addPolyline(polylineOptions);
                map.animateCamera(camera);
            }
        }
    }

    public void removeMarkers(View v) {
        map.clear();
    }
}

