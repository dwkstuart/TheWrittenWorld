package com.example.dwks.thewrittenworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**Utility class that handles the saving and loading of values into the shared preferences
 * Includes parsing arrays and maps into Json using Gson
 * Created by David Stuart on 07/08/2017.
 */

public class ProcessSharedPref  {

    Constants constants = Constants.getInstance();
    Context context;
    private static final String TAG = ProcessSharedPref.class.getSimpleName();

    public ProcessSharedPref(Context context) {
        this.context = context;
    }


    /**Saves the HashMap and TreeSet tracking the currently loaded PlaceObjects
     * into the SharedPreferences as a Json file.
     * Parses the complex data structures using Gson library
     *
     */
    public void saveAsJson(){
        Gson gson = new Gson();
        String jsonHashMap =  gson.toJson(Constants.places);
        String jsonTreeSet = gson.toJson(Constants.placeObjects);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString(String.valueOf(R.string.placesHashMap),jsonHashMap);
        editor.putString(String.valueOf(R.string.placesTreeSet),jsonTreeSet);

        editor.apply();
    }

    /**Loads the Json representations of the TreeSet and HashMap from SharedPreferences
     * Parses them using Gson and stores them in the constants file
     *
     */
    public void loadFromJson(){
        Gson gson = new Gson();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String jsonHashMap =  sharedPref.getString(String.valueOf(R.string.placesHashMap), "");
        String jsonTreeSet = sharedPref.getString(String.valueOf(R.string.placesTreeSet), "");

        //need to specify TokenType of data structure to Gson
        Map<String, PlaceObject> mapJson = gson.fromJson(jsonHashMap, new TypeToken<HashMap<String ,PlaceObject>>() {}.getType());

        Set<PlaceObject> set = gson.fromJson(jsonTreeSet, new TypeToken<TreeSet<PlaceObject>>() {}.getType());

        Constants.places = (HashMap) mapJson;
        Constants.placeObjects = (TreeSet) set;

    }
    public boolean savedListExists(){


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.contains("TEST");

    }

    public boolean savedDataExists(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.contains(String.valueOf(R.string.placesHashMap));
    }

    /**Saves the curently added titles as a Json representation
     * Used in the Search activity
     * @param addedTitles
     */
    public void saveAddedTitles(ArrayList<PlaceObject> addedTitles){

        Gson gson = new Gson();
        String jsonArrayList = gson.toJson(addedTitles);
        Log.d(TAG,jsonArrayList);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString("TEST",jsonArrayList);
        editor.apply();

    }

    /**Loads the currently added titles Json file from SharedPreferences and parses it into array
     * used for handling Search activity lifecycle events
     *
     * @return
     */
    public ArrayList<PlaceObject> loadAddedTitles(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String jsonArrayList =  sharedPref.getString("TEST", "");
        Log.d(TAG, "JSON" + jsonArrayList);
        ArrayList<PlaceObject> temp = new ArrayList<PlaceObject>();
        Gson gson = new Gson();
        temp = gson.fromJson(jsonArrayList, new TypeToken<ArrayList<PlaceObject>>() {}.getType());
        Log.d(TAG,temp.toString());


        return temp;

    }
}
