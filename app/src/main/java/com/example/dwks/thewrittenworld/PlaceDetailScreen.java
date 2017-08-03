package com.example.dwks.thewrittenworld;

        import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    private TextView locationName;
    private CheckBox checkBox;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail_screen);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.small_map);
        mapFragment.getMapAsync(this);

        imageView =(ImageView) findViewById(R.id.detailImage);
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
                locationName = (TextView) findViewById(R.id.details);
                locationName.setText(placeObject.getLocation());
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

            //Used for default if DB does not contain any preset image
            String googleStreetViewImage = "https://maps.googleapis.com/maps/api/streetview?size=600x300&location="+ placeObject.getLatitude()+"," +placeObject.getLongitude()+"&heading=151.78&pitch=-0.76&key=" + getString(R.string.GOOGLE_API_KEY);

            Glide.with(getApplicationContext()).load(googleStreetViewImage).into(imageView);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //noinspection MissingPermission, asked for on starting app
        map.setMyLocationEnabled(true);
        //place marker of point of interest and zoom camera
        if(placeObject != null){
            LatLng placeLocation = new LatLng(placeObject.getLatitude(),placeObject.getLongitude());
            map.addMarker(new MarkerOptions().position(placeLocation));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(placeLocation).zoom(10).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));}
    }


}
