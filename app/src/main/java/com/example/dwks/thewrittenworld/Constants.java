package com.example.dwks.thewrittenworld;

import android.location.Location;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public GoogleSignInAccount currentUser;
    //Globals, not good practice
    //Map with IDs from DB linked to objects, used to retrieve places when intents are fired
    public static HashMap<String,PlaceObject> places = new HashMap<>();

    public static TreeSet<PlaceObject> placeObjects = new TreeSet<>();
    public static ArrayList<Geofence> geofenceArrayList = new ArrayList<>();

    public  static  HashMap<PlaceObject, Geofence> placeObjectGeofenceHashMap = new HashMap<>();

    public Location lastLocation;
    public List<SavedCollection> files = new ArrayList<>();

}
