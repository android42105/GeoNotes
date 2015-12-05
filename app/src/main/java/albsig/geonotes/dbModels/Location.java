package albsig.geonotes.dbModels;

/**
 * Created by Ihsan on 05.12.2015.
 */
public class Location {
    private int id;
    private String text;
    private String note;
    private String longitude;
    private String latitude;
    private Filter filter;

    public Location(int id, String text, String note, String longitude, String latitude, Filter filter){
        this.id = id;
        this.text = text;
        this.note = note;
        this.longitude = longitude;
        this.latitude = latitude;
        this.filter = filter;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", note='" + note + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", filter=" + filter +
                '}';
    }
}
