package com.example.dwks.thewrittenworld;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.dwks.thewrittenworld.PlaceReaderContract.PlacesTable.TABLE_NAME;

/**
 * Created by User on 25/07/2017.
 */

public class UserPlacesDbHelper extends SQLiteOpenHelper {

   //Databasename
    private final static String DATABASE_NAME ="userplaces.db";
    ///Database version
    private static final int DATABASE_VERSION = 3;

    private static SQLiteDatabase db;

    public UserPlacesDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //TODO add user id to the table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_USERPLACE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                PlaceReaderContract.PlacesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlaceReaderContract.PlacesTable.COLUMN_NAME_LISTNAME + " TEXT NOT NULL," +
                PlaceReaderContract.PlacesTable.COLUMN_NAME_PlACE + " TEXT NOT NULL, " +
                PlaceReaderContract.PlacesTable.COLUMN_NAME_BOOKTITLE + " TEXT NOT NULL, " +
                PlaceReaderContract.PlacesTable.COLUMN_NAME_DBKEY + " TEXT NOT NULL, " +
                PlaceReaderContract.PlacesTable.COLUMN_NAME_LATITUDE + " DECIMAL NOT NULL, " +
                PlaceReaderContract.PlacesTable.COLUMN_NAME_LONGITUDE + " DECIMAL NOT NULL, " +
                PlaceReaderContract.PlacesTable.COLUMN_NAME_VISITED + " BOOLEAN DEFAULT FALSE" +
                ")";
        sqLiteDatabase.execSQL(SQL_CREATE_USERPLACE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    //add to DB
    public void addNewList(PlaceObject placeObject, String listName){
        ContentValues row = new ContentValues();

        row.put(PlaceReaderContract.PlacesTable.COLUMN_NAME_LISTNAME, listName);
        row.put(PlaceReaderContract.PlacesTable.COLUMN_NAME_PlACE,placeObject.getBookTitle());
        row.put(PlaceReaderContract.PlacesTable.COLUMN_NAME_BOOKTITLE,placeObject.getBookTitle());
        row.put(PlaceReaderContract.PlacesTable.COLUMN_NAME_DBKEY, placeObject.getDb_key());
        row.put(PlaceReaderContract.PlacesTable.COLUMN_NAME_LATITUDE,placeObject.getLatitude());
        row.put(PlaceReaderContract.PlacesTable.COLUMN_NAME_LONGITUDE,placeObject.getLongitude());
        row.put(PlaceReaderContract.PlacesTable.COLUMN_NAME_VISITED,placeObject.getLongitude());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME,null,row);
        db.close();

    }
}
