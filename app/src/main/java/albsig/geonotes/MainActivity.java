package albsig.geonotes;


import android.app.Service;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    private static final float UPATE_DISTANCE_IN_METERS = 5;

    //variable to check if activity is currently tracking.
    private boolean isTracking = false;

    private GoogleMap map;

    private LocationManager locationManager;
    private Location currentLocation;

    //Elements in UserInterface
    private Button buttonTrack;
    private ProgressBar progressBar;

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

        //init the UI elements
        this.buttonTrack = (Button) findViewById(R.id.buttonTrack);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onMapReady(GoogleMap googleMap) {

        LatLng location_win = new LatLng(48.209280, 9.032319);
        googleMap.addMarker(new MarkerOptions().position(location_win).title("WIN Fakultät"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location_win));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
        map.setMyLocationEnabled(true);
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

    public void stopLocationUpdates() {
        //noinspection ResourceType
        locationManager.removeUpdates(this);
    }

}

