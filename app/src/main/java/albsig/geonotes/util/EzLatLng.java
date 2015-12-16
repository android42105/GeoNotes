package albsig.geonotes.util;

/**
 * Simple class LatLng class.
 */
public class EzLatLng {

    private double latitude;
    private double longitute;

    public EzLatLng(double latitude, double longitute) {
        this.latitude = latitude;
        this.longitute = longitute;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitute() {
        return longitute;
    }
}
