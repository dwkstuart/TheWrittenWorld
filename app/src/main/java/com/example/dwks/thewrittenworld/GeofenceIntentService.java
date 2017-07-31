package com.example.dwks.thewrittenworld;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceIntentService extends IntentService {

    private Constants constants = Constants.getInstance();

    private static final String TAG = "GeofenceTransitionsIS";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
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
                Log.d(TAG,"ID of triggeded event" + triggeredID);
                Log.d(TAG, "Is trigging id in places has map?" + constants.places.containsKey(triggeredID));
                Log.d(TAG, "size of HashMap = " + constants.places.size());
                //ID matches DB Key
                PlaceObject placeTriggered = constants.places.get(triggeredID);
                if (placeTriggered != null)
                Log.d(TAG, "Place Object = " + placeTriggered.toString());
                if (placeTriggered != null){
                Log.d(TAG, "place trigger is not null" + placeTriggered.getBookTitle());
                    //remove triggered fence
                    List<String> remove = new ArrayList<>();
                    remove.add(triggeredID);
                    Log.d(TAG, remove.toString());
                    CreateGeofence geohandler = new CreateGeofence(this.getApplicationContext());
                    geohandler.removeGeofence(remove);

                this.sendNotification(placeTriggered.getBookTitle(), placeTriggered.getDb_key());



                }
                Log.d("Intent Service", "Triggered");

            }

    }


    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails, String ID) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), PlaceDetailScreen.class);
        notificationIntent.putExtra("ID", ID);

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
        builder.setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.

                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText("Test")
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "Entered";
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                return "sitting inside";
            default:
                return "Unknown transition";
        }
    }
}
