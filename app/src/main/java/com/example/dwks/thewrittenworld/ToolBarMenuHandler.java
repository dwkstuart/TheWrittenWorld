package com.example.dwks.thewrittenworld;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by User on 10/08/2017.
 */

public class ToolBarMenuHandler {
    private Activity activity;

    public ToolBarMenuHandler(Activity activity) {
        this.activity = activity;
    }

    public boolean onPrepareOptionsMenu(Menu menu){


        return true;

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("MENU", "onCreate called");
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.top_bar_menu,menu);

        MenuItem notifyon = menu.findItem(R.id.turn_on_notifications);
        MenuItem notifyOff = menu.findItem(R.id.turn_off_notifications);

        if(Constants.notificationsOn){
            notifyOff.setVisible(true);
            notifyon.setVisible(false);
        }
        else if (!Constants.notificationsOn){
            notifyOff.setVisible(false);
            notifyon.setVisible(true);
        }

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        final Intent alerts = new Intent(activity, ChooseAndLoad.class);
        final Intent currentList = new Intent(activity, ListOfPlaces.class);
        final Intent returnToMap = new Intent (activity, MapDisplay.class);



        switch (item.getItemId()){
            case R.id.main_map_menu:
                returnToMap.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //Uses previous version of activity, maintains users position and zoom
                activity.startActivity(returnToMap);
                break;
            case R.id.location_menu_item:
                activity.startActivity(currentList);
                break;
            case R.id.alerts_menu:
                activity.startActivity(alerts);
                break;
            case R.id.clear_places:
                new CreateGeofence(activity,"",null)
                        .removeAllGeofence();
                Constants.geofenceArrayList.clear();
                Constants.placeObjectGeofenceHashMap.clear();
                Constants.placeObjects.clear();
                Constants.places.clear();

                activity.recreate();
//                if (activity instanceof MapDisplay){
//                 Intent refreshmap = new Intent(activity,MapDisplay.class);
//                    activity.startActivity(refreshmap);
//                    }
                break;
            case R.id.turn_off_notifications:
                new CreateGeofence(activity,"",null)
                        .removeAllGeofence();
              //      Constants.notificationsOn = false;
                break;
            case R.id.turn_on_notifications:
                CreateGeofence geofenceaction = new CreateGeofence(activity,"ADD",null);
                geofenceaction.startGeofence();
               // Constants.notificationsOn = true;
                break;
        }

        return true;
    }

}