package albsig.geonotes.activity;


import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, DialogSaveFragment.DialogSaveListener {

    private static final long UPDATE_INTERVAL_IN_MILLIS = 3000;
    private static final long UPDATE_FASTEST_INTERVAL_IN_MILLIS = 3000;
    private static final int UPATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;


    //variable to check if activity is currently tracking.
    private boolean isTracking = false;

    private GoogleMap map;
    private GoogleApiClient googleApi;

    private LocationRequest locationRequest;
    private Location currentLocation;
    //Database var
    private DatabaseHelper database;

    //Elements in UserInterface
    private Button buttonTrack;
    private Chronometer chronoTime;
    //Location from DBActivity
    private double latitude = 48.209280;
    private double longitude = 9.032319;

    /**
     * String to accumulate all locations.
     */
    private String trackInfo;


    private ArrayList<MarkerOptions> savedMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // getting mapFragment.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get the GoogleMap Object.
        this.map = mapFragment.getMap();

        database = new DatabaseHelper(this);

        //init the UI elements
        this.buttonTrack = (Button) findViewById(R.id.buttonTrack);
        this.chronoTime = (Chronometer) findViewById(R.id.main_chrom);

        buildGoogleApiClient();

    }

    private void buildGoogleApiClient() {
        googleApi = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLIS);
        this.locationRequest.setFastestInterval(UPDATE_FASTEST_INTERVAL_IN_MILLIS);
        this.locationRequest.setPriority(UPATE_PRIORITY);
    }


    @Override //saves map data in Bundle
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("markers", this.savedMarkers);
    }

    @Override //restores markers
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.savedMarkers = savedInstanceState.getParcelableArrayList("markers");

        for (MarkerOptions marker : savedMarkers) {
            this.map.addMarker(marker);
        }
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
        googleMap.addMarker(new MarkerOptions().position(position).title(getString(R.string.standardWaypointTitle)));
        googleMap.animateCamera(camera);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.currentLocation = location;
        this.map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        this.map.setMyLocationEnabled(true);
        this.trackInfo += currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ";";
    }

    /**
     * Starts locating current position with best available provider.
     */
    @SuppressWarnings("ResourceType")
    public void startTracking(View v) {
        if (!isTracking) {

            this.trackInfo = String.valueOf("");
            this.buttonTrack.setText(R.string.track_stop);
            this.chronoTime.start();
            this.chronoTime.setBase(SystemClock.elapsedRealtime());
            this.isTracking = true;

            startLocationUpdates();

        } else if (isTracking) {

            this.buttonTrack.setText(R.string.track_start);
            this.isTracking = false;
            this.chronoTime.stop();

            stopLocationUpdates();

            if (this.currentLocation == null) {
                Toast.makeText(this, getString(R.string.locationError), Toast.LENGTH_LONG).show();
            } else {
                DialogFragment dialogTrack = new DialogSaveFragment();
                Bundle args = new Bundle();
                args.putString("title", getString(R.string.saveTrackTitle));
                dialogTrack.setArguments(args);
                dialogTrack.show(getSupportFragmentManager(), "dialogSaveTrack");
            }
        }
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                this.googleApi, this.locationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApi, this);
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

        if (this.currentLocation == null) {
            Toast.makeText(this, getString(R.string.locationError), Toast.LENGTH_LONG).show();
        } else {
            DialogFragment dialogLocation = new DialogSaveFragment();
            Bundle args = new Bundle();
            args.putString("title", getString(R.string.saveLocationTitle));
            dialogLocation.setArguments(args);
            dialogLocation.show(getSupportFragmentManager(), "dialogSaveLocation");
        }
    }

    @Override
    public void onDialogSaveSaveClick(String title, String note, String tag) {
        //differentiate between incoming save clicks from DialogsaveFragment.
        if (tag.equals("dialogSaveLocation")) {
            database.saveCurrentPosition(title, note, this.currentLocation);
        }

        if (tag.equals("dialogSaveTrack")) {
            database.saveTrack(title, note, this.trackInfo, SystemClock.elapsedRealtime() - this.chronoTime.getBase());
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

                final MarkerOptions markOps = new MarkerOptions();
                markOps.position(position);
                markOps.title(title);
                this.savedMarkers.add(markOps); //add markers to restore it later
                map.addMarker(markOps);
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

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.googleError), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApi.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApi.isConnected()) {
            googleApi.disconnect();
        }
    }

}

