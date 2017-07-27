package com.example.dwks.thewrittenworld;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceDetailScreen extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = PlaceDetailScreen.class.getSimpleName();
    private MapFragment mapFragment;
    private GoogleMap map;
    private PlaceObject placeObject;
    private TextView titleText;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail_screen);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.small_map);
        mapFragment.getMapAsync(this);
        checkBox = (CheckBox) findViewById(R.id.visitedCheckBox);


        Intent input = getIntent();
        if (input.hasExtra("ID")) {
            Log.d(TAG, "Has extra ID is true");

            String id = input.getStringExtra("ID");
            Log.d(TAG, "recieved id string =" + id);
            final Constants constants = Constants.getInstance();
            Log.d(TAG, "HashMap empty =  " + constants.places.isEmpty());
            Log.d(TAG, "object exists?  " + constants.places.containsKey(id));
            Log.d(TAG, constants.places.toString());
            placeObject = constants.places.get(id);
            if (placeObject != null) {
                String title = placeObject.getBookTitle();
                titleText = (TextView) findViewById(R.id.place_title);
                titleText.setText(title);
                checkBox.setChecked(placeObject.isVisited());
                Log.d(TAG, "Place visited = " + placeObject.isVisited());

            }
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()){
                        placeObject.setVisited(true);
                    }
                    else if(checkBox.isChecked()== false)
                        placeObject.setVisited(false);
                Log.d(TAG, "On click result, is visited = " + placeObject.isVisited());
                }

            });

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
       //place marker of point of interest and zoom camera
        if(placeObject != null){
        LatLng placeLocation = new LatLng(placeObject.getLatitude(),placeObject.getLongitude());
        map.addMarker(new MarkerOptions().position(placeLocation));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(placeLocation).zoom(10).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));}
    }


}
