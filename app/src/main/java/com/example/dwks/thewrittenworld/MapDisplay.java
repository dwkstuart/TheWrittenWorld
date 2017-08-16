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

import java.util.TreeSet;

public class MapDisplay extends AppCompatActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener<PlaceObject>,
        View.OnClickListener {


    private static final String TAG = MapDisplay.class.getSimpleName();
    Constants constants = Constants.getInstance();

   // private HashMap<Marker, PlaceObject> markersCollection = new HashMap<>();
    private MapFragment mapFragment;
    private GoogleMap map;
    private FloatingActionButton findLocal;
    private Toast mToast;
    private ClusterManager<PlaceObject> clusterManager = null;
    private CameraPosition cameraPosition;
    private int ZOOM = 14; //inital zoom

    ///Buttons
    ImageButton save;
    ImageButton search;
    ImageButton alerts;
    ImageButton seeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
       // mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
       // setupBottomNavBar();
        Log.d(TAG, "onCreate()");
        if(savedInstanceState == null)
            Log.d(TAG, "Saved instance state null");

        if (savedInstanceState !=null) {
            cameraPosition = (CameraPosition)
                    savedInstanceState.get("MAP_STATE");
            Log.d(TAG, "Create instance state" + cameraPosition.toString());

        }
        configueButtons();
        initializeGoogleMap();
        if(Constants.getInstance().lastLocation !=null)
        locateNearby();
        findLocal = (FloatingActionButton) findViewById(R.id.findLocal);
        findLocal.setOnClickListener(this);
        this.checkAlertButton();
        Log.d(TAG, "Notify Button State  " + alerts.isEnabled());
        checkAlertButton();
        switchAlertButton();

    }

    private void switchAlertButton(){
        if(Constants.notificationsOn){
            Log.d(TAG, "ALerts is enable, should change icon");
            alerts.setImageResource(R.drawable.alert_bell);
        }
        else if (!Constants.notificationsOn){
            alerts.setImageResource(R.drawable.checked_circle);
        }

    }
    private void setStyle() {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.retro_style_overlay));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private void configueButtons(){
        save = (ImageButton) findViewById(R.id.save_files);
        save.setOnClickListener(this);
        alerts =(ImageButton) findViewById(R.id.set_alerts);
        alerts.setOnClickListener(this);
        search= (ImageButton) findViewById(R.id.search_locations);
        search.setOnClickListener(this);
        seeList =(ImageButton) findViewById(R.id.location_list);
        seeList.setOnClickListener(this);
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

    //
    private void setInitalZoom(){
        LatLng currentspot = null;
        Log.d(TAG, "setInitalZoom");




            if (constants.lastLocation != null) {
               // Log.d(TAG, "Map display user location is not null" + constants.lastLocation.getLatitude());

                currentspot = new LatLng(constants.lastLocation.getLatitude(), constants.lastLocation.getLongitude());
             //   Log.d(TAG, currentspot.toString());

            }
            if (cameraPosition == null) {
                Log.d(TAG, "Camera position is null");

                //for case when user first starts app and currentspot and location service are both null
                if(currentspot ==null){
                currentspot = new LatLng(0, 0);
                ZOOM = 1;}


                cameraPosition = new CameraPosition.Builder().target(currentspot).zoom(ZOOM).build();
                Log.d(TAG, cameraPosition.toString());
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
    }


    private void checkAlertButton(){
        //TODO see if we can change this when setting alerts elsewhere, using intents
        if (!Constants.geofenceArrayList.isEmpty() || Constants.placeObjects.isEmpty())
        Constants.notificationsOn = false;
        //if the number of alerts set up is lower than the number of items in list then make it possible to set up alerts
        //TODO this will reset all alerts! can we just add new objects
        if (Constants.geofenceArrayList.size() < Constants.placeObjects.size())
            Constants.notificationsOn = true;
    }


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
        switchAlertButton();


}


    private void initializeGoogleMap() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_display);
        mapFragment.getMapAsync(this);
    }


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

    private void addMarkers() {
        map.clear();
        clusterManager.clearItems();
        SetIcon setIcon = new SetIcon(this, map, clusterManager);
        setIcon.setMinClusterSize(5);
        clusterManager.setRenderer(setIcon);

        Log.d(TAG, String.valueOf(Constants.placeObjects.size()));

        for (PlaceObject placeObject : Constants.placeObjects) {
            Log.d(TAG, "MARKER ADDED" + placeObject.getBookTitle());
            clusterManager.addItem(placeObject);

        }
        clusterManager.cluster();
        if(Constants.geofenceArrayList.size() < Constants.placeObjects.size()){
            alerts.setEnabled(true);
        }
    }






    @Override
    public void onClick(View view) {


        switch (view.getId()) {
            case (R.id.set_alerts):
                {
                CreateGeofence geofence = new CreateGeofence(this, "ADD", null);
                geofence.startGeofence();
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(this, "Location Alerts Activated", Toast.LENGTH_LONG);
                mToast.show();
                alerts.setEnabled(false);
            }
            break;

            case (R.id.location_list):

                Intent currentitems = new Intent(getApplicationContext(),ListOfPlaces.class);
                        startActivity(currentitems);

                break;

            case (R.id.findLocal):
                if(constants.lastLocation == null){
                            if(mToast!=null)
                                mToast.cancel();
                            mToast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT);
                            mToast.show();
                            break;
                        }

                        addNearByPlaces();
                        if (nearbyObject.isEmpty()){
                            Log.d(TAG, mToast.toString());
                            //if(mToast!=null)
                            //    mToast.cancel();
                            Toast.makeText(getApplicationContext(), "Nothing nearby sorry...", Toast.LENGTH_SHORT).show();
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
                Intent search = new Intent(this, ChooseAndLoad.class);
                startActivity(search);
                break;
        }
        checkAlertButton();
        switchAlertButton();

    }


    ////////////////////Find Nearby places code /////////////////////
    private TreeSet<PlaceObject> nearbyLong = new TreeSet<>();
    private TreeSet<PlaceObject> nearbyLat = new TreeSet<>();
    private TreeSet<PlaceObject> nearbyObject;


    private void addNearByPlaces(){
        nearbyObject = nearbyLat;
        nearbyObject.retainAll(nearbyLong);

        Log.d(TAG, "Nearby places" + nearbyObject.toString());
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

    private void locateNearby(){
        Log.d(TAG, "Locate nearby method called");

        Toast.makeText(getApplicationContext(),"Searching for Nearby Places", Toast.LENGTH_LONG).show();

        Database db = new Database();

        if (Constants.getInstance().lastLocation !=null) {
            db.nearbyPlacesLatitude(new firebaseDataListener() {
                @Override
                public void onStart() {
                    if (mToast != null)
                        mToast.cancel();
                    mToast.makeText(getApplicationContext(), "Checking Database, Please Wait", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        Log.d(TAG, object.getBookTitle() + " " + object.getLongitude());
                        nearbyLong.add(object);
                    }
                    //Log.d(TAG,"Longitude set = " + nearbyLong.toString());

                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Log.d(TAG, "Database query failed");
                }

            });

            db.nearbyPlacesLongitude(new firebaseDataListener() {
                @Override
                public void onStart() {
                    Log.d(TAG, "long search start");
                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        nearbyLat.add(object);
                        //Log.d(TAG, object.getBookTitle() + "latitude = " + object.getLatitude());
                    }
                    // Log.d(TAG,"Latitude set = " + nearbyLat.toString());
                }

                @Override
                public void onFailed(DatabaseError databaseError) {

                }
            });
        }


    }



    @Override
    public void onClusterItemInfoWindowClick(PlaceObject placeObject) {

        Log.d(TAG, "Info window clicked, object details : " + placeObject.toString());
        String id = placeObject.getDb_key();


        //open details page
        Intent i = new Intent(this, PlaceDetailScreen.class);
        i.putExtra("ID", id);
        i.putExtra("ClassFrom",MapDisplay.class.toString());
        startActivity(i);
        finish(); //destroy map activity so will be redrawn on a back press

    }
    //////////////////////////////////////////////////////////////////////////////////////


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
            return cluster.getSize() > 10;
        }

        @Override
        public void setMinClusterSize(int minClusterSize) {
            super.setMinClusterSize(minClusterSize);
        }

    }
}
