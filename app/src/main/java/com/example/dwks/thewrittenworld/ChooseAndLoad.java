package com.example.dwks.thewrittenworld;
//Class to confirm data to use, calls methods to make geofences and has buttons to launch Map and List View

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//TODO Decide whether to update to just use GeofencingApiClient

public class ChooseAndLoad extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener, ResultCallback<Status> {



    private final static String TAG = ChooseAndLoad.class.getSimpleName();
    //Buttons and text fields

    private Button loadPlacesButton;
    private Button createFenceButton;
    private Button loadMap;
    private Button showList;
    private EditText listName;
    private TextView infoText;

   // private ArrayList<PlaceObject> placeObjects;

    //Geofencing
    private GeofencingApi geofencingApi;
    private PendingIntent pendingIntent;
    private GoogleApiClient googleApiClient;

    Constants constants = Constants.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_and_load);

        setUpButtons();
        pendingIntent = null;
        geofencingApi = LocationServices.GeofencingApi;

    }


    /**
     * Helper Method to initialise buttons and set listeners
     */
    private void setUpButtons() {

        loadPlacesButton = (Button) findViewById(R.id.loadPlaces);
        createFenceButton = (Button) findViewById(R.id.createGeofences);
        loadMap = (Button) findViewById(R.id.ViewMap);
        showList = (Button) findViewById(R.id.ViewList);
        listName = (EditText) findViewById(R.id.enterListName);
        infoText = (TextView) findViewById(R.id.InfoBox);


        loadPlacesButton.setOnClickListener(this);
        createFenceButton.setOnClickListener(this);
        loadMap.setOnClickListener(this);
        showList.setOnClickListener(this);
        showList.setEnabled(false);
        createFenceButton.setEnabled(false);


    }

    class LoadAysncTask extends AsyncTask<String, Integer, Boolean>{
        Constants constants = Constants.getInstance();
        String searchTerm;
        FirebaseDatabase database;
        DatabaseReference myRef;

        public LoadAysncTask(String title) {
        this.searchTerm = title;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            myRef = database.getReference("places/");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(String... strings) {



            final Query recentQuery = myRef.orderByChild("title").equalTo(searchTerm);

            recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   int counter =0;
                    //Log.d("Data snapshot =", dataSnapshot.toString());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        Log.d(TAG, object.getDb_key());
                        constants.placeObjects.add(object);
                        //populate HashMap
                        constants.places.put(object.getDb_key(), object);
                    }
                    infoText.setText("Number of Results : " + constants.placeObjects.size());
                    Log.d(TAG, "Size of array : " + constants.placeObjects.size());
                    createFenceButton.setEnabled(true);
                    showList.setEnabled(true);


                }



                @Override
                public void onCancelled(DatabaseError databaseError) {
                    ;
                }

            });

            Log.d(TAG,"On background finished");
            return true;


        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean) {


                //infoText.setText("Number of places found related to " + searchTerm + " = " + constants.placeObjects.size());
            }
        }


    }

    private void loadPlaces(String booktitle) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        final ArrayList<PlaceObject> tempList = new ArrayList<>();
        DatabaseReference myRef = database.getReference("places/");

        Query recentQuery =myRef.orderByChild("title").equalTo(booktitle);
        recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("Data snapshot =", dataSnapshot.toString());
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        PlaceObject object = new PlaceObject(postSnapshot);
                    Log.d(TAG, object.getDb_key());
                    tempList.add(object);
                    //populate HashMap
                    constants.places.put(object.getDb_key(),object);

                }
                constants.placeObjects = tempList;
                Log.d(TAG, "Size of array : " + constants.placeObjects.size());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        //String placesJson = this.assestJsonFile();
//        constants.placeObjects = new PlacesListCreator(placesJson)
//                .getPointOfInterestObjects();
    }

//

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadPlaces:

                String title = String.valueOf(listName.getText());

                new LoadAysncTask(title).execute();

//                this.loadPlaces(title);
//
//
//                createFenceButton.setEnabled(true);
//                showList.setEnabled(true);
////                listName.setEnabled(false);
////                loadPlacesButton.setEnabled(false);


                break;

            case R.id.createGeofences:
                this.populateGeofenceList();
                createGoogleApi();
                googleApiClient.connect();
                createFenceButton.setEnabled(false);
                break;

            case R.id.ViewMap:
                Intent  map = new Intent(this, MapDisplay.class);
                startActivity(map);
                break;

            case R.id.ViewList:
                Intent list = new Intent(this, ListOfPlaces.class);
                startActivity(list);
                break;
        }
    }

    private void createGoogleApi() {
        Log.d(TAG, "create API");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    /**Creates the geofencing requests
     * modified from Google GeoLocation sample code
     * avaialable at https://github.com/googlesamples/android-play-location/tree/master/Geofencing
     *
     * @return
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest builder = new GeofencingRequest.Builder()
                .addGeofences(constants.geofenceArrayList)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_DWELL)
                .build();
        return builder;
    }

    /**Populates the constant geofence list with the places that have not yet been marked as visited
     *
     */
    private void populateGeofenceList() {



        for (PlaceObject place : constants.placeObjects) {
            //Create a geofence object for each place not ticked off as visited
            if (!place.isVisited()) {
                Geofence geofence = (new Geofence.Builder()
                        .setRequestId(place.getDb_key())
                        .setCircularRegion(place.getLatitude(), place.getLongitude(), 500)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                        .setLoiteringDelay(10000)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .build());

                constants.geofenceArrayList.add(geofence);
            }

        }
        int arraylength = constants.geofenceArrayList.size();
        Log.d(TAG, String.valueOf(arraylength));

    }

    //add a request to the monitoring list
    private void addGeofences(GeofencingRequest request) {

        Intent intent = new Intent(this, GeofenceIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this,123,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //TODO add pending intent creator, add transition class
        LocationServices.GeofencingApi.addGeofences(
                googleApiClient,
                request,
                pendingIntent).setResultCallback(this);
    }

//    private void createIntents(){
//        Intent intent = new Intent(this, GeofenceIntentService.class);
//        PendingIntent pendingIntent = PendingIntent.getService(this,123,intent, PendingIntent.FLAG_UPDATE_CURRENT);
//    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        addGeofences(getGeofencingRequest());

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Toast.makeText(this, "Google API Connection Failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    //TODO remove if not using assest load
//    public void populateHashMap() {
//            Log.d(TAG, "HashMap populated");
//        for (PlaceObject placeObject : constants.placeObjects) {
//                    constants.places.put(placeObject.getDb_key(),placeObject);
//            Log.d(TAG, "HashMap populated for loop");
//        }
//    }

    //LOAD FROM ASSEST WILL BE REPLACED WITH SOME METHOD FOR DATABASE LOADING////
//    private String assestJsonFile() {
//        String json = null;
//        try {
//            InputStream is = getAssets().open("dickensJSON");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, "UTF-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//        return json;
//    }
}
