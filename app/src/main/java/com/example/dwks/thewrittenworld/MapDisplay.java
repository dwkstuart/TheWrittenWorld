package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapDisplay extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener
{


    private static final String TAG = MapDisplay.class.getSimpleName();
    Constants constants = Constants.getInstance();

    private HashMap<Marker, PlaceObject> markersCollection = new HashMap<>();
    private MapFragment mapFragment;
    private GoogleMap map;

//    //For showing user location
//    GoogleApiClient googleApiClient;
//    Location userLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);
        initializeGoogleMap();
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
//        createGoogleApi();
//        googleApiClient.connect();
//        String contents = constants.placeObjects.toString();
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);
        //userLastLocation = map.getMyLocation();


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
        MarkerOptions markerOptions;

        for (PlaceObject placeObject : constants.placeObjects) {
            if (!placeObject.isVisited()) {
                markerOptions = new MarkerOptions().
                        position(placeObject.getLatLng())
                        .title(placeObject.getBookTitle());
                Marker marker = map.addMarker(markerOptions);
                marker.setTag(placeObject);
                markersCollection.put(marker, placeObject);
            }

        }
    }

//    private void findLocation() {
//        Log.d(TAG, "findLocation()");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions();
//            return;
//        }
//        userLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        startLocationUpdates();
//
//    }

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
//        @Override
//        public void onConnected(@Nullable Bundle bundle) {
//               Log.d(TAG, "Connected");
//                findLocation();
//            if(userLastLocation != null){
//                Log.d(TAG, "user location is not null");
//                LatLng currentspot = new LatLng(userLastLocation.getLatitude(),userLastLocation.getLongitude());
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(currentspot).zoom(5).tilt(10).build();
//                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//        }
//
//        @Override
//        public void onConnectionSuspended(int i) {
//
//        }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//    /////////// //////////////////////////////
//    // instance variables for fetching location
//    private LocationRequest locationRequest;
//    private static final int UPDATEINTERVAL = 1000;
//    private static final int FASTESTINTERVAL = 9000;
//
//    private static final int REQ_PERMISSION = 999;
//
//    private void startLocationUpdates() {
//        locationRequest = LocationRequest.create()
//                .setFastestInterval(FASTESTINTERVAL)
//                .setInterval(UPDATEINTERVAL)
//                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "Check self permissions and returns");
//            requestPermissions();
//            return;
//        }
//        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
//
//
//    }
//
//
//
////
////    ////////////////////////////////////////////////////////////
//    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
//
//    /**
//     * Return the current state of the permissions needed.
//     */
//    private boolean checkPermissions() {
//        int permissionState = ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        return permissionState == PackageManager.PERMISSION_GRANTED;
//    }
//
//    private void requestPermissions() {
//        boolean shouldProvideRationale =
//                ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//
//        // Provide an additional rationale to the user. This would happen if the user denied the
//        // request previously, but didn't check the "Don't ask again" checkbox.
//        if (shouldProvideRationale) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
//            showSnackbar(R.string.permission_rationale, android.R.string.ok,
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(MapDisplay.this,
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    });
//        } else {
//            Log.i(TAG, "Requesting permission");
//            // Request permission. It's possible this can be auto answered if device policy
//            // sets the permission in a given state or the user denied the permission
//            // previously and checked "Never ask again".
//            ActivityCompat.requestPermissions(MapDisplay.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    REQUEST_PERMISSIONS_REQUEST_CODE);
//        }
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.length <= 0) {
//                // If user interaction was interrupted, the permission request is cancelled and you
//                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(TAG, "Permission granted.");
//
//            } else {
//                // Permission denied.
//
//                // Notify the user via a SnackBar that they have rejected a core permission for the
//                // app, which makes the Activity useless. In a real app, core permissions would
//                // typically be best requested during a welcome-screen flow.
//
//                // Additionally, it is important to remember that a permission might have been
//                // rejected without asking the user for permission (device policy or "Never ask
//                // again" prompts). Therefore, a user interface affordance is typically implemented
//                // when permissions are denied. Otherwise, your app could appear unresponsive to
//                // touches or interactions which have required permissions.
//                showSnackbar(R.string.permission_denied_explanation, 1,
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                //Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
//            }
//        }
//    }
//
//    /**
//     * Shows a {@link Snackbar}.
//     *
//     * @param mainTextStringId The id for the string resource for the Snackbar text.
//     * @param actionStringId   The text of the action item.
//     * @param listener         The listener associated with the Snackbar action.
//     */
//    private void showSnackbar(final int mainTextStringId, final int actionStringId,
//                              View.OnClickListener listener) {
//        Snackbar.make(
//                findViewById(android.R.id.content),
//                getString(mainTextStringId),
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(getString(actionStringId), listener).show();
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        //Log.d(TAG, "Location changed, Lat= " + location.getLatitude() + " Long = " + location.getLongitude());
//        startLocationUpdates();
//    }
}
