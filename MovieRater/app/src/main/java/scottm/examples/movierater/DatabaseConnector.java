package scottm.examples.movierater;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseConnector {

    private static final String TAG = "DatabaseConnector";

    private static final String DATABASE_NAME = "MovieRatings";
    private SQLiteDatabase database; 
    private DatabaseOpenHelper databaseOpenHelper; 


    public DatabaseConnector(Context context) {
        databaseOpenHelper = 
                new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }


    public void open() throws SQLException {
        // create or open a database for reading/writing
        database = databaseOpenHelper.getWritableDatabase();
    } 


    public void close() {
        if (database != null)
            database.close();
    } 


    // inserts a new rating into the database
    public void insertRating(String title, int rating, 
            String genre, String dateSeen, String tag1, String tag2) {

        ContentValues newRating = createRating(rating,  genre, 
                dateSeen, tag1, tag2);
        newRating.put("name", title);

        open();
        database.insert("ratings", null, newRating);
        close();
    }
    
    private ContentValues createRating(int rating, 
            String genre, String dateSeen, String tag1, String tag2) {
        ContentValues result = new ContentValues();
        result.put("rating", rating);
        result.put("genre", genre);
        result.put("dateSeen", dateSeen);
        result.put("tag1", tag1);
        result.put("tag2", tag2);
        return result;
    }


    // updates a rating in the database
    public void updateRating(long id, String name, int rating, 
            String genre, String dateSeen, String tag1, String tag2) {

        ContentValues editRating = createRating(rating,  genre, 
                dateSeen, tag1, tag2);

        open(); 
        database.update("ratings", editRating, "_id=" + id, null);
        close(); 
    } 

    public Cursor getAllRatings() {
        return database.query("ratings", new String[] {"_id", "name"}, 
                null, null, null, null, "name");
        // query(String table, 
        // String[] columns, String selection, String[] selectionArgs, 
        // String groupBy, String having, String orderBy)
    }


    // get a Cursor containing all information about the movie specified
    // by the given id
    public Cursor getOneRating(long id) {
        return database.query(
                "ratings", null, "_id=" + id, null, null, null, null);

        // public Cursor query (String table, String[] columns, 
        // String selection, String[] selectionArgs, String groupBy, 
        // String having, String orderBy, String limit)
    }


    // delete the rating specified by the given id
    public void deleteRating(long id) {
        open(); 
        database.delete("ratings", "_id=" + id, null);
        close();
    } 


    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
            Log.d(TAG, "in constructor");
        } 


        // creates the ratings table when the database is created
        @Override   
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "in on create");
            // query to create a new table named ratings
            String createQuery = "CREATE TABLE ratings" +
                    "(_id INTEGER PRIMARY KEY autoincrement, " +
                    "name TEXT, " +
                    "genre TEXT, " +
                    "dateSeen TEXT, " +
                    "tag1 TEXT, " +
                    "tag2 TEXT, " +
                    "rating INTEGER);";

            db.execSQL(createQuery);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
                int newVersion) 
        { /* nothing to do*/ }
    } 
} 

