package albsig.geonotes.database;


import android.provider.BaseColumns;

public final class DatabaseContract {

    // To prevent someone from accidentally instantiating the contract class.
    public DatabaseContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "SavedLocation2";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_NOTE = "note";
        public static final String COLUMN_NAME_LOCATION = "location";
    }


}
