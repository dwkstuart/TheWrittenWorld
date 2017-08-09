package com.example.dwks.thewrittenworld;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by User on 09/08/2017.
 */

public class SavedCollection {

    private String listName;
    private int numPlaces;
    private Set<PlaceObject> selection = new TreeSet<>();
    private int numberVisited;

    public SavedCollection(String listName, String jsonFromDB) {
        Gson gson = new Gson();
        this.listName = listName;
        Set<PlaceObject> set = gson.fromJson(jsonFromDB, new TypeToken<TreeSet<PlaceObject>>() {}.getType());
        numPlaces = set.size();
        for (PlaceObject object: set){
            if(object.isVisited()){
                numberVisited++;
            }
        }




    }

    public String getListName() {
        return listName;
    }

    public int getNumPlaces() {
        return numPlaces;
    }

    public Set<PlaceObject> getSelection() {
        return selection;
    }

    public int getNumberVisited() {

        return numberVisited;
    }
}
