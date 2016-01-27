package albsig.geonotes.database;

/**
 * simple class to refreclt how the databse looks.
 */
public class WaypointDto {

    private Long primaryKey;
    private String title;
    private String note;
    private Double latitude;
    private Double longitude;

    public WaypointDto(Long primaryKey, String title, String note, Double latitude, Double longitude) {
        this.primaryKey = primaryKey;
        this.title = title;
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getPrimaryKey() {

        return primaryKey;
    }

    public String getTitle() {
        return title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getNote() {
        return note;
    }
}
