package com.example.dwks.thewrittenworld;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by User on 07/08/2017.
 */

public class ProcessSharedPref extends Application {

    Constants constants = Constants.getInstance();
    Context context;
    private static final String TAG = ProcessSharedPref.class.getSimpleName();

    public ProcessSharedPref(Context context) {
        this.context = context;
    }

    //TEST

    public void saveAsJson(){
        Gson gson = new Gson();
        String jsonHashMap =  gson.toJson(constants.places);
        String jsonTreeSet = gson.toJson(constants.placeObjects);

        Log.d(TAG, "Saved as json" + jsonHashMap);

        SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(R.string.shared_pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString(String.valueOf(R.string.placesHashMap),jsonHashMap);
        editor.putString(String.valueOf(R.string.placesTreeSet),jsonTreeSet);

        editor.commit();
        //Log.d(TAG, "Shared pref get all result" + sharedPref.getAll());
    }

    public void loadFromJson(){
        Gson gson = new Gson();
        SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(R.string.shared_pref_file), Context.MODE_PRIVATE);
        String jsonHashMap =  sharedPref.getString(String.valueOf(R.string.placesHashMap), "");
        String jsonTreeSet = sharedPref.getString(String.valueOf(R.string.placesTreeSet), "");


        Map<String, PlaceObject> mapJson = gson.fromJson(jsonHashMap, new TypeToken<HashMap<String ,PlaceObject>>() {}.getType());
        Log.d(TAG, "Loaded from shared pref " + mapJson.toString());

        Set<PlaceObject> set = gson.fromJson(jsonTreeSet, new TypeToken<TreeSet<PlaceObject>>() {}.getType());

        constants.places = (HashMap) mapJson;
        constants.placeObjects = (TreeSet) set;

    }

    public boolean savedDataExists(){
        SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(R.string.shared_pref_file), Context.MODE_PRIVATE);

        if(sharedPref.contains(String.valueOf(R.string.placesHashMap))){
            return true;
            }
        return false;
    }
}
