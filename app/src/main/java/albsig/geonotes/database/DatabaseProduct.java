package albsig.geonotes.database;

/**
 * simple class to refreclt how the databse looks.
 */
public class DatabaseProduct {

    private Long primaryKey;
    private String title;
    private String note;
    private Long latitude;
    private Long longitude;

    public DatabaseProduct(Long primaryKey, String title, String note, Long latitude, Long longitude) {
        this.primaryKey = primaryKey;
        this.title = title;
        this.note = note;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public void setPrimaryKey(Long primaryKey) {
        this.primaryKey = primaryKey;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    public Long getPrimaryKey() {

        return primaryKey;
    }

    public String getTitle() {
        return title;
    }

    public Long getLatitude() {
        return latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public String getNote() {
        return note;
    }
}
