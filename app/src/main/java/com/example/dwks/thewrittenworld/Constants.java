package com.example.dwks.thewrittenworld;

import android.location.Location;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**Singleton class that stores constant data that is accessed across the applcation
 * Stores constants such as the HashMap of Points of Interest Currently loaded Geofence
 */
//
class Constants {
    private static final Constants ourInstance = new Constants();

    static Constants getInstance() {
        return ourInstance;
    }

    private Constants() {
    }

    //Map with IDs from DB linked to objects, used to retrieve places when intents are fired
    public static HashMap<String,PlaceObject> places = new HashMap<>();
    //TreeSet containing placeObjects currently in the users list
    public static TreeSet<PlaceObject> placeObjects = new TreeSet<>();
    //List of currently active Geofences
    public static ArrayList<Geofence> geofenceArrayList = new ArrayList<>();

    ///Map connecting PlaceObject to it's related Geofence, needed to remove fences on triggering
    public  static  HashMap<PlaceObject, Geofence> placeObjectGeofenceHashMap = new HashMap<>();

    //users latest location
    public static Location lastLocation;
    //List of users saved collections
    public static List<SavedCollection> files = new ArrayList<>();
    //Flag to track if notifications are set or not, used to determine which UI elements to display
    public static boolean notificationsOn;

}
