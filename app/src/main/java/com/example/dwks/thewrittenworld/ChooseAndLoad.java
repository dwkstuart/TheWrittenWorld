package com.example.dwks.thewrittenworld;
//Class to confirm data to use, calls methods to make geofences and has buttons to launch Map and List View

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingApi;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.TreeSet;

//TODO Decide whether to update to just use GeofencingApiClient

public class ChooseAndLoad extends AppCompatActivity implements View.OnClickListener,
        ResultCallback<Status> {


    private CreateGeofence gfG;
    private final static String TAG = ChooseAndLoad.class.getSimpleName();
    //Buttons and text fields

    private Button deleteFences;
    private Button loadPlacesButton;
    private Button createFenceButton;
    private Button loadMap;
    private Button showList;
    private TextView infoText;
    private Spinner titleDrop;

    private String selectedTitle ="";
   // private ArrayList<PlaceObject> placeObjects;

    //Geofencing
    private GeofencingApi geofencingApi;
    private PendingIntent pendingIntent;
    private GoogleApiClient googleApiClient;
    //get instance of Firebase database to use for queries
    //private FirebaseDatabase database = FirebaseDatabase.getInstance();


    private ArrayList<String> spinnerData = new ArrayList<String>();

//    private TreeSet<PlaceObject> nearbyLong = new TreeSet<PlaceObject>();
//    private TreeSet<PlaceObject> nearbyLat = new TreeSet<PlaceObject>();
//    private TreeSet<PlaceObject> nearbyObject;

    Constants constants = Constants.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_and_load);

        setUpButtons();
        getTitles();
//        pendingIntent = null;
          geofencingApi = LocationServices.GeofencingApi;
        infoText.setText("Number of markers set : " + constants.placeObjects.size());
        //if(constants.lastLocation != null)
        //findNearby();



    }

    /**Gets the distinct book titles on
     *
     */
    private void getTitles(){

        Database db =   new Database();
        db.getUniqueTitles(new firebaseDataListener() {
                @Override
                public void onStart() {
                    Toast.makeText(getApplicationContext(),"Searching Database", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    TreeSet<String> titles = new TreeSet<String>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        PlaceObject object = new PlaceObject(postSnapshot);
                        titles.add(object.getBookTitle());

                    }
                    for (String title:titles){
                        spinnerData.add(title);
                    }

                }

                @Override
                public void onFailed(DatabaseError databaseError) {

                }

        });

        //Set the contents of the drop down
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerData);
        titleDrop.setAdapter(adapter);
        titleDrop.setOnItemSelectedListener(new onItemSelectedListener());

    }

    /**
     * Helper Method to initialise buttons and set listeners
     */
    private void setUpButtons() {

        loadPlacesButton = (Button) findViewById(R.id.loadPlaces);
        createFenceButton = (Button) findViewById(R.id.createGeofences);
        deleteFences =(Button) findViewById(R.id.removeGeofences);
        loadMap = (Button) findViewById(R.id.ViewMap);
        showList = (Button) findViewById(R.id.ViewList);
        infoText = (TextView) findViewById(R.id.InfoBox);
        titleDrop= (Spinner) findViewById(R.id.titleSpinner);
        spinnerData.add("Choose a Book");


        loadPlacesButton.setOnClickListener(this);
        createFenceButton.setOnClickListener(this);
        loadMap.setOnClickListener(this);
        showList.setOnClickListener(this);
        deleteFences.setOnClickListener(this);
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
        showList.setEnabled(false);
        if(constants.placeObjects.isEmpty())
        createFenceButton.setEnabled(false);
        infoText.setText("Places Selected : " + constants.placeObjects.size());



    }

    @Override
    public void onResult(@NonNull Status status) {

    }




    /**Method to return the locations from the databases assocaited with a particular title
     *
     * @param title
     */
    private void findBookPlaces(String title){
          Database db = new Database();
        db.getBookPlaces(title, new firebaseDataListener() {

            @Override
            public void onStart() {
                Toast.makeText(getApplicationContext(),"Loading Selection", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    PlaceObject object = new PlaceObject(postSnapshot);
                    constants.placeObjects.add(object);
                    //populate HashMap
                    constants.places.put(object.getDb_key(), object);

                }
                infoText.setText("Number of Results : " + constants.placeObjects.size());
                if(!constants.placeObjects.isEmpty())
                    createFenceButton.setEnabled(true);
            }

            @Override
            public void onFailed(DatabaseError databaseError) {

            }
        });


    }

    //saves current hasmmap and treeset if app is destroyed


    //Saves data when pause is called in case app is killed in background
    @Override
    protected void onPause() {
        super.onPause();
        ProcessSharedPref processSharedPref = new ProcessSharedPref(this);
        processSharedPref.saveAsJson();
    }

    //saves current hasmmap and treeset if app is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy called");
        ProcessSharedPref processSharedPref = new ProcessSharedPref(this);
        processSharedPref.saveAsJson();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.loadPlaces:

                //this.addNearByPlaces();
                String title = String.valueOf(selectedTitle);
                this.findBookPlaces(title);


                break;

            case R.id.createGeofences:
                Log.d(TAG, "Create geofence button pressed");
                gfG = new CreateGeofence(this.getApplicationContext(), "ADD", null);
                Log.d(TAG, gfG.toString());
                gfG.startGeofence();
