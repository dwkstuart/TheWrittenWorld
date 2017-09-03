package com.example.dwks.thewrittenworld;
/*Class to handle the setting up off the Geofencing and Google API
  abstract away from main

 */

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 31/07/2017.
 */

public class GeofenceHandler extends Application implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, ResultCallback<Status> {

    Context context;
    //Geofencing
    private GeofencingApi geofencingApi;
    private PendingIntent pendingIntent;
    private GoogleApiClient googleApiClient;
    private String request_type ="";
    private String removeFence;
    private static final String TAG = GeofenceHandler.class.getSimpleName();
    Constants constants = Constants.getInstance();

    public GeofenceHandler(Context appContext, String request_type, String fenceToBeRemoved) {
    Log.d(TAG,"Create geofences constructed with request type of " + request_type);
        this.request_type = request_type;
        this.removeFence = fenceToBeRemoved;
        context = appContext;
        //initialise APIs
        createGoogleApi();


        geofencingApi = LocationServices.GeofencingApi;
        if(request_type.equals(REMOVE)){
            Log.d(TAG, "Remove request type if called");

            googleApiClient.connect();
        }


    }
    public void startGeofence(){
        pendingIntent = null;
        this.populateGeofenceList();
        Log.d(TAG, "Start geofence");
        this.createGoogleApi();
            if (Constants.geofenceArrayList.size()>0){
                googleApiClient.connect();
            }
            Constants.notificationsOn=true;

        }

    private void createGoogleApi() {
        Log.d(TAG, "create API");
        Log.d(TAG, context.toString());
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**Creates the geofencing requests
     * modified from Google GeoLocation sample code
     * avaialable at https://github.com/googlesamples/android-play-location/tree/master/Geofencing
     *
     * @return
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest builder = new GeofencingRequest.Builder()
                .addGeofences(Constants.geofenceArrayList)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_DWELL)
                .build();
        return builder;
    }

    /**Populates the constant geofence list with the places that have not yet been marked as visited
     *
     */
    private void populateGeofenceList() {

        Log.d(TAG, "Size of fence is " + Float.parseFloat(String.valueOf(R.integer.GEOFENCE_RADIUS)));


        for (PlaceObject place : Constants.placeObjects) {
            //Create a geofence object for each place not ticked off as visited
            if (!place.isVisited()) {
                Geofence geofence = (new Geofence.Builder()
                        .setRequestId(place.getDb_key())
                        .setCircularRegion(place.getLatitude(), place.getLongitude(), 50)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(5000)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build());

                Constants.geofenceArrayList.add(geofence);
                //Need to be able to link a geofence to an object to remove geofences individual from Pending Monitoring list
                Constants.placeObjectGeofenceHashMap.put(place,geofence);
            }

        }

    }



    private PendingIntent getGeofenceIntent(){
        if (pendingIntent != null)
            return pendingIntent;

        Intent intent = new Intent(context, GeofenceIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context,123,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
    //add a request to the monitoring list
    private void addGeofences(GeofencingRequest request) {

        pendingIntent = getGeofenceIntent();

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Permission check is called when app is launched
            return;
        }
        geofencingApi.addGeofences(
                googleApiClient,
                request,
                pendingIntent).setResultCallback(this);

        Log.d(TAG,"geofence added" + request.toString());

    }
    /////////////////////////////////////////////////////////////////////////////////

    public  void removeAllGeofence(){

        if(googleApiClient != null) {
            Log.d(TAG, "Remove fences");
            List<String> removeAll = new ArrayList<>();
            for (Geofence fence : Constants.geofenceArrayList) {
                removeAll.add(fence.getRequestId());
            }
            Log.d(TAG, "Size of list" + Constants.geofenceArrayList.size());
            Constants.geofenceArrayList.clear();
            if(!removeAll.isEmpty() && googleApiClient.isConnected()) {
                geofencingApi.removeGeofences(googleApiClient, removeAll);
            }
            Log.d(TAG, "Should be 0 " + Constants.geofenceArrayList.size());
        }
        Constants.notificationsOn = false;
    }

     public  void removeGeofence(String toBeRemovedFence){
         Log.d(TAG, "Remove fences method call");

         List<String> remove = new ArrayList<>();
         remove.add(toBeRemovedFence);
         Log.d(TAG, "geofence to remove = " + remove.toString());

         if(googleApiClient != null && googleApiClient.isConnected()) {
            Log.d(TAG, "Remove fence called, API client not null");
             if(geofencingApi !=null) {
              Log.d(TAG, "geofencing APi is not null");

                 geofencingApi.removeGeofences(googleApiClient, remove);
             }
        }
    }

    public static final String ADD = "ADD";
    public static final String REMOVE = "REMOVE";


    @Override
    public void onConnected(@Nullable Bundle bundle) {
       Log.d(TAG, "on connected, request type = " + request_type);
        if ( request_type.equals(ADD)) {
           addGeofences(getGeofencingRequest());
           Log.d(TAG, "add geofence called, request type = " + request_type);
       }
       else if (request_type.equals(REMOVE)){
            removeGeofence(removeFence);
            Log.d(TAG, "Remove Geofence " +  removeFence);
        }
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
