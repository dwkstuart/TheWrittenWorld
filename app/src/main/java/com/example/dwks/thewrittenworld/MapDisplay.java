package com.example.dwks.thewrittenworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
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
    private FloatingActionButton setAlerts;
    private Toast mToast;
    private ClusterManager<PlaceObject> clusterManager = null;
    private CameraPosition cameraPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
       // mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        setupBottomNavBar();
        initializeGoogleMap();
        if (savedInstanceState !=null) {
            cameraPosition = (CameraPosition)
                    savedInstanceState.get("MAP_STATE");
        }

        Log.d(TAG, "onCreate()");
        locateNearby();
        setAlerts = (FloatingActionButton) findViewById(R.id.setAlerts);
        setAlerts.setOnClickListener(this);
        this.checkAlertButton();

    }


//
    private void setInitalZoom(){
        LatLng currentspot = null;
        Log.d(TAG, "setInitalZoom");

            if (constants.lastLocation != null) {
                Log.d(TAG, "Map display user location is not null" + constants.lastLocation.getLatitude());

                currentspot = new LatLng(constants.lastLocation.getLatitude(), constants.lastLocation.getLongitude());
                Log.d(TAG, currentspot.toString());

            }
            if (cameraPosition == null && currentspot!=null) {
                cameraPosition = new CameraPosition.Builder().target(currentspot).zoom(9).build();

                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
    }

    private void checkAlertButton(){
        //TODO see if we can change this when setting alerts elsewhere, using intents
        if (!Constants.geofenceArrayList.isEmpty() || Constants.placeObjects.isEmpty())
            setAlerts.setVisibility(View.INVISIBLE);
        //if the number of alerts set up is lower than the number of items in list then make it possible to set up alerts
        //TODO this will reset all alerts! can we just add new objects
        if (Constants.geofenceArrayList.size() < Constants.placeObjects.size())
            setAlerts.setVisibility(View.VISIBLE);
    }


    private ToolBarMenuHandler toolBarMenuHandler = new ToolBarMenuHandler(this);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return toolBarMenuHandler.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toolBarMenuHandler.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        cameraPosition = map.getCameraPosition();
        outState.putParcelable("MAP STATE",cameraPosition);
        map.clear();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        new ProcessSharedPref(this).saveAsJson();
        mapFragment.onPause();
        map.clear();



    }

    @Override
    protected void onResume(){
        super.onResume();
        initializeGoogleMap();
        this.checkAlertButton();


}

    private void setupBottomNavBar(){
        final Intent lookup = new Intent(this, ChooseAndLoad.class);
        final Intent save = new Intent(this, UserFiles.class);
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.mapBottomNavBar);
        bottomNavMenu.inflateMenu(R.menu.map_bottom_navigation);

        bottomNavMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.displayNearby:
                        if(constants.lastLocation == null){
                            if(mToast!=null)
                                mToast.cancel();
                            mToast.makeText(getApplicationContext(), "Location not available", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        addNearByPlaces();
                        if (nearbyObject.isEmpty()){
                            Log.d(TAG, mToast.toString());
                            if(mToast!=null)
                                mToast.cancel();
                            mToast.makeText(getApplicationContext(), "Nothing nearby sorry...", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.findPlaces:
                        startActivity(lookup);
                        break;
                    case R.id.save_menu:
                        if (FirebaseAuth.getInstance().getCurrentUser()==null){
                            if(mToast!=null)
                                mToast.cancel();
                            mToast = Toast.makeText(getApplicationContext(),"Log in to save or load", Toast.LENGTH_SHORT);
                            mToast.show();
                            break;
                        }
                        startActivity(save);
                        break;

                    case R.id.current_place_list:
                        Intent currentitems = new Intent(getApplicationContext(),ListOfPlaces.class);
                        startActivity(currentitems);
                        break;

                }
                return false;
            }
        });
    }

    private void initializeGoogleMap() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_display);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

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



