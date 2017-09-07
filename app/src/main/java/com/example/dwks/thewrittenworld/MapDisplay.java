package com.example.dwks.thewrittenworld;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;
import java.util.TreeSet;

/**Activity class for displaying the screen with the main map,
 * provides access icons to other functionality and displays selected locations as markers
 */
public class MapDisplay extends AppCompatActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<PlaceObject>,
        View.OnClickListener {


    private static final String TAG = MapDisplay.class.getSimpleName();
    Constants constants = Constants.getInstance();

    private MapFragment mapFragment;
    private GoogleMap map;
    private Toast mToast;
    private ClusterManager<PlaceObject> clusterManager = null;
    private CameraPosition cameraPosition;
    private int ZOOM = 14; //inital zoom

    private ImageButton alertOn;
    private ImageButton alertOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map_display);

        //If map has been loaded before then will reload previous zoom and location on being recreated
        if (savedInstanceState !=null) {
            cameraPosition = (CameraPosition)
                    savedInstanceState.get("MAP_STATE");
        }
        configueButtons();
        initializeGoogleMap();

        FloatingActionButton findLocal = (FloatingActionButton) findViewById(R.id.findLocal);
        findLocal.setOnClickListener(this);
        checkAlertButton();

    }

    /**Sets the overlay on the map to style it, in future versions choice of different styles would be available
     *
     */
    private void setStyle() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.retro_style_overlay));

            if (!success) {
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    /**Helper method to configure all the buttons and icons
     * Sets some of the visibilty depending if user is logged in or not
     *
     */
    private void configueButtons(){
        ImageButton save = (ImageButton) findViewById(R.id.save_files);
        save.setOnClickListener(this);
        alertOn =(ImageButton) findViewById(R.id.set_alerts);
        alertOn.setOnClickListener(this);
        alertOff =(ImageButton) findViewById(R.id.stop_alerts);
        alertOff.setOnClickListener(this);
        ImageButton search = (ImageButton) findViewById(R.id.search_locations);
        search.setOnClickListener(this);
        ImageButton seeList = (ImageButton) findViewById(R.id.location_list);
        seeList.setOnClickListener(this);

        //only registered users can save and load , if not logged in save button hidden
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            save.setVisibility(View.GONE);
        }
        }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG,"Restore instance state");
        if (savedInstanceState == null)
            Log.d(TAG, "saved instance null restore instance");
        if (savedInstanceState !=null) {
            cameraPosition = (CameraPosition)
                    savedInstanceState.get("MAP_STATE");
            Log.d(TAG, "Restore instance state" + cameraPosition.toString());

        }
    }


    /**Method to set the initial zoom centred on user location first time they open the map
     * If location cannot be determined then postion is set as default
     *
     */
    private void setInitalZoom(){
        LatLng currentspot = null;
        Log.d(TAG, "setInitalZoom");

            if (Constants.lastLocation != null) {
                currentspot = new LatLng(Constants.lastLocation.getLatitude(), Constants.lastLocation.getLongitude());
            }
            if (cameraPosition == null) {
                Log.d(TAG, "Camera position is null");

                if(currentspot ==null){
                currentspot = new LatLng(0, 0);
                ZOOM = 1;}


                cameraPosition = new CameraPosition.Builder().target(currentspot).zoom(ZOOM).build();
                Log.d(TAG, cameraPosition.toString());
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
    }

    /**Toggles the notification set/disable buttons visible or not visible depending on notification state
     *
     */
    private void checkAlertButton(){
        if (Constants.notificationsOn){
            alertOff.setVisibility(View.VISIBLE);
            alertOff.setEnabled(true);
            alertOn.setEnabled(false);
            alertOn.setVisibility(View.GONE);
        }
        if (!Constants.notificationsOn && !Constants.placeObjects.isEmpty()){
            alertOff.setVisibility(View.GONE);
            alertOff.setEnabled(false);
            alertOn.setEnabled(true);
            alertOn.setVisibility(View.VISIBLE);
        }
        if(Constants.placeObjects.isEmpty()) {
            alertOn.setVisibility(View.GONE);
            alertOff.setVisibility(View.GONE);
        }
    }

////////Methods to add menu to Action Bar ////////
    private ToolBarMenuHandler toolBarMenuHandler = new ToolBarMenuHandler(this);


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
    return toolBarMenuHandler.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return toolBarMenuHandler.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        invalidateOptionsMenu();
        return toolBarMenuHandler.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        cameraPosition = map.getCameraPosition();
        Log.d(TAG, "On save instance" + cameraPosition.toString());
        outState.putParcelable("MAP_STATE",map.getCameraPosition());

    }

    @Override
    protected void onPause() {
        super.onPause();
        new ProcessSharedPref(this).saveAsJson();
        mapFragment.onPause();
        clusterManager.clearItems();


    }

    @Override
    protected void onResume(){
        super.onResume();
        initializeGoogleMap();
        if(Constants.lastLocation !=null)
            locateNearby();
        checkAlertButton();

    }


    /**Gets Google map fragment
     *
     */
    private void initializeGoogleMap() {

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_display);
        mapFragment.getMapAsync(this);

    }


    /**Sets up the clusters and other map annotations when fragment is returned
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        this.setStyle();
        //only create one instance of cluster manager,
        // if created again in onResume then click listeners don't work on reload and
        //some markers don't disappear correctly on Zoom
        if (clusterManager == null)
        clusterManager =new ClusterManager(this, map);

        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager.getMarkerManager());
        map.setBuildingsEnabled(true);
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);
        map.setOnCameraIdleListener(clusterManager);


        clusterManager.setOnClusterItemInfoWindowClickListener(this);


        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PlaceObject>() {
                    @Override
                    public boolean onClusterItemClick(PlaceObject item) {
                        return false;
                    }
                });

        setInitalZoom();
        if (cameraPosition != null){
            Log.d(TAG, "cam pos not null" + cameraPosition.toString());
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        addMarkers();
    }

    /**Adds cluster items to a fetched map
     *
     */
    private void addMarkers() {
        map.clear();
        clusterManager.clearItems();
        SetIcon setIcon = new SetIcon(this, map, clusterManager);
        setIcon.setMinClusterSize(5);
        clusterManager.setRenderer(setIcon);

        Log.d(TAG, String.valueOf(Constants.placeObjects.size()));

        for (PlaceObject placeObject : Constants.placeObjects) {

            clusterManager.addItem(placeObject);

        }
        clusterManager.cluster();
        if(Constants.geofenceArrayList.size() < Constants.placeObjects.size()){
            alertOn.setEnabled(true);
        }
    }


    /**Handles all the click events for buttons
     *
     * @param view
     */

    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case (R.id.set_alerts):
                {
                GeofenceHandler geofence = new GeofenceHandler(this, "ADD", null);
                geofence.startGeofence();
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(this, "Location Alerts Activated", Toast.LENGTH_LONG);
                mToast.show();
                alertOn.setVisibility(View.GONE);
                alertOn.setEnabled(false);
                    alertOff.setVisibility(View.VISIBLE);
            }
            break;

            case (R.id.stop_alerts):
            {
                GeofenceHandler geofence = new GeofenceHandler(this, "", null);
                geofence.removeAllGeofence();
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(this, "Location Alerts Stopped", Toast.LENGTH_LONG);
                mToast.show();
                alertOff.setVisibility(View.GONE);
                alertOff.setEnabled(false);
                alertOn.setVisibility(View.VISIBLE);
                break;
            }


            case (R.id.location_list):

                Intent currentList = new Intent(getApplicationContext(),PlaceObjectList.class);
                ArrayList<PlaceObject> loadedPlaceList = new ArrayList<>();
                loadedPlaceList.addAll(Constants.placeObjects);
                currentList.putParcelableArrayListExtra("LIST",loadedPlaceList);
                startActivity(currentList);

                break;

            case (R.id.findLocal):
                if(Constants.lastLocation == null){
                            if(mToast!=null)
                                mToast.cancel();
                            mToast= Toast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT);
                            mToast.show();
                            break;
                        }

                        addNearByPlaces();
                        if (nearbyObject.isEmpty()){
                            Log.d(TAG, mToast.toString());
                            if(mToast!=null)
                                mToast.cancel();
                            mToast = Toast.makeText(getApplicationContext(), "Nothing nearby sorry...", Toast.LENGTH_SHORT);
                            mToast.show();
                        }

                break;

            case (R.id.save_files):
                Intent save = new Intent(this, UserFiles.class);
                if (FirebaseAuth.getInstance().getCurrentUser()==null){
                    if(mToast!=null)
                        mToast.cancel();
                    mToast = Toast.makeText(getApplicationContext(),"Log in to save or load", Toast.LENGTH_SHORT);
                    mToast.show();
                    break;
                }
                startActivity(save);
                break;

            case (R.id.search_locations):
                Intent search = new Intent(this, Search.class);
                startActivity(search);
                break;
        }
        checkAlertButton();
    }


    ////////////////////Find Nearby places code /////////////////////
    private TreeSet<PlaceObject> nearbyLong = new TreeSet<>();
    private TreeSet<PlaceObject> nearbyLat = new TreeSet<>();
    private TreeSet<PlaceObject> nearbyObject;


    private void addNearByPlaces(){
        nearbyObject = nearbyLat;
        nearbyObject.retainAll(nearbyLong);

        ///Automatically adds nearby locations to the current list of users chosen locations
        Constants.placeObjects.addAll(nearbyObject);

        for(PlaceObject object: Constants.placeObjects) {
            Constants.places.put(object.getDb_key(),object);
        }

        if(mToast!=null)
            mToast.cancel();

        mToast = Toast.makeText(getApplicationContext(),"Found " + nearbyObject.size() + "  places nearby", Toast.LENGTH_SHORT);
        mToast.show();

        addMarkers();
    }

    /**Access the databases to find locations within a certain radius of the users location
     * Populates two treesets for latitude and longitude locations, by finding interset of the sets, only returns locations within a defined radius
     */
    private void locateNearby(){

        if(mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(getApplicationContext(),"Searching for Nearby Places", Toast.LENGTH_LONG);
        mToast.show();

        Database db = new Database();

        if (Constants.lastLocation !=null) {
            db.nearbyPlacesLatitude(new firebaseDataListener() {
                @Override
                public void onStart() {
                    if (mToast != null)
                        mToast.cancel();
                    Toast.makeText(getApplicationContext(), "Checking Database, Please Wait", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        nearbyLong.add(object);
                    }

                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Log.d(TAG, "Database query failed");
                }

            });

            db.nearbyPlacesLongitude(new firebaseDataListener() {
                @Override
                public void onStart() {}


                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        nearbyLat.add(object);
                    }
                       }

                @Override
                public void onFailed(DatabaseError databaseError) {

                }
            });
        }


    }



    @Override
    public void onClusterItemInfoWindowClick(PlaceObject placeObject) {


        //open details page
        Intent i = new Intent(this, PlaceDetailScreen.class);
        i.putExtra("ClassFrom",MapDisplay.class.toString());
        i.putExtra("Place",placeObject);
        startActivity(i);


    }
    //////////////////////////////////////////////////////////////////////////////////////

    /**Customises the ClusterIcon depending on if the users has marked the location as visited or not
     *
     */
    private class SetIcon extends DefaultClusterRenderer<PlaceObject> {
        @Override
        protected void onBeforeClusterItemRendered(PlaceObject item, MarkerOptions markerOptions) {
            if (item.isVisited()){
            markerOptions.position(item.getLatLng())
                    .title(item.getBookTitle())
                    .snippet("You've been here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));}
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        public SetIcon(Context context, GoogleMap map, ClusterManager<PlaceObject> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<PlaceObject> cluster) {
            return cluster.getSize() > 5; //sets the number of items close together before they are made into a cluster
        }

        @Override
        public void setMinClusterSize(int minClusterSize) {
            super.setMinClusterSize(minClusterSize);
        }

    }
}
