package com.example.dwks.thewrittenworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.TreeSet;

public class FindNearby extends AppCompatActivity implements View.OnClickListener{

    private final static String TAG = FindNearby.class.getSimpleName();
    Constants constants = Constants.getInstance();

    private TreeSet<PlaceObject> nearbyLong = new TreeSet<PlaceObject>();
    private TreeSet<PlaceObject> nearbyLat = new TreeSet<PlaceObject>();
    private TreeSet<PlaceObject> nearbyObject;

    private Button findNearby;
    private TextView numNearby;
    private TextView userName;
    private Button viewMap;
    private Button pickBooks;
    private Button clearSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_nearby);
        this.setButtons();

        if (constants.lastLocation != null){
        this.locateNearby();
            findNearby.setEnabled(true);

        }
        else {
            findNearby.setEnabled(false);
            Toast.makeText(this.getApplicationContext(),"User location not found", Toast.LENGTH_LONG).show();
        }

        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        //if(constants.currentUser != null)
        //userName.setText(constants.currentUser.getDisplayName());
    }

    private void setButtons(){

        findNearby = (Button) findViewById(R.id.findNearbyButton);
        numNearby = (TextView) findViewById(R.id.numPlacesNearby);
        viewMap = (Button) findViewById(R.id.seeMap);
        pickBooks = (Button) findViewById(R.id.findBook);
        userName = (TextView) findViewById(R.id.userName);
        clearSelection = (Button) findViewById(R.id.clearSelection);

        clearSelection.setOnClickListener(this);
        findNearby.setOnClickListener(this);
        viewMap.setOnClickListener(this);
        pickBooks.setOnClickListener(this);


    }



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


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.findNearbyButton:
                Log.d(TAG, "Find nearby button click");
                this.addNearByPlaces();
                numNearby.setText("There are " + nearbyObject.size() + "places nearby");
                break;

            case R.id.seeMap:

                Intent intent = new Intent(this, MapDisplay.class);
                startActivity(intent);
                break;
            case R.id.findBook:

                Intent addbookintent = new Intent(this, ChooseAndLoad.class);
                startActivity(addbookintent);
                break;
            case R.id.clearSelection:
                new CreateGeofence(this.getApplicationContext(),"",null)
                        .removeAllGeofence();
                constants.geofenceArrayList.clear();
                constants.placeObjectGeofenceHashMap.clear();
                constants.placeObjects.clear();
                constants.places.clear();
              break;
        }


    }
}
