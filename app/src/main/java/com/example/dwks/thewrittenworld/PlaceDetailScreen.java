package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PlaceDetailScreen extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private static final String TAG = PlaceDetailScreen.class.getSimpleName();
    private PlaceObject placeObject;
    private CheckBox checkBox;
    private Button detailMain;
    private Button information;
   // private String ClassFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail_screen);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.small_map);
        mapFragment.getMapAsync(this);

        checkBox = (CheckBox) findViewById(R.id.visitedCheckBox);
        Log.d(TAG, "Size of places hashmap before on create" + String.valueOf(Constants.places.size()));

        Intent input = getIntent();
        Log.d(TAG,input.toString());
        if (input.hasExtra("Place")) {

            placeObject = input.getParcelableExtra("Place");
            Log.d(TAG, String.valueOf(Constants.placeObjects.contains(placeObject)));
            if (placeObject != null) {

                String title = placeObject.getBookTitle();
                TextView titleText = (TextView) findViewById(R.id.place_title);
                titleText.setText(title);
                TextView locationName = (TextView) findViewById(R.id.details);
                locationName.setText(placeObject.getLocation());
                TextView author = (TextView) findViewById(R.id.author);
                author.setText("Written by " + placeObject.getAuthorName());
                TextView quote = (TextView) findViewById(R.id.detail_quote);
                quote.setText(placeObject.getAssociatedQuote());
                checkBox.setChecked(placeObject.isVisited());

            }
            checkBox.setOnClickListener(this);
            information = (Button) findViewById(R.id.more_info);
            information.setOnClickListener(this);


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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap map = googleMap;

        //noinspection MissingPermission, asked for on starting app
        map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(true);
        map.setMapType(2);
       // setupBottomNavBar();
        //place marker of point of interest and zoom camera
        if(placeObject != null){
            LatLng placeLocation = new LatLng(placeObject.getLatitude(),placeObject.getLongitude());
            map.addMarker(new MarkerOptions().position(placeLocation));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(placeLocation).zoom(18).tilt(35).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));}
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.visitedCheckBox:
                Log.d(TAG, "Size of places hashmap before" + String.valueOf(Constants.places.size()));
            placeObject.setVisited(checkBox.isChecked());
            //Replace the object passed via intent
            Constants.places.remove(placeObject.getDb_key());
            Constants.places.put(placeObject.getDb_key(), placeObject);

                Log.d(TAG, "Size of places hashmap after object removed + added" + String.valueOf(Constants.places.size()));

                Constants.placeObjects.clear();
            for (PlaceObject object : Constants.places.values()) {
                Constants.placeObjects.add(object);
            }

                Log.d(TAG, "Size of places after for loop" + String.valueOf(Constants.places.size()));
                Log.d(TAG, "Size of placesObjects after for loop" + String.valueOf(Constants.placeObjects.size()));

                break;

            case R.id.more_info:
                Intent moreInfo = new Intent(this, PlaceDetailShare.class);
                moreInfo.putExtra("place",placeObject);
                startActivity(moreInfo);
                break;

        }
    }
}
