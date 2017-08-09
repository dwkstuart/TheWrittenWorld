package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.TreeSet;

public class MapDisplay extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        View.OnClickListener
{


    private static final String TAG = MapDisplay.class.getSimpleName();
    Constants constants = Constants.getInstance();

    private HashMap<Marker, PlaceObject> markersCollection = new HashMap<>();
    private MapFragment mapFragment;
    private GoogleMap map;
    private BottomNavigationView bottomNavMenu;
    private FloatingActionButton setAlerts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
        setupBottomNavBar();
        initializeGoogleMap();

        Log.d(TAG, "onCreate()");


        setAlerts = (FloatingActionButton) findViewById(R.id.setAlerts);
        setAlerts.setOnClickListener(this);
        //TODO see if we can change this when setting alerts elsewhere, using intents
        if(!constants.geofenceArrayList.isEmpty() || constants.placeObjects.isEmpty())
            setAlerts.setVisibility(View.INVISIBLE);
        //if no alerts are set up and there are selected places
        if(constants.geofenceArrayList.isEmpty() && !constants.placeObjects.isEmpty())
            setAlerts.setVisibility(View.VISIBLE);

    }


    private void setupBottomNavBar(){
        final Intent lookup = new Intent(this, ChooseAndLoad.class);
        final Intent save = new Intent(this, UserFiles.class);
        bottomNavMenu =(BottomNavigationView) findViewById(R.id.mapBottomNavBar);
        bottomNavMenu.inflateMenu(R.menu.map_bottom_navigation);

        bottomNavMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.displayNearby:
                        locateNearby();
                        addNearByPlaces();
                        break;
                    case R.id.findPlaces:
                        startActivity(lookup);
                        break;
                    case R.id.save_menu:
                        if (FirebaseAuth.getInstance().getCurrentUser()==null){
                            Toast.makeText(getApplicationContext(),"Log in to save or load", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        startActivity(save);
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
    public void onMapClick(LatLng latLng) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.d(TAG, "Marker clicked = " + marker.getId());
        Log.d(TAG, "On marker click, collection empty? " + markersCollection.isEmpty());
        Log.d(TAG, "Marker collection to string" + markersCollection.toString());
        PlaceObject pickedPlace = markersCollection.get(marker);
        String id = pickedPlace.getDb_key();

        //open details page
        Intent i = new Intent(this, PlaceDetailScreen.class);
        i.putExtra("ID", id);
        startActivity(i);
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        map.setBuildingsEnabled(true);
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);

        if(constants.lastLocation != null){
            Log.d(TAG, "Map display user location is not null" + constants.lastLocation.getLatitude());

            LatLng currentspot = new LatLng(constants.lastLocation.getLatitude(),constants.lastLocation.getLongitude());
            Log.d(TAG,currentspot.toString());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(currentspot).zoom(7).build();

            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }
        addMarkers();
    }

    private void addMarkers() {
        markersCollection.clear();
        MarkerOptions markerOptions;
        Log.d(TAG, "add markers");

        for (PlaceObject placeObject : constants.placeObjects) {
            if (!placeObject.isVisited()) {
                markerOptions = new MarkerOptions().
                        position(placeObject.getLatLng())
                        .title(placeObject.getBookTitle());
                Marker marker = map.addMarker(markerOptions);
                marker.setTag(placeObject);
                markersCollection.put(marker, placeObject);
            }
            else if (placeObject.isVisited()) {
                Log.d(TAG, "Place is visited add marker loop" + placeObject.getBookTitle());
                markerOptions = new MarkerOptions().
                        position(placeObject.getLatLng())
                        .title(placeObject.getBookTitle())
                        .rotation(90);
                Marker marker = map.addMarker(markerOptions);
                marker.setTag(placeObject);
                markersCollection.put(marker, placeObject);
            }

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.setAlerts){
        CreateGeofence geofence = new CreateGeofence(this, "ADD", null);
        geofence.startGeofence();
            Toast.makeText(this, "Location Alerts Activated", Toast.LENGTH_LONG).show();
        setAlerts.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    ////////////////////Find Nearby places code /////////////////////
    private TreeSet<PlaceObject> nearbyLong = new TreeSet<PlaceObject>();
    private TreeSet<PlaceObject> nearbyLat = new TreeSet<PlaceObject>();
    private TreeSet<PlaceObject> nearbyObject;


    private void addNearByPlaces(){
        nearbyObject = nearbyLat;
        nearbyObject.retainAll(nearbyLong);
        Toast.makeText(getApplicationContext(),"Found " + nearbyObject.size() + "  places nearby", Toast.LENGTH_LONG).show();

        Log.d(TAG, "Nearby places" + nearbyObject.toString());
        constants.placeObjects.addAll(nearbyObject);

        for(PlaceObject object:constants.placeObjects) {
            constants.places.put(object.getDb_key(),object);
        }
        Log.d(TAG,constants.places.toString());
    }

    private void locateNearby(){

        Toast.makeText(getApplicationContext(),"Searching for Nearby Places", Toast.LENGTH_LONG).show();

        Database db = new Database();

        db.nearbyPlacesLatitude(new firebaseDataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceObject object = new PlaceObject(postSnapshot);
                    Log.d(TAG, object.getBookTitle() + " " + object.getLongitude());
                    nearbyLong.add(object);
                }
                Log.d(TAG,"Longitude set = " + nearbyLong.toString());
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
                    Log.d(TAG, object.getBookTitle() + "latitude = " + object.getLatitude());
                }
                Log.d(TAG,"Latitude set = " + nearbyLat.toString());
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });


    }


}
