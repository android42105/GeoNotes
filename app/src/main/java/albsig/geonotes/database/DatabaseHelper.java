package albsig.geonotes.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    // If you change the database schema, you must increment the database version.
    //---------------------------------------------------------------
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "GeoNotesDATABASE.db";
    //---------------------------------------------------------------
    public static final String TABLE_NAME_TRACK = "SavedTracks";
    public static final String TABLE_NAME_WAYPOINT = "SavedWaypoints";


    //Columns TRACK
    public static final String COLUMN_TRACK_TITLE = "title";
    public static final String COLUMN_TRACK_NOTE = "note";
    public static final String COLUMN_TRACK_TRACKINFO = "trackinfo";
    public static final String COLUMN_TRACK_TIME = "time";

    //Columns WAYPOINT
    public static final String COLUMN_WAYPOINT_TITLE = "title";
    public static final String COLUMN_WAYPOINT_NOTE = "note";
    public static final String COLUMN_WAYPOINT_LATITUDE = "latitude";
    public static final String COLUMN_WAYPOINT_LONGITUDE = "longitude";

    //---------------------------------------------------------------

    //SQL statements
    private final String CREATE_TABLE_TRACK = "CREATE TABLE " +
            TABLE_NAME_TRACK + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_TRACK_TITLE + " TEXT," +
            COLUMN_TRACK_NOTE + " TEXT," +
            COLUMN_TRACK_TRACKINFO + " TEXT," +
            COLUMN_TRACK_TIME + " REAL" +
            " );";

    private final String CREATE_TABLE_WAYPOINT = "CREATE TABLE " +
            TABLE_NAME_WAYPOINT + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_WAYPOINT_TITLE + " TEXT," +
            COLUMN_WAYPOINT_NOTE + " TEXT," +
            COLUMN_WAYPOINT_LATITUDE + " REAL," +
            COLUMN_WAYPOINT_LONGITUDE + " REAL" +
            " );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("IN DATABASEHELPER", " onCREATE HAS BEEN CALLED");
        db.execSQL(CREATE_TABLE_TRACK);
        db.execSQL(CREATE_TABLE_WAYPOINT);
        fillMockData(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME_WAYPOINT;
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }


    /**
     * fills the db with mockdata of some tracks and waypoints.
     * so we dont always have to create new objects for testing.
     */
    private void fillMockData(SQLiteDatabase db) {

        final ContentValues waymock = new ContentValues();
        final ContentValues trackmock = new ContentValues();

        waymock.put(COLUMN_WAYPOINT_TITLE, "Haux Gebäude Albstadt");
        waymock.put(COLUMN_WAYPOINT_NOTE, "das ist ein testeintrag um funktionen zu testen.");
        waymock.put(COLUMN_WAYPOINT_LATITUDE, 48.210567);
        waymock.put(COLUMN_WAYPOINT_LONGITUDE, 9.030785);
        db.insert(TABLE_NAME_WAYPOINT, "null", waymock);

        trackmock.put(COLUMN_TRACK_TITLE, "Albstadt - Sigmaringen");
        trackmock.put(COLUMN_TRACK_NOTE, "Ein Track zum testen");
        trackmock.put(COLUMN_TRACK_TIME, 234443);
        trackmock.put(COLUMN_TRACK_TRACKINFO, "48.205776,9.036642;" +
                "48.191891,9.064180;" +
                "48.185367, 9.100229;" +
                "48.150011, 9.144584;" +
                "48.113149, 9.201654;" +
                "48.090221, 9.205773");
        db.insert(TABLE_NAME_TRACK, "null", trackmock);
        //dont close() the db here, onCreate will close it. else error occurs.
    }


    /**
     * saves a location with a title and a string in the  current db
     *
     * @return primarykey of the newly added entry
     */
    public long saveTrack(String title, String note, String trackInfo, long time) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TRACK_TITLE, title);
        values.put(COLUMN_TRACK_NOTE, note);

        values.put(COLUMN_TRACK_TRACKINFO, trackInfo);
        values.put(COLUMN_TRACK_TIME, time);

        long primarykey = db.insert(TABLE_NAME_TRACK, "null", values);
        db.close();
        return primarykey;
    }

    public long saveCurrentPosition(String title, String note, Location location) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_WAYPOINT_TITLE, title);
        values.put(COLUMN_WAYPOINT_NOTE, note);

        if (location != null) {
            values.put(COLUMN_WAYPOINT_LATITUDE, location.getLatitude());
            values.put(COLUMN_WAYPOINT_LONGITUDE, location.getLongitude());
        } else {
            values.put(COLUMN_WAYPOINT_LATITUDE, 48.209280);
            values.put(COLUMN_WAYPOINT_LONGITUDE, 9.032319);
        }

        long primarykey = db.insert(TABLE_NAME_WAYPOINT, "null", values);
        db.close();
        return primarykey;
    }


    public ArrayList<TrackDto> getAllEntrysTrack() {

        ArrayList<TrackDto> allEntrys = new ArrayList<TrackDto>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * FROM " + TABLE_NAME_TRACK;
        Cursor c = db.rawQuery(query, null);

        while (c.moveToNext()) {
            allEntrys.add(new TrackDto(c.getLong(0), c.getString(1), c.getString(2), c.getString(3), c.getLong(4)));
        }
        db.close();
        return allEntrys;
    }

    public ArrayList<WaypointDto> getAllEntrysWaypoint() {

        ArrayList<WaypointDto> allEntrys = new ArrayList<WaypointDto>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * FROM " + TABLE_NAME_WAYPOINT;
        Cursor c = db.rawQuery(query, null);

        while (c.moveToNext()) {
            allEntrys.add(new WaypointDto(c.getLong(0), c.getString(1), c.getString(2), c.getDouble(3), c.getDouble(4)));
        }
        db.close();
        return allEntrys;
    }

    public void deleteEntryTrack(long primaryKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_TRACK, _ID + "=" + primaryKey, null);
        db.close();
    }

    public void deleteEntryWaypoint(long primaryKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_WAYPOINT, _ID + "=" + primaryKey, null);
        db.close();
    }

    public void changeEntryWaypoint(long primaryKey, String title, String note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_WAYPOINT_TITLE, title);
        values.put(COLUMN_WAYPOINT_NOTE, note);

        db.update(TABLE_NAME_WAYPOINT, values, _ID + "=" + primaryKey, null);
    }

    public void changeEntryTrack(long primaryKey, String title, String note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TRACK_TITLE, title);
        values.put(COLUMN_TRACK_NOTE, note);

        db.update(TABLE_NAME_TRACK, values, _ID + "=" + primaryKey, null);
    }
}
