package albsig.geonotes.database;

/**
 * simple class to refreclt how the databse looks.
 */
public class TrackDto {

    private Long primaryKey;
    private String title;
    private String note;
    private String waypoints;
    private String time;

    public TrackDto(Long primaryKey, String title, String note, String waypoints, String time) {
        this.primaryKey = primaryKey;
        this.title = title;
        this.note = note;
        this.waypoints = waypoints;
        this.time = time;
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

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getPrimaryKey() {

        return primaryKey;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getWaypoints() {
        return this.waypoints;
    }

    public String getTime() {
        return this.time;
    }
}
