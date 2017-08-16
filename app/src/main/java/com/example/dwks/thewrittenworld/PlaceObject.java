package com.example.dwks.thewrittenworld;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 22/07/2017.
 */

public class PlaceObject implements Parcelable, Comparable<PlaceObject>, ClusterItem {

    private static final String TAG = PlaceObject.class.getSimpleName();
    //Instance vaiables
    private String location, authorFirstName, authorSecondName, imageURI;
    private String longDescription = "description";
    private String associatedQuote = "test quote";
    private String bookTitle = "default";
    private double latitude, longitude;
    private boolean visited = false; //mark if have visited or not
    private JSONObject jsonObject = null;
    private LatLng latLng;
    private String db_key ="test";


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
        String authorname = dataSnapshot.child("author").getValue().toString();
        String[] names = authorname.split(" ");
        authorFirstName = names[0];
        authorSecondName = names[1];
        db_key = dataSnapshot.child("db_key").getValue().toString();
        latLng = new LatLng(latitude, longitude);
        location = dataSnapshot.child("location").getValue().toString();
        if (dataSnapshot.child("quote").exists()) {
            associatedQuote = dataSnapshot.child("quote").getValue().toString();
        }

    }

    public PlaceObject(Parcel parcel) {
        db_key = parcel.readString();
        bookTitle = parcel.readString();
        authorFirstName = parcel.readString();
        authorSecondName = parcel.readString();
        location = parcel.readString();
        longDescription = parcel.readString();
        associatedQuote = parcel.readString();
        longitude = parcel.readDouble();
        latitude = parcel.readDouble();
        String visitCheck = parcel.readString();
        if (visitCheck.equals("true")){
            visited = true;
        }
        else visited = false;

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
            authorSecondName = names[1];
            latLng = new LatLng(latitude, longitude);
            db_key = jsonObject.getString("db_key");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLocation() {
        return location;
    }


    public String getBookTitle() {
        return bookTitle;
    }


    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public String getAuthorSecondName() {
        return authorSecondName;
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


    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return location;
    }

    @Override
    public String getSnippet() {
        return bookTitle;
    }

    ///////////////////////////////////////////////////////////////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(db_key);
        parcel.writeString(bookTitle);
        parcel.writeString(authorFirstName);
        parcel.writeString(authorSecondName);
        parcel.writeString(location);
        parcel.writeString(longDescription);
        parcel.writeString(associatedQuote);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeString((String.valueOf(visited)));


    }

    public static final Parcelable.Creator<PlaceObject> CREATOR = new Parcelable.Creator<PlaceObject>() {

        @Override
        public PlaceObject createFromParcel(Parcel parcel) {
            return new PlaceObject(parcel);
        }

        @Override
        public PlaceObject[] newArray(int i) {
            return new PlaceObject[i];
        }
    };
}
