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
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "GeoNotesDATABASE.db";
    //---------------------------------------------------------------
    public static final String TABLE_NAME = "SavedLocation2";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_NOTE = "note";
    public static final String COLUMN_NAME_LATITUDE = "latitude";
    public static final String COLUMN_NAME_LONGITUDE = "longitude";
    public static final String COLUMN_NAME_TRACKNO = "trackno";
    //---------------------------------------------------------------

    //SQL statements
    private final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_TITLE + " TEXT," +
            COLUMN_NAME_NOTE + " TEXT," +
            COLUMN_NAME_LATITUDE + " REAL," +
            COLUMN_NAME_LONGITUDE + " REAL," +
            COLUMN_NAME_TRACKNO + " REAL" +
            " );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("IN DATABASEHELPER", " onCREATE HAS BEEN CALLED");
        db.execSQL(SQL_CREATE_ENTRIES);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    /**
     * saves a location with a title and a string in the  current db
     *
     * @param title
     * @param note
     * @param location only lat and long will be saved.
     * @return primarykey of the newly added entry
     */
    public long saveCurrentPosition(String title, String note, Location location, Double trackNo) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME_TITLE, title);
        values.put(COLUMN_NAME_NOTE, note);
        if(location != null) {
            values.put(COLUMN_NAME_LATITUDE, location.getLatitude());
            values.put(COLUMN_NAME_LONGITUDE, location.getLongitude());
        } else {
            values.put(COLUMN_NAME_LATITUDE, 48.209280);
            values.put(COLUMN_NAME_LONGITUDE, 9.032319);
        }
        values.put(COLUMN_NAME_LONGITUDE,trackNo);

        long primarykey = db.insert(TABLE_NAME, "null", values);
        db.close();
        return primarykey;
    }


    public ArrayList<DatabaseProduct> getAllEntrys() {

        ArrayList<DatabaseProduct> allEntrys = new ArrayList<DatabaseProduct>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * FROM " + TABLE_NAME;
        Cursor c = db.rawQuery(query, null);

        while (c.moveToNext()) {
            allEntrys.add(new DatabaseProduct(c.getLong(0), c.getString(1), c.getString(2),
                    c.getDouble(3), c.getDouble(4),c.getDouble(5)));
        }
        db.close();
        return allEntrys;
    }

    public void deleteEntry(long primaryKey) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, _ID + "=" + primaryKey, null);
        db.close();
    }

    public void changeEntry(long primaryKey, String title, String note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE, title);
        values.put(COLUMN_NAME_NOTE, note);

        db.update(TABLE_NAME, values, _ID + "=" + primaryKey, null);
    }
}
