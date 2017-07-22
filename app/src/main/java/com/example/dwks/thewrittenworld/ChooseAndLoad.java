package com.example.dwks.thewrittenworld;
//Class to confirm data to use, calls methods to make geofences and has buttons to launch Map and List View

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ChooseAndLoad extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = ChooseAndLoad.class.getSimpleName();
    //Buttons
    private Button loadPlacesButton;
    private Button createFenceButton;

    private ArrayList<PlaceObject> placeObjects;

    //Geofencing
    private GeofencingApi geofencingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_and_load);

        setUpButtons();

    }


    /**
     * Helper Method to initialise buttons and set listeners
     */
    private void setUpButtons() {

        loadPlacesButton = (Button) findViewById(R.id.loadPlaces);
        createFenceButton = (Button) findViewById(R.id.createGeofences);

        loadPlacesButton.setOnClickListener(this);
        createFenceButton.setOnClickListener(this);
        createFenceButton.setEnabled(false);

    }

    private void loadPlaces() {
        String placesJson = this.assestJsonFile();
        placeObjects = new PlacesListCreator(placesJson)
                .getPointOfInterestObjects();
    }

    //LOAD FROM ASSEST WILL BE REPLACED WITH SOME METHOD FOR DATABASE LOADING////
    private String assestJsonFile() {
        String json = null;
        try {
            InputStream is = getAssets().open("dickensJSON");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadPlaces:
                this.loadPlaces();
                Constants constants = Constants.getInstance();
                for (PlaceObject place : placeObjects) {
                    constants.places.put(String.valueOf(place.getId()), place);

                }
                Log.d(TAG, String.valueOf(constants.places.isEmpty()));
                createFenceButton.setEnabled(true);
                break;
            case R.id.createGeofences:
                this.populateGeofenceList();
                createFenceButton.setEnabled(false);
                break;
        }
    }

    private void populateGeofenceList() {
        Constants constant = Constants.getInstance();
        for (PlaceObject place : placeObjects) {
            //Create a geofence object for each place not ticked off as visited
            if (!place.isVisited()) {
                Geofence geofence = (new Geofence.Builder()
                        .setRequestId(place.getDb_key())
                        .setCircularRegion(place.getLatitude(), place.getLongitude(), 100)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build());

                constant.geofenceArrayList.add(geofence);
            }

        }
        int arraylength = constant.geofenceArrayList.size();
        Log.d(TAG, String.valueOf(arraylength));

    }
}
