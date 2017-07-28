package com.example.dwks.thewrittenworld;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by User on 22/07/2017.
 */
//Stores constants such as the HashMap of Points of Interest Currently loaded Geofence Radius etc.
class Constants {
    private static final Constants ourInstance = new Constants();

    static Constants getInstance() {
        return ourInstance;
    }

    private Constants() {
    }

    //Globals, not good practice
    //Map with IDs from DB linked to objects, used to retrieve places when intents are fired
    public static HashMap<String,PlaceObject> places = new HashMap<String ,PlaceObject>();

    public static TreeSet<PlaceObject> placeObjects = new TreeSet<PlaceObject>();
    public static ArrayList<Geofence> geofenceArrayList = new ArrayList<Geofence>();

    public  static  HashMap<PlaceObject, Geofence> placeObjectGeofenceHashMap = new HashMap<>();

}
