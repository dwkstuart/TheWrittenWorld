package com.example.dwks.thewrittenworld;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by User on 21/07/2017.
 */

//Creates a list of point of view objects to be used in the list view and for creating markers in the map view//

public class PlacesListCreator {


    private ArrayList<PlaceObject> placeObjectArrayList = new ArrayList<PlaceObject>();
    private String inputString;

    public PlacesListCreator(String jsonInput){
        inputString = jsonInput;
        convertToList();
    }

    private void convertToList(){
        //convert/ initial input string into JsonArray
        JSONArray inputJsonArray = null;
        try {
            inputJsonArray = new JSONArray(inputString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i<inputJsonArray.length()-1; i++){
            JSONObject initalObject = null;
            try {
                initalObject = inputJsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PlaceObject poi = new PlaceObject(initalObject);
            placeObjectArrayList.add(poi);
        }

    }

    public ArrayList<PlaceObject> getPointOfInterestObjects() {
        return placeObjectArrayList;
    }
}
