package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

public class PlaceDetailScreen extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = PlaceDetailScreen.class.getSimpleName();
    private PlaceObject placeObject;
    private CheckBox checkBox;
   // private String ClassFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail_screen);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.small_map);
        mapFragment.getMapAsync(this);

        ImageView imageView = (ImageView) findViewById(R.id.detailImage);
        checkBox = (CheckBox) findViewById(R.id.visitedCheckBox);


        Intent input = getIntent();
        Log.d(TAG,input.toString());
        if (input.hasExtra("ID")) {
            Log.d(TAG, "Has extra ID is true");
            String id = input.getStringExtra("ID");
            final Constants constants = Constants.getInstance();
            placeObject = Constants.places.get(id);
            Log.d("before if", String.valueOf(placeObject.isVisited()));

            if (placeObject != null) {
                Log.d("TEST!!!!", placeObject.toString());

                String title = placeObject.getBookTitle();
                TextView titleText = (TextView) findViewById(R.id.place_title);
                titleText.setText(title);
                TextView locationName = (TextView) findViewById(R.id.details);
                locationName.setText(placeObject.getLocation());
                TextView author = (TextView) findViewById(R.id.author);
                author.setText("Written by " + placeObject.getAuthorFirstName() + " " + placeObject.getAuthorSecondName());
                TextView quote = (TextView) findViewById(R.id.detail_quote);
                quote.setText(placeObject.getAssociatedQuote());
                checkBox.setChecked(placeObject.isVisited());

            }
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (checkBox.isChecked()){
                        placeObject.setVisited(true);
                    }
                    else if(!checkBox.isChecked())
                        placeObject.setVisited(false);

                }

            });
//            if (input.hasExtra("ClassFrom")){
//                ClassFrom = input.getStringExtra("ClassFrom");
//            }
            //Used for default if DB does not contain any preset image
            String googleStreetViewImage = "https://maps.googleapis.com/maps/api/streetview?size=600x300&location="+ placeObject.getLatitude()+"," +placeObject.getLongitude()+"&heading=151.78&pitch=-0.76&key=" + getString(R.string.GOOGLE_API_KEY);

            Glide.with(getApplicationContext()).load(googleStreetViewImage).into(imageView);
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
        setupBottomNavBar();
        //place marker of point of interest and zoom camera
        if(placeObject != null){
            LatLng placeLocation = new LatLng(placeObject.getLatitude(),placeObject.getLongitude());
            map.addMarker(new MarkerOptions().position(placeLocation));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(placeLocation).zoom(18).tilt(35).build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));}
    }


    private void setupBottomNavBar(){
        final Intent lookup = new Intent(this, ChooseAndLoad.class);
        final Intent save = new Intent(this, UserFiles.class);
        final Intent returnToMap = new Intent (this, MapDisplay.class);
        BottomNavigationView bottomNavMenu = (BottomNavigationView) findViewById(R.id.mapBottomNavBar);
        bottomNavMenu.inflateMenu(R.menu.map_bottom_navigation);

        bottomNavMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.displayNearby:
                        startActivity(returnToMap);
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

//    @Override
//    public void onBackPressed() {
//        if (ClassFrom != null){
//            Intent back = new Intent(this, MapDisplay.class);
//            startActivity(back);
//        }
//        super.onBackPressed();
//
//    }
}
