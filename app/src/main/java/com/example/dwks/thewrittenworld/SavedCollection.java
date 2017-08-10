package com.example.dwks.thewrittenworld;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by User on 09/08/2017.
 */

public class SavedCollection implements Parcelable{

    private String listName;
    private int numPlaces;
    private Set<PlaceObject> selection = new TreeSet<>();
    private int numberVisited;

    public SavedCollection(String listName, String jsonFromDB) {
        Gson gson = new Gson();
        this.listName = listName;
        Set<PlaceObject> set = gson.fromJson(jsonFromDB, new TypeToken<TreeSet<PlaceObject>>() {}.getType());
        selection = set;
        numPlaces = set.size();
        for (PlaceObject object: set){
            if(object.isVisited()){
                numberVisited++;
            }
        }




    }

    protected SavedCollection(Parcel in) {
        listName = in.readString();
        numPlaces = in.readInt();
        numberVisited = in.readInt();
        selection = (Set<PlaceObject>) in.readSerializable();
    }

    public static final Creator<SavedCollection> CREATOR = new Creator<SavedCollection>() {
        @Override
        public SavedCollection createFromParcel(Parcel in) {
            return new SavedCollection(in);
        }

        @Override
        public SavedCollection[] newArray(int size) {
            return new SavedCollection[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(listName);
        parcel.writeInt(numPlaces);
        parcel.writeInt(numberVisited);
    }
}
