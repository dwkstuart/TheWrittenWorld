package com.example.dwks.thewrittenworld;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by User on 22/07/2017.
 */

public class PlaceObject {

    private static final String TAG = PlaceObject.class.getSimpleName();
    //Instance vaiables
    private int id; //Database unique ID
    private String header, bookTitle, snippet, authorFirstName, autherSecondName, longDescription, associatedQuote, imageURI;
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

    public String getDb_key() {
        return db_key;
    }

    private void initialisePOIObject() {


        try {
            bookTitle = jsonObject.getString("title");
            latitude = jsonObject.getDouble("latitude");
            longitude = jsonObject.getDouble("longitude");
            snippet = jsonObject.getString("location");
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




    public int getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getSnippet() {
        return snippet;
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
        try {
            jsonObject.getString("latitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latitude;
    }

    public double getLongitude() {
        try {
            jsonObject.getDouble("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

}
