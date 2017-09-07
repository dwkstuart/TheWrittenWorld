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

/**
 * Class to handle all the Firebase queries
 * Created by User on 05/08/2017.
 */

public class Database {

    private FirebaseDatabase database;
    private static boolean peristanceEnabledCalled = false;
    private static final String TAG = Database.class.getSimpleName();


    public Database() {
        //Persistance enabled can only be set on first calling of Database
        if (!peristanceEnabledCalled) {

            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            peristanceEnabledCalled = true;
        }
        database = FirebaseDatabase.getInstance();
    }

    //Find list of unique titles in the databse
    public void getUniqueTitles(final firebaseDataListener listener) {

        listener.onStart();

        DatabaseReference myRef = database.getReference("places/");
        myRef.keepSynced(true);
        final Query titleQuery = myRef.orderByChild("title");

        titleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Listener returns data to calling method
                listener.onSuccess(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailed(databaseError);
            }
        });
    }

    //Find list of authors titles
    public void getAuthors(final firebaseDataListener listener) {
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


    /**
     * Method to return the locations for a particular
     * book title on the database
     *
     * @param title    of the book
     * @param listener to return database
     */
    public void getBookPlaces(String title, final firebaseDataListener listener) {
        listener.onStart();

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

    /**
     * Returns the data of Locations for all books by a specified author
     *
     * @param author   name of author
     * @param listener
     */
    public void getBooksByAuthor(String author, final firebaseDataListener listener) {
        listener.onStart();
        Log.d(TAG, author);
        //  FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("places/");

        final Query recentQuery = myRef.orderByChild("author").equalTo(author);

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

    /**
     * Method to return locations within a certain range of longitude either side of the users location
     *
     * @param listener
     */
    public void nearbyPlacesLongitude(final firebaseDataListener listener) {
        Double userLong = Constants.lastLocation.getLongitude();

        Double maxLong = userLong + 0.03;
        Double minLong = userLong - 0.03;

        listener.onStart();

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

    /**
     * Method to return locations within a certain range of latitude
     * either side of the users location
     *
     * @param listener
     */
    public void nearbyPlacesLatitude(final firebaseDataListener listener) {
        Constants constants = Constants.getInstance();
        Double userLat = Constants.lastLocation.getLatitude();


        Double maxLat = userLat + 0.01;
        Double minLat = userLat - 0.01;

        DatabaseReference myRef = database.getReference("places/");
        final Query latitudeQuery = myRef.orderByChild("latitude").startAt(minLat).endAt(maxLat);

        latitudeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
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

    /**
     * Uploads a file to the Firebase database
     *
     * @param name     of file user has chosen
     * @param jsonfile containing details of users collection and places visited
     */
    public void uploadSaveSelection(String name, String jsonfile) {

        //get logged in users unique FirebaseAuth id
        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        DatabaseReference mRef = database.getReference();
        DatabaseReference childRef = mRef.child("user"); //Accesses the users folder of the Firebase database
        DatabaseReference userID = childRef.child(UID);//Creates/accesses a child in the NoSQL database with the Firebase AuthID
        userID.child(name).setValue(jsonfile);


    }

    /**
     * Returns all the users saved files
     *
     * @param listener
     */
    public void getUsersLists(final firebaseDataListener listener) {

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("user/" + UID);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

    ///////Methods that were used to upload locations from the sample data json file, could be used if functionality was added to have users generate locations within app////

    /**
     * Fetches the number of locations currently in the Firebase database, needed so any uploaded locations
     * are added at the with correct key to maintain array
     *
     * @param listener
     */
    public void currentArrayLength(final firebaseDataListener listener) {

        DatabaseReference mRef = database.getReference();
        final DatabaseReference childRef = mRef.child("places");
        //Query to determine the number of items in the array
        final Query query = mRef.orderByKey().startAt("places").limitToFirst(1);

        final ChildEventListener eventListener;
        query.addChildEventListener(eventListener = new ChildEventListener() {
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
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    /**
     * Add a location to the FirebaseDatabase
     *
     * @param placeObject
     * @param arrayPos
     */
    public void loadInfo(PlaceObject placeObject, int arrayPos) {

        DatabaseReference mRef = database.getReference();
        DatabaseReference childRef = mRef.child("places");
        DatabaseReference places = childRef.child(String.valueOf(arrayPos));
        places.child("author").setValue(placeObject.getAuthorName());
        places.child("db_key").setValue(places.push().getKey());
        places.child("location").setValue(placeObject.getLocation());
        places.child("latitude").setValue(placeObject.getLatitude());
        places.child("longitude").setValue(placeObject.getLongitude());
        places.child("description").setValue(placeObject.getLongDescription());
        places.child("title").setValue(placeObject.getBookTitle());
        places.child("quote").setValue(placeObject.getAssociatedQuote());

    }

}
