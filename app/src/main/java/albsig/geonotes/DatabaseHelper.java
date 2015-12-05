package albsig.geonotes;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static albsig.geonotes.DatabaseContract.*;

public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "GeoNotesDATABASE.db";


    //SQL statements
    private final String SQL_CREATE_LOCATION = "CREATE TABLE " +
            FeedEntry.TABLE_NAME + " (" +
            FeedEntry._ID + " INTEGER PRIMARY KEY," +
            FeedEntry.COLUMN_NAME_TITLE + " TEXT," +
            FeedEntry.COLUMN_NAME_NOTE + " TEXT," +
            FeedEntry.COLUMN_NAME_LONGITUDE + " TEXT," +
            FeedEntry.COLUMN_NAME_LATITUDE + " TEXT," +
            FeedEntry.COLUMN_NAME_FILTER + " INTEGER " +
            " FOREIGN KEY (" + FeedEntry.COLUMN_NAME_FILTER + ") REFERENCES " + FeedEntry.TABLE_NAME2 + " (" + FeedEntry._ID + "));";

    private final String SQL_CREATE_FILTER = "CREATE TABLE " +
            FeedEntry.TABLE_NAME2 + " (" +
            FeedEntry._ID + " INTEGER PRIMARY KEY," +
            FeedEntry.COLUMN_NAME_DESCRIPTION + " TEXT );";

    private final String SQL_DELETE_TABLE_LOCATION = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    private final String SQL_DELETE_TABLE_FILTER = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME2;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("IN DATABASEHELPER", " onCREATE HAS BEEN CALLED");
        db.execSQL(SQL_CREATE_LOCATION);
        db.execSQL(SQL_CREATE_FILTER);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE_LOCATION);
        db.execSQL(SQL_DELETE_TABLE_FILTER);
        onCreate(db);
    }
}
