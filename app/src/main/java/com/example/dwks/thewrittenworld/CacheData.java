package com.example.dwks.thewrittenworld;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by User on 26/07/2017.
 */

public class CacheData extends Application{

    Constants constants = Constants.getInstance();
    FileInputStream fileInputStream;
    ObjectInputStream objectInputStream;

    ObjectOutputStream objectOutputStream;
    FileOutputStream fileOutputStream;

    private String cachePlacesMap = "cachedPlacesMap";
    private String cachePlacesSet = "cachedPlacesSet.json";

    public CacheData(){
        Log.d("Test", "Cache Data Constructor");
        saveToCache(cachePlacesMap, constants.places);
        saveToCache(cachePlacesSet, constants.placeObjects);

    }
    private void saveToCache(String saveFileName, Object saved){


        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = openFileOutput(saveFileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try {
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(saved);
            Log.d("Save", "Save to cache called");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
