package com.example.dwks.thewrittenworld;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**Class to handle all the Firebase queries
 * Created by User on 05/08/2017.
 */

public class Database {


    //Find list of unique titles
    public void getUniqueTitles(final firebaseDataListener listener){
        listener.onStart();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("places/");
        final Query titleQuery = myRef.orderByChild("title");

        titleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               listener.onFailed(databaseError);
            }
        });



    }

    public void getBookPlaces(String title, final firebaseDataListener listener) {
        listener.onStart();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("places/");

        final Query recentQuery = myRef.orderByChild("title").equalTo(title);

        recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    //Cannot search by two where clauses in Firebase, create set of objects with near longitude and latitude and then find where they intersect

    public void nearbyPlacesLongitude(final firebaseDataListener listener) {
        Constants constants = Constants.getInstance();
        Double userLong = constants.lastLocation.getLongitude();

        Double maxLong = userLong + 1;
        Double minLong = userLong - 1;

        listener.onStart();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("places/");
        final Query longitudeQuery = myRef.orderByChild("longitude").startAt(minLong).endAt(maxLong);

        longitudeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }

        });
    }

    public void nearbyPlacesLatitude(final firebaseDataListener listener) {
        Constants constants = Constants.getInstance();
        Double userLat = constants.lastLocation.getLatitude();

        Double maxLat = userLat + 1;
        Double minLat = userLat - 1;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("places/");
        final Query latitudeQuery = myRef.orderByChild("latitude").startAt(minLat).endAt(maxLat);

        latitudeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                listener.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }

        });


    }
}
