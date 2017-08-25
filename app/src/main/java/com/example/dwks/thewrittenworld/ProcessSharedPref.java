package com.example.dwks.thewrittenworld;

import android.app.Application;
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
        String jsonHashMap =  gson.toJson(Constants.places);
        String jsonTreeSet = gson.toJson(Constants.placeObjects);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString(String.valueOf(R.string.placesHashMap),jsonHashMap);
        editor.putString(String.valueOf(R.string.placesTreeSet),jsonTreeSet);

        editor.apply();
        //Log.d(TAG, "Shared pref get all result" + sharedPref.getAll());
    }

    public void loadFromJson(){
        Gson gson = new Gson();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String jsonHashMap =  sharedPref.getString(String.valueOf(R.string.placesHashMap), "");
        String jsonTreeSet = sharedPref.getString(String.valueOf(R.string.placesTreeSet), "");


        Map<String, PlaceObject> mapJson = gson.fromJson(jsonHashMap, new TypeToken<HashMap<String ,PlaceObject>>() {}.getType());

        Set<PlaceObject> set = gson.fromJson(jsonTreeSet, new TypeToken<TreeSet<PlaceObject>>() {}.getType());

        Constants.places = (HashMap) mapJson;
        Constants.placeObjects = (TreeSet) set;

    }
    public boolean savedListExists(){
       // SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(R.string.shared_pref_file), Context.MODE_PRIVATE);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.contains("TEST");

    }

    public boolean savedDataExists(){
        //SharedPreferences sharedPref = context.getSharedPreferences(String.valueOf(R.string.shared_pref_file), Context.MODE_PRIVATE);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.contains(String.valueOf(R.string.placesHashMap));
    }

    public void saveAddedTitles(ArrayList<PlaceObject> addedTitles){
       Log.d(TAG,"Saved added titles");
        Gson gson = new Gson();
        String jsonArrayList = gson.toJson(addedTitles);
        Log.d(TAG,jsonArrayList);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor= sharedPref.edit();
        editor.putString("TEST",jsonArrayList);
        editor.apply();
        boolean check = this.savedListExists();
        Log.d(TAG, "Saved List data exist" + check);
    }

    public ArrayList<PlaceObject> loadAddedTitles(){
        Log.d(TAG, "Load added titles called!!");

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
