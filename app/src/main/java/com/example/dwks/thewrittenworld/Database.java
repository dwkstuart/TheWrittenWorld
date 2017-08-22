package com.example.dwks.thewrittenworld;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

    private FirebaseDatabase database;
    static boolean peristanceEnabledCalled = false;
    private static final String TAG = Database.class.getSimpleName();



    public Database() {
        Log.d("DATABASE", "Database instance created");
        if(!peristanceEnabledCalled) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            peristanceEnabledCalled = true;
        }
        database= FirebaseDatabase.getInstance();
    }

    //Find list of unique titles
    public void getUniqueTitles(final firebaseDataListener listener){
        listener.onStart();

       // FirebaseDatabase database = FirebaseDatabase.getInstance();
      //database.setPersistenceEnabled(true);
        DatabaseReference myRef = database.getReference("places/");
        myRef.keepSynced(true);
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

    //Find list of authors titles
    public void getAuthors(final firebaseDataListener listener){
        listener.onStart();


        DatabaseReference myRef = database.getReference("places/");
        myRef.keepSynced(true);
        final Query titleQuery = myRef.orderByChild("author");

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

      //  FirebaseDatabase database = FirebaseDatabase.getInstance();
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

    public void getBooksByAuthor(String author, final firebaseDataListener listener) {
        listener.onStart();
        Log.d(TAG,author);
        //  FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("places/");

        final Query recentQuery = myRef.orderByChild("author").equalTo(author);

        recentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               // Log.d(TAG, "Books by author data snapshot?" +dataSnapshot.toString());
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

        Double maxLong = userLong + 0.03;
        Double minLong = userLong - 0.03;

        listener.onStart();

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
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


        Double maxLat = userLat + 0.01;
        Double minLat = userLat - 0.01;

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
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

    public void uploadSaveSelection(String name, String jsonfile){

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Constants constants = Constants.getInstance();


        DatabaseReference mRef = database.getReference();
        DatabaseReference childRef = mRef.child("user");
        DatabaseReference userID = childRef.child(UID);
        userID.child(name).setValue(jsonfile);



    }

    public void getUsersLists(final firebaseDataListener listener){

        Constants constants = Constants.getInstance();
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user/" + UID);

        final Query usersLists = myRef;
        Log.d("userlist query", myRef.toString());

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void uploadLocation (final firebaseDataListener listener){

        DatabaseReference mRef = database.getReference();
        final DatabaseReference childRef = mRef.child("places");
        //Query to determine the number of items in the array
        final Query query = mRef.orderByKey().startAt("places").limitToFirst(1);

        final ChildEventListener eventListener;
        query.addChildEventListener(eventListener =new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listener.onSuccess(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });



    }

    public void loadInfo(PlaceObject placeObject, int arrayPos){

        Log.d("Load", "load info called");
        DatabaseReference mRef = database.getReference();
        DatabaseReference childRef = mRef.child("places");
        DatabaseReference places = childRef.child(String.valueOf(arrayPos));
        places.child("author").setValue(placeObject.getAuthorName());
        places.child("db_key").setValue(places.push().getKey());
        places.child("location").setValue(placeObject.getLocation());
        places.child("latitude").setValue(placeObject.getLatitude());
        places.child("longitude").setValue(placeObject.getLongitude());
//                places.child("snippet").setValue(placeObject.getSnippet());
        places.child("description").setValue(placeObject.getLongDescription());
        places.child("title").setValue(placeObject.getBookTitle());
        places.child("quote").setValue(placeObject.getAssociatedQuote());

    }

}
