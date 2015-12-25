package albsig.geonotes.database;

/**
 * simple class to refreclt how the databse looks.
 */
public class TrackDto {

    private Long primaryKey;
    private String title;
    private String note;
    private String waypoints;
    private long time;

    public TrackDto(Long primaryKey, String title, String note, String waypoints, long time) {
        this.primaryKey = primaryKey;
        this.title = title;
        this.note = note;
        this.waypoints = waypoints;
        this.time = time;
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

    public String getNote() {
        return note;
    }

    public String getWaypoints() {
        return this.waypoints;
    }

    public long getTime() {
        return this.time;
    }
}
