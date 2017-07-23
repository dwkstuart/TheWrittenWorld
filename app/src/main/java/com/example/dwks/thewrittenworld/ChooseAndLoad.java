package com.example.dwks.thewrittenworld;
//Class to confirm data to use, calls methods to make geofences and has buttons to launch Map and List View

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.InputStream;

//TODO Decide whether to update to just use GeofencingApiClient

public class ChooseAndLoad extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, ResultCallback<Status> {

    private final static String TAG = ChooseAndLoad.class.getSimpleName();
    //Buttons
    private Button loadPlacesButton;
    private Button createFenceButton;
    private Button loadMap;

   // private ArrayList<PlaceObject> placeObjects;

    //Geofencing
    private GeofencingApi geofencingApi;
    private PendingIntent pendingIntent;
    private GoogleApiClient googleApiClient;

    Constants constants = Constants.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_and_load);

        setUpButtons();
        pendingIntent = null;
        geofencingApi = LocationServices.GeofencingApi;

    }


    /**
     * Helper Method to initialise buttons and set listeners
     */
    private void setUpButtons() {

        loadPlacesButton = (Button) findViewById(R.id.loadPlaces);
        createFenceButton = (Button) findViewById(R.id.createGeofences);
        loadMap = (Button) findViewById(R.id.ViewMap);

        loadPlacesButton.setOnClickListener(this);
        createFenceButton.setOnClickListener(this);
        loadMap.setOnClickListener(this);
        createFenceButton.setEnabled(false);

    }

    private void loadPlaces() {
        String placesJson = this.assestJsonFile();
        constants.placeObjects = new PlacesListCreator(placesJson)
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

                for (PlaceObject place : constants.placeObjects) {
                    constants.places.put(String.valueOf(place.getId()), place);

                }
                Log.d(TAG, String.valueOf(constants.places.isEmpty()));
                createFenceButton.setEnabled(true);
                break;
            case R.id.createGeofences:
                this.populateGeofenceList();
                createGoogleApi();
                googleApiClient.connect();
                createFenceButton.setEnabled(false);
                break;

            case R.id.ViewMap:
                Intent  map = new Intent(this, MapDisplay.class);
                startActivity(map);
                break;
        }
    }

    private void createGoogleApi() {
        Log.d(TAG, "create API");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    /**Creates the geofencing requests
     * modified from Google GeoLocation sample code
     * avaialable at https://github.com/googlesamples/android-play-location/tree/master/Geofencing
     *
     * @return
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest builder = new GeofencingRequest.Builder()
                .addGeofences(constants.geofenceArrayList)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
        return builder;
    }

    /**Populates the constant geofence list with the places that have not yet been marked as visited
     *
     */
    private void populateGeofenceList() {

        for (PlaceObject place : constants.placeObjects) {
            //Create a geofence object for each place not ticked off as visited
            if (!place.isVisited()) {
                Geofence geofence = (new Geofence.Builder()
                        .setRequestId(place.getDb_key())
                        .setCircularRegion(place.getLatitude(), place.getLongitude(), 100)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build());

                constants.geofenceArrayList.add(geofence);
            }

        }
        int arraylength = constants.geofenceArrayList.size();
        Log.d(TAG, String.valueOf(arraylength));

    }

    //add a request to the monitoring list
    private void addGeofences(GeofencingRequest request) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                pendingIntent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        addGeofences(getGeofencingRequest());

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(this, "Google API Connection Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
