package com.example.dwks.thewrittenworld;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * Adapted from GoogleSamples https://github.com/googlesamples/android-play-location/tree/master/Geofencing
 *
 * Also handles the location update services so Location Listener does not have to be created twice
 */
@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)

public class GeofenceIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GeofenceTransitionsIS";
    //public static final String ADD = "ADD";
    private static final String REMOVE = "REMOVE";

    private GoogleApiClient googleApiClient;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceIntentService() {
        super(TAG);
    }



    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

        Log.d(TAG,"Service started");
        createGoogleApi();
        googleApiClient.connect();
        Context context = this.getApplicationContext();
    }


    private void createGoogleApi() {
        Log.d(TAG, "create API");
        Log.d(TAG, this.toString());
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
                if (geofencingEvent.hasError())
                    return;
        int geofenceTransitionType = geofencingEvent.getGeofenceTransition();
        if (geofenceTransitionType== Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransitionType == Geofence.GEOFENCE_TRANSITION_DWELL){
            //get the geofences that were triggered
            List <Geofence>triggeringGeofence = geofencingEvent.getTriggeringGeofences();
            for(Geofence event : triggeringGeofence){
               String triggeredID = event.getRequestId();

                PlaceObject placeTriggered = Constants.places.get(triggeredID);
                if (placeTriggered != null){

                    new GeofenceHandler(this.getApplicationContext(),REMOVE, triggeredID);

                this.sendNotification(placeTriggered.getBookTitle(), placeTriggered.getDb_key());


                }

            }

    }


    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails, String ID) {
        // Create an explicit content Intent that starts the PlaceDetailScreen activity.
        Intent notificationIntent = new Intent(getApplicationContext(), PlaceDetailScreen.class);
        notificationIntent.putExtra("ID", ID); //puts the ID for the PlaceObject
        PlaceObject placeTriggered = Constants.places.get(ID); //Puts the object as a bundable extra
        notificationIntent.putExtra("Place", placeTriggered);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.book_outlint_marker)
                .setContentTitle(notificationDetails)
                .setContentText(placeTriggered.getLocation())
                .setContentIntent(notificationPendingIntent
                );

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    /////////////////////////////////Sets up location tracking details ////////////////////////////////

    private static final int UPDATEINTERVAL = 20000;
    private static final int FASTESTINTERVAL = 15000;

    private static final int REQ_PERMISSION = 999;

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setFastestInterval(FASTESTINTERVAL)
                .setInterval(UPDATEINTERVAL)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }


    @Override
    public void onLocationChanged(Location location) {
        startLocationUpdates();
    }


    private void findLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           // Permissions are requested on start up
            return;
        }
        Constants.lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        startLocationUpdates();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        findLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }




}
