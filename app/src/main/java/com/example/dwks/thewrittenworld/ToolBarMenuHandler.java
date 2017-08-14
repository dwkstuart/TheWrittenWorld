package com.example.dwks.thewrittenworld;

import android.app.Activity;
import android.content.Intent;
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


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = activity.getMenuInflater();
        inflater.inflate(R.menu.top_bar_menu,menu);
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
//                if (activity instanceof MapDisplay)
//                activity.startActivity(returnToMap);
                break;
        }
        return true;
    }

}