//                this.populateGeofenceList();
//                createGoogleApi();
//                if (constants.geofenceArrayList.size()>0){
//                googleApiClient.connect();
//                createFenceButton.setEnabled(false);}
//                else infoText.setText("You've visited everywhere in the list!");
                break;

            case R.id.removeGeofences:
                if (gfG != null) {
                    Log.d(TAG, gfG.toString());
                    gfG.removeAllGeofence();
                }
                break;

            case R.id.ViewMap:
                Intent map = new Intent(this, MapDisplay.class);
                startActivity(map);

                break;

            case R.id.ViewList:
               // TODO temp Intent list = new Intent(this, ListOfPlaces.class);

                Intent save = new Intent(this, UserFiles.class);
                startActivity(save);
                break;
        }


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



//
//    private void createGoogleApi() {
//        Log.d(TAG, "create API");
//        if (googleApiClient == null) {
//            googleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//    }
//
//
//    /**Creates the geofencing requests
//     * modified from Google GeoLocation sample code
//     * avaialable at https://github.com/googlesamples/android-play-location/tree/master/Geofencing
//     *
//     * @return
//     */
//    private GeofencingRequest getGeofencingRequest() {
//        GeofencingRequest builder = new GeofencingRequest.Builder()
//                .addGeofences(constants.geofenceArrayList)
//                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
//                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_DWELL)
//                .build();
//        return builder;
//    }
//
//    /**Populates the constant geofence list with the places that have not yet been marked as visited
//     *
//     */
//    private void populateGeofenceList() {
//
//
//
//        for (PlaceObject place : constants.placeObjects) {
//            //Create a geofence object for each place not ticked off as visited
//            if (!place.isVisited()) {
//                Geofence geofence = (new Geofence.Builder()
//                        .setRequestId(place.getDb_key())
//                        .setCircularRegion(place.getLatitude(), place.getLongitude(), 800)
//                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
//                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
//                        .setLoiteringDelay(5000)
//                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
//                        .build());
//
//                //TODO check if this arraylist needs to be constant
//                constants.geofenceArrayList.add(geofence);
//                //Need to be able to link a geofence to an object to remove geofences individual from Pending Monitoring list
//                constants.placeObjectGeofenceHashMap.put(place,geofence);
//            }
//
//        }
//        int arraylength = constants.geofenceArrayList.size();
//
//    }
//
//
//
//     private PendingIntent getGeofenceIntent(){
//         if (pendingIntent != null)
//             return pendingIntent;
//
//         Intent intent = new Intent(this, GeofenceIntentService.class);
//         PendingIntent pendingIntent = PendingIntent.getService(this,123,intent, PendingIntent.FLAG_UPDATE_CURRENT);
//         return pendingIntent;
//     }
//    //add a request to the monitoring list
//    private void addGeofences(GeofencingRequest request) {
//
//        pendingIntent = getGeofenceIntent();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        //TODO add pending intent creator, add transition class
//        geofencingApi.addGeofences(
//                googleApiClient,
//                request,
//                pendingIntent).setResultCallback(this);
//
//        Log.d(TAG,"geofence added" + request.toString());
//
//    }
//
//    private  void removeAllGeofence(){
//        if(googleApiClient != null) {
//            Log.d(TAG, "Remove fences");
//            List<String> removeAll = new ArrayList<String>();
//            for (Geofence fence : constants.geofenceArrayList) {
//                removeAll.add(fence.getRequestId());
//            }
//            Log.d(TAG, "Size of list" + constants.geofenceArrayList.size());
//            constants.geofenceArrayList.clear();
//            geofencingApi.removeGeofences(googleApiClient, removeAll);
//            Log.d(TAG, "Should be 0 " + constants.geofenceArrayList.size());
//        }
//
//    }
//
//
//
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        addGeofences(getGeofencingRequest());
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//            Toast.makeText(this, "Google API Connection Failed", Toast.LENGTH_LONG).show();
//    }
//
//    @Override
//    public void onResult(@NonNull Status status) {
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//
//    }


    private class onItemSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.d(TAG, "onSpinnerSelected");
            selectedTitle = adapterView.getSelectedItem().toString();


        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
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
