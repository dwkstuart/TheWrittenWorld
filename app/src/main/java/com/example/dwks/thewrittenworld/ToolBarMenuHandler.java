package com.example.dwks.thewrittenworld;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**Utitly class that is used to enable easy setting of ActionBar menu in mulitple actvites and handle
 * dynamically setting menu items visiable depenedant on status of application
 * Created by David Stuart on 10/08/2017.
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
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.top_bar_menu,menu);

        //set up menu items
        MenuItem notifyon = menu.findItem(R.id.turn_on_notifications);
        MenuItem notifyOff = menu.findItem(R.id.turn_off_notifications);
        MenuItem save = menu.findItem(R.id.save_menu_item);
        MenuItem logOut = menu.findItem(R.id.log_out_menu_button);

        //Only display notification on/off if there are items to set geofences for

        if (Constants.placeObjects.size()>0) {

            //Determine which notification setting message to display
            if (Constants.notificationsOn) {
                notifyOff.setVisible(true);
                notifyon.setVisible(false);
            } else if (!Constants.notificationsOn) {
                notifyOff.setVisible(false);
                notifyon.setVisible(true);
            }

        }
        else {
            notifyon.setVisible(false);
            notifyOff.setVisible(false);
        }

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            logOut.setVisible(false);
            save.setVisible(false);
        }
            return true;
    }


    /**    set up responses and intents for menu selction
     *
     * @param item menuitem pressed
     * @return
     */
    public boolean onOptionsItemSelected(MenuItem item){

        final Intent alerts = new Intent(activity, Search.class);
        final Intent currentList = new Intent(activity, PlaceObjectList.class);
        final Intent returnToMap = new Intent (activity, MapDisplay.class);
        final Intent saveList = new Intent(activity, UserFiles.class);
        final Intent settings = new Intent(activity,Settings.class);
        final Intent logout = new Intent(activity, MainActivity.class);
        final Intent credits = new Intent(activity, Credits.class);



        switch (item.getItemId()){
            case R.id.main_map_menu:
                returnToMap.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); //Uses previous version of activity, maintains users position and zoom
                activity.startActivity(returnToMap);
                break;
            case R.id.location_menu_item:
                ArrayList<PlaceObject> loadedPlaceList = new ArrayList<>();
                loadedPlaceList.addAll(Constants.placeObjects);
                currentList.putParcelableArrayListExtra("LIST",loadedPlaceList);
                activity.startActivity(currentList);
                break;
            case R.id.alerts_menu:
                activity.startActivity(alerts);
                break;
            case R.id.clear_places:
                new GeofenceHandler(activity,"",null)
                        .removeAllGeofence();
                Constants.geofenceArrayList.clear();
                Constants.placeObjectGeofenceHashMap.clear();
                Constants.placeObjects.clear();
                Constants.places.clear();

                activity.recreate();

                break;
            case R.id.turn_off_notifications:
                new GeofenceHandler(activity,"",null)
                        .removeAllGeofence();
                activity.recreate();
              //      Constants.notificationsOn = false;
                break;
            case R.id.turn_on_notifications:
                GeofenceHandler geofenceaction = new GeofenceHandler(activity,"ADD",null);
                geofenceaction.startGeofence();
                activity.recreate();
               // Constants.notificationsOn = true;
                break;
            case R.id.save_menu_item:
                activity.startActivity(saveList);
                break;

            case R.id.settings_menu_item:

                activity.startActivity(settings);
                break;
            case R.id.log_out_menu_button:

                logout.putExtra("SIGN_OUT", true);
                activity.startActivity(logout);
                break;
            case R.id.credits_menu:

                activity.startActivity(credits);
                break;

        }

        return true;
    }

}
