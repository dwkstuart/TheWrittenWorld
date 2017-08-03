package com.example.dwks.thewrittenworld;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 22/07/2017.
 */

public class PlaceObject implements Comparable<PlaceObject> {

    private static final String TAG = PlaceObject.class.getSimpleName();
    //Instance vaiables
    private int id; //Database unique ID
    private String header, location, authorFirstName, autherSecondName, longDescription, associatedQuote, imageURI;
    private String bookTitle = "default";
    private double latitude, longitude;
    private boolean visited = false; //mark if have visited or not
    private JSONObject jsonObject;
    private LatLng latLng;
    private String db_key;


    public PlaceObject(JSONObject input){
        //Best to take JSON jsonObject here?
        jsonObject = input;
        this.initialisePOIObject();
    }



    //Constructor for getting place object from Firebase query
    public PlaceObject(DataSnapshot dataSnapshot){
        bookTitle = dataSnapshot.child("title").getValue().toString();
        latitude = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
        longitude = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
        String authorname = dataSnapshot.child("author").toString();
        String[] names = authorname.split(" ");
        authorFirstName = names[0];
        autherSecondName = names[1];
        db_key = dataSnapshot.child("db_key").getValue().toString();
        latLng = new LatLng(latitude, longitude);
        location = dataSnapshot.child("location").getValue().toString();


    }

    public String getDb_key() {
        return db_key;
    }

    private void initialisePOIObject() {


        try {
            bookTitle = jsonObject.getString("title");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            location = jsonObject.getString("location");
            String authorname = jsonObject.getString("author");
            String[] names = authorname.split(" ");
            authorFirstName = names[0];
            autherSecondName = names[1];
            latLng = new LatLng(latitude, longitude);
            db_key = jsonObject.getString("db_key");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLocation() {
        return location;
    }


    public int getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getBookTitle() {
        return bookTitle;
    }



    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public String getAutherSecondName() {
        return autherSecondName;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getAssociatedQuote() {
        return associatedQuote;
    }

    public String getImageURI() {
        return imageURI;
    }

    public double getLatitude() {

        return latitude;
    }

    public double getLongitude() {

        return longitude;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(Boolean check){
        visited = check;
    }

    public LatLng getLatLng() {
        return latLng;
    }



    @Override
    public int compareTo(@NonNull PlaceObject other) {

       int compare = db_key.compareTo(other.db_key);
        return compare;
    }



}