//        if(constants.lastLocation != null){
//            Log.d(TAG, "Map display user location is not null" + constants.lastLocation.getLatitude());
//
//            LatLng currentspot = new LatLng(constants.lastLocation.getLatitude(),constants.lastLocation.getLongitude());
//            Log.d(TAG,currentspot.toString());
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentspot).zoom(7).build();
//
//            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//        }

        setInitalZoom();
        if (cameraPosition != null){
            Log.d(TAG, "cam pos not null" + cameraPosition.toString());
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        addMarkers();
    }

    private void addMarkers() {
        map.clear();
        //markersCollection.clear();
        //MarkerOptions markerOptions;
       // Log.d(TAG, "add markers");
        SetIcon setIcon = new SetIcon(this, map, clusterManager);
        setIcon.setMinClusterSize(5);
        clusterManager.setRenderer(setIcon);


        for (PlaceObject placeObject : Constants.placeObjects) {

//            markerOptions = new MarkerOptions().
//                    position(placeObject.getLatLng())
//                    .title(placeObject.getBookTitle())
//                    .snippet("You've been here")
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
// ;           setIcon.onBeforeClusterItemRendered(placeObject,markerOptions);
//
            clusterManager.addItem(placeObject);



//            if (!placeObject.isVisited()) {
//                markerOptions = new MarkerOptions().
//                        position(placeObject.getLatLng())
//                        .snippet("You've not visted here")
//                        .title(placeObject.getBookTitle());
//               // Marker marker = map.addMarker(markerOptions);
//               // marker.setTag(placeObject);
//             //   markersCollection.put(marker, placeObject);
//            }
//            else if (placeObject.isVisited()) {
//                Log.d(TAG, "Place is visited add marker loop" + placeObject.getBookTitle());
//                markerOptions = new MarkerOptions().
//                        position(placeObject.getLatLng())
//                        .title(placeObject.getBookTitle())
//                        .snippet("You've been here")
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                //Marker marker = map.addMarker(markerOptions);
                //marker.setTag(placeObject);
              //  markersCollection.put(marker, placeObject);
   //         }

        }
        clusterManager.cluster();
        if(Constants.geofenceArrayList.size() < Constants.placeObjects.size()){
            setAlerts.setVisibility(View.VISIBLE);
        }
    }






    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setAlerts){
        CreateGeofence geofence = new CreateGeofence(this, "ADD", null);
        geofence.startGeofence();
            if(mToast!=null)
                mToast.cancel();
            mToast = Toast.makeText(this, "Location Alerts Activated", Toast.LENGTH_LONG);
            mToast.show();
        setAlerts.setVisibility(View.INVISIBLE);
        }
    }


    ////////////////////Find Nearby places code /////////////////////
    private TreeSet<PlaceObject> nearbyLong = new TreeSet<>();
    private TreeSet<PlaceObject> nearbyLat = new TreeSet<>();
    private TreeSet<PlaceObject> nearbyObject;


    private void addNearByPlaces(){
        nearbyObject = nearbyLat;
        nearbyObject.retainAll(nearbyLong);

        //Log.d(TAG, "Nearby places" + nearbyObject.toString());
        Constants.placeObjects.addAll(nearbyObject);

        for(PlaceObject object: Constants.placeObjects) {
            Constants.places.put(object.getDb_key(),object);
        }
        if(mToast!=null)
            mToast.cancel();

        mToast = Toast.makeText(getApplicationContext(),"Found " + nearbyObject.size() + "  places nearby", Toast.LENGTH_SHORT);
        mToast.show();

        addMarkers();
        //Log.d(TAG, Constants.places.toString());
    }

    private void locateNearby(){

       // Toast.makeText(getApplicationContext(),"Searching for Nearby Places", Toast.LENGTH_LONG).show();

        Database db = new Database();

        if (Constants.getInstance().lastLocation !=null)
        db.nearbyPlacesLatitude(new firebaseDataListener() {
            @Override
            public void onStart() {
                if(mToast!=null)
                    mToast.cancel();
                mToast.makeText(getApplicationContext(),"Checking Database, Please Wait", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceObject object = new PlaceObject(postSnapshot);
                    //Log.d(TAG, object.getBookTitle() + " " + object.getLongitude());
                    nearbyLong.add(object);
                }
                //Log.d(TAG,"Longitude set = " + nearbyLong.toString());

            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }

        });

        db.nearbyPlacesLongitude(new firebaseDataListener() {
            @Override
            public void onStart() {

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


//    @Override
//    public void onInfoWindowClick(PlaceObject placeObject) {
//        Log.d(TAG, "Marker clicked = " + placeObject.getId());
////        Log.d(TAG, "On marker click, collection empty? " + markersCollection.isEmpty());
////        Log.d(TAG, "Marker collection to string" + markersCollection.toString());
////        PlaceObject pickedPlace = (PlaceObject) marker.getTag();
////        // PlaceObject pickedPlace = markersCollection.get(marker);
////        String id = pickedPlace.getDb_key();
//
//        //open details page
//        Intent i = new Intent(this, PlaceDetailScreen.class);
//        i.putExtra("ID", id);
//        startActivity(i);
//
//    }


    @Override
    public void onClusterItemInfoWindowClick(PlaceObject placeObject) {

        Log.d(TAG, "Info window clicked, object details : " + placeObject.toString());
        String id = placeObject.getDb_key();


        //open details page
        Intent i = new Intent(this, PlaceDetailScreen.class);
        i.putExtra("ID", id);
        startActivity(i);
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
