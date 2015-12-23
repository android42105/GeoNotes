package albsig.geonotes.util;

/**
 * Simple class LatLng class.
 */
public class EzLatLng {

    private double latitude;
    private double longitude;

    public EzLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
