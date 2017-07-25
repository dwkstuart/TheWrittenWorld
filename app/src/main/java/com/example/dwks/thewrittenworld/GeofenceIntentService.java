package com.example.dwks.thewrittenworld;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

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

    public GeofenceIntentService() {
        super("GeofenceIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
                if (geofencingEvent.hasError())
                    return;
        int geofenceTransitionType = geofencingEvent.getGeofenceTransition();
        if (geofenceTransitionType== Geofence.GEOFENCE_TRANSITION_ENTER){
            //get the geofences that were triggered
            List <Geofence>triggeringGeofence = geofencingEvent.getTriggeringGeofences();
            for(Geofence event : triggeringGeofence){
               String triggeredID = event.getRequestId();
                //ID matches DB Key
                PlaceObject placeTriggered = constants.places.get(triggeredID);


            }

    }


    }
}
