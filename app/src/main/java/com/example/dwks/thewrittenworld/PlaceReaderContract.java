package com.example.dwks.thewrittenworld;

import android.provider.BaseColumns;

/**
 * Created by User on 25/07/2017.
 */

//sets out contract for local SQL Database

public class PlaceReaderContract {
    //private constructor to stop class be instantiated accidently
    private PlaceReaderContract(){}

    //Inner class to define table contents
    public static class PlacesTable implements BaseColumns{

        public static final String TABLE_NAME = "UsersPlaces";

        public static final String COLUMN_NAME_LISTNAME ="ListID";
        public static final String COLUMN_NAME_PlACE = "Place";
        public static final String COLUMN_NAME_BOOKTITLE = "Book_Title";
        public static final String COLUMN_NAME_SNIPPET = "Snippet";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_DBKEY ="DB_Key";
        public static final String COLUMN_NAME_VISITED ="Visted";

    }
}
